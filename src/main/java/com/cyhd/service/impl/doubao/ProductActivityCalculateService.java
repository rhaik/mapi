package com.cyhd.service.impl.doubao;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.doubao.ProductActivityMapper;
import com.cyhd.service.dao.db.mapper.doubao.ProductActivityRuleMapper;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.doubao.*;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.impl.SmsService;
import com.cyhd.service.impl.UserMessageService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.CacheUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * 开奖计算相关的服务
 */
@Service
public class ProductActivityCalculateService extends BaseService {

	/**
	 * 计算获奖号码时，获取历史订单的数量
	 */
	public final static int HISTORY_ORDER_NUM = 10;

	@Resource
	private ProductActivityMapper productActivityMapper;

	@Resource
	private ProductService productService;  

	@Resource
	private ProductActivityRuleMapper productActivityRuleMapper;
	
	@Resource
	private UserService userService;
	
	@Resource
	private OrderProductService orderProductService; 
	
	@Resource
	private OrderService orderService;

	@Resource
	ThirdShishicaiService thirdShishicaiService;

	@Resource
	ProductActivityService activityService;

	@Resource
	UserDuobaoCoinService duobaoCoinService;

	@Resource
	private UserMessageService userMessageService;

	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;

	private volatile boolean isAnnouncing = false;

	@Resource
	private SmsService smsService;

	/**
	 * 一元夺宝开奖操作，通过定时任务执行
	 */
	public void announceProductActivity() {
		try{
			if(isAnnouncing) {
				logger.warn("update product activity isAnnouncing is running!");
				return;
			}
			isAnnouncing = true;
			logger.info("start announce product activity!");

			//获取待开奖的活动列表
			List<ProductActivity> paList = activityService.getSoldoutProductList();
			if(paList != null && paList.size() > 0){
				logger.info("load update product activity size:{}", paList.size());
				
				for(ProductActivity pa : paList) {
					boolean flag = true;

					if(pa.getStatus() == ProductActivity.STATUS_DOING ) { //如果是进行中，设置为待揭晓
						boolean success = updateAnnounced(pa.getId(), pa.getProduct_id());
						logger.info("product activity id:{} , set Announced, result: {}", pa.getId(), success ? "success" : "fail");
					} else if(pa.getStatus() == ProductActivity.STATUS_ANNOUNCED) { //如果是揭晓中，则检查时时彩数据，进行揭晓
						boolean success = announcedProductActivity(pa);
						logger.info("product activity id:{} announced {}", pa.getId(), success ? "success" : "fail");

						flag = success;
					} else {
						flag = false;
					}

					if(flag) {
						//清除活动商品缓存
						activityService.deleteProductActivityCache(pa.getId(), pa.getProduct_id());
					}
				}
			}
			logger.info("end announce product activity!");
		}catch(Exception e){
			logger.error("ProductActivityService UpdateProductActivityStatus error:{}", e);
		}finally {
			isAnnouncing = false;
		}
	}


	/**
	 * 定期生成新的夺宝活动<br/>
	 * 一般开奖之后，会自动生成下一期，所以不需要执行太频繁，主要是处理生成了新的活动规则后，上新的夺宝活动
	 */
	public void buildProductActivity(){
		logger.info("start build product activity");
		//按规则生成新活动商品
		List<ProductActivityRule> parlist = productService.getNotDoingProductActivityRuleList();
		if(parlist!=null && parlist.size() > 0) {
			logger.info("build product activity, valid rules:{}", parlist.size());

			for(ProductActivityRule par:parlist) {
				ProductActivity pa = this.buildProductActivity(par);
				if(pa != null) {
					logger.info("buildProductActivity product_id:{},ruleId:{},productActivityId:{} success!", par.getProduct_id(), par.getId(), pa.getId());
				} else {
					logger.info("buildProductActivity product_id:{},ruleId:{} fail!", par.getProduct_id(), par.getId());
				}
			}
		}
	}


	/**
	 * 获取过期的活动，逐个处理<br/>
	 * 本操作通过定时任务进行执行
	 * @return
	 */
	public void expireProductActivity() {
		logger.info("start expire product activity");
		List<ProductActivity> activityList = getExpiredProductActivity();
		if (activityList != null && activityList.size() > 0){
			logger.info("expire product activity, size:{}", activityList.size());
			for (ProductActivity pv : activityList){
				logger.info("expire product activity now, activity:{}", pv);
				doExpireProductActivity(pv);
			}
		}
	}

	/**
	 * 根据当前的期数，自动生成下一期，多次调用无害，会检测当前是不是进行中
	 * @param productActivityId	当前期数
	 * @return
	 */
	public ProductActivity buildNextPeriod(int productActivityId) {
		ProductActivity currentActivity = productActivityMapper.getProductActivityById(productActivityId);
		return buildNextPeriod(currentActivity);
	}
	/**
	 * 生成下一期
	 * @param currentActivity	当前期数
	 * @return
	 */
	public ProductActivity buildNextPeriod(ProductActivity currentActivity) {
		if(!currentActivity.isFull()) {
			return currentActivity;
		}

		//为当前的夺宝活动生成订单记录
		generateOrderHistory(currentActivity);

		//当前是否有进行中的活动
		ProductActivity pa = activityService.getLatestProductActivityByProductId(currentActivity.getProduct_id());
		if(pa != null &&  (pa.getStatus() == ProductActivity.STATUS_DOING  && !pa.isFull())) {
			return pa;
		}

		//生成新的活动
		ProductActivityRule rule = productActivityRuleMapper.getProductActivityRuleByProductId(currentActivity.getProduct_id());
		if (rule != null) {
			return buildProductActivity(rule);
		}
		return null;
	}


	/**
	 * 为夺宝订单导出历史记录，用来计算最终的夺宝号码
	 * @param currentActivity
	 */
	protected void generateOrderHistory(ProductActivity currentActivity){
		//如果当前的活动未导出历史订单信息，则执行导出
		if(!currentActivity.isHaveHistoryOrder()) {
			//根据最近的10个订单，生成历史订单，便于后面生成夺宝号码
			List<Order> orderList = orderService.getLatestOrder(HISTORY_ORDER_NUM);
			if(orderList != null && orderList.size() > 0) {
				List<OrderHistory> ohList = new ArrayList<>();
				for(Order o: orderList) {
					User u = userService.getUserById(o.getUser_id());
					OrderHistory oh = new OrderHistory();
					oh.setOrder_sn(o.getOrder_sn());
					oh.setOrderTimeAndValue(o.getCreatetime(), o.getOrder_sn());
					oh.setProduct_activity_id(currentActivity.getId());
					oh.setUser_id(o.getUser_id());
					oh.setUser_name(u.getName());
					ohList.add(oh);
				}

				//更新历史订单信息，保证只进行一次
				if(updateHistoryOrder(currentActivity.getId())){
					orderService.createOrderHistory(ohList);

					//清除缓存
					activityService.deleteProductActivityCache(currentActivity.getId(), currentActivity.getProduct_id());
				}
			}
		}
	}

	/**
	 * 按规则生成活动商品
	 * 
	 * @param rule
	 * @return
	 */
	public ProductActivity buildProductActivity(ProductActivityRule rule) {
		if(!rule.isAllowBuildNextPeriod()) {
			return null;
		}
		if(productActivityRuleMapper.updateLockStock(rule.getId()) > 0) {
			//生成一个新的活动商品
			ProductActivity newProductActivity = new ProductActivity();
			newProductActivity.setProduct_id(rule.getProduct_id());
			newProductActivity.setActivity_rule_id(rule.getId());
			newProductActivity.setProduct_name(rule.getName());
			newProductActivity.setProduct_lable(rule.getLable());
			newProductActivity.setProduct_number(getNextNumber(rule.getProduct_id()));
			newProductActivity.setPrice(rule.getPrice());
			newProductActivity.setPresell(rule.getPresell());
			newProductActivity.setStart_time(GenerateDateUtil.getCurrentDate());
			newProductActivity.setEnd_time(new Date(System.currentTimeMillis() + Constants.PRODUCT_ACTIVITY_EXPIRE_TIME));
			newProductActivity.setCreatetime(GenerateDateUtil.getCurrentDate());
			newProductActivity.setNumber(rule.getNumber());
			newProductActivity.setBuy_number(0);
			newProductActivity.setMin_buy_number(rule.getMin_buy_number());
			newProductActivity.setVendor_price(rule.getVendor_price());
			newProductActivity.setStatus(1);
			if(productActivityMapper.add(newProductActivity) > 0) {
				//生成夺宝号码
				cacheLotteryNumber(newProductActivity.getId());

				//清除缓存
				activityService.deleteProductActivityCache(newProductActivity.getId(), rule.getProduct_id());
				return newProductActivity;
			}
			
		} 
		return null;
	}


	/**
	 * 获取下一期编号
	 * 
	 * @param productId
	 * @return
	 */
	public int getNextNumber(int productId) {
		String key = "PRODUCT_NUMBER_" + productId;
		Integer value = (Integer)memcachedCacheDao.get(key);
		int nextNumber = 1;
		if(value == null) {
			Integer max = productActivityMapper.getMaxNumberByProductId(productId);
			if(max == null) {
				nextNumber = 1;
			} else {
				nextNumber = max + 1;
			}
		} else {
			nextNumber = value + 1;
		} 
		memcachedCacheDao.set(key, nextNumber);
		return nextNumber;
	}
	
	/**
	 * 更新揭晓中
	 * 
	 * @param activityId
	 * @return
	 */
	public boolean updateAnnounced(int activityId, int productId) {
		//获取最新一期未开奖时时彩的期号，需要考虑隔天的情况
		int period = thirdShishicaiService.getNextPeriod();
		boolean flag = productActivityMapper.updateAnnounced(activityId, period) > 0;
		if (flag) {
			//清除缓存
			activityService.deleteProductActivityCache(activityId, productId);

			//保险期间，再调用一次生成下一期的逻辑
			buildNextPeriod(activityId);
		}
		return flag;
	}

	/**
	 * 更新过期商品活动
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateExpire(int id) {
		return productActivityMapper.updateExpire(id) > 0;
	}

	/**
	 * 更新退款
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateRefund(int id) {
		return productActivityMapper.updateRefund(id) > 0;
	}

	/**
	 * 更新夺奖信息
	 * 
	 * @param id
	 * @return
	 */
	public boolean setLotterySuccess(int id, int userId, int lotteryNumber,long lotteryOrder, int orderProductId, int totalBuy) {
		return productActivityMapper.setLotterySuccess(id, userId, lotteryNumber, lotteryOrder, orderProductId, totalBuy) > 0;
	}

	/**
	 * 更新历史订单记录
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateHistoryOrder(int id) {
		return productActivityMapper.updateHistoryOrder(id) > 0;
	}
	

	/**
	 * 缓存中奖号码
	 * 
	 * @param productActivityId
	 * @return
	 */
	public void cacheLotteryNumber(int productActivityId) {
		Set<Integer> lotteryNumberSet = new HashSet<>();
		Set<Integer> lotterySet = orderProductService.getOrderProductLottery(productActivityId);

		ProductActivity activity = productActivityMapper.getProductActivityById(productActivityId);
		int number = activity.getNumber();
		for(int i = 0; i < number; i++) {
			int num = 10000001 + i;
			if(!lotterySet.contains(num)) {
				lotteryNumberSet.add(num);
			}
		}

		//缓存30天
		String key = CacheUtil.getProductLotteryNumberKey(productActivityId);
		memcachedCacheDao.set(key, lotteryNumberSet, Constants.day_millis * 30);
	}
	/**
	 * 揭晓活动商品<br/>
	 * 本方法未清除缓存，需要在调用处清除缓存
	 * 
	 * @param pv
	 * @return
	 */
	@Transactional
	public boolean announcedProductActivity(ProductActivity pv) {
		ThridShishicai ssc = thirdShishicaiService.getByPeriods(pv.getShishicai());

		//还未获取到时时彩号码，揭晓失败
		if(ssc == null || StringUtil.isBlank(ssc.getLottery_number())) {
			logger.warn("announce product activity, shishicai is empty. activity id:{}, shishicai:{}", pv.getId(), ssc);
			return false;
		}

		//如果等待的时间不足10分钟(这里宽松一点，9分钟)，则暂不揭晓。
		if (pv.getLotteryTimes().getTime() - GenerateDateUtil.getCurrentTime() > 90 * 1000){
			logger.warn("announce product activity, not delayed for 10min. activity id:{}", pv.getId());
			return false;
		}

		int shishicai = NumberUtil.safeParseInt(ssc.getLottery_number());

		//生成中奖号码
		int lotteryNumber = buildLotteryNumber(pv.getId(), pv.getNumber(), shishicai);
		 
		//获取夺奖订单商品信息
		OrderProductLottery opl= orderProductService.getProductActivityLotteryByNumber(pv.getId(), lotteryNumber);
		if (opl != null) {
			//获取用户夺宝的订单信息
			OrderProduct op = orderProductService.getOrderProductById(opl.getOrder_product_id());

			//获取用户总共参与次数
			int totalBuy = orderProductService.countUserBuy(op.getUser_id(), pv.getId());

			//更新夺奖信息 && //更新OrderProduct
			if(this.setLotterySuccess(pv.getId(), op.getUser_id(), lotteryNumber, op.getOrder_sn(), op.getId(), totalBuy) && orderProductService.updateLotteryById(op.getId())) {
				//将改夺宝活动的所有夺宝订单设置为已揭晓
				orderProductService.updateAnnouncedByActivityId(pv.getId());

				//修改商品已售库存
				productService.updateSellStock(pv.getProduct_id());

				//揭晓成功，发送消息给用户
				onUserDuobaoSuccess(op, pv);

				return true;
			} else {
				logger.info("update product:{} activity:{} lottery user:{},lotteryNumber:{} fail", op.getId(), pv.getId(), op.getUser_id(), lotteryNumber);
			}
		} else {
			logger.warn("product activity:{} lottery user not exist", pv.getId());
		}
		return false;
	}

	/**
	 * 用户夺宝成功的处理 <br/>
	 * 此时传过来的activity还没有中奖用户信息
	 * @param activity
	 */
	protected void onUserDuobaoSuccess(OrderProduct orderProduct, ProductActivity activity){
		try {
			User user = userService.getUserById(orderProduct.getUser_id());

			Product product = productService.getProductById(orderProduct.getProduct_id());
			userMessageService.addDuobaoSuccessMessage(user, product.getShortName());

			smsService.sendDuobaoSuccessSms(user, product.getShortName());

		}catch (Exception exp){
			logger.error("onUserDuobaoSuccess error", exp);
		}
	}

	/**
	 * 揭晓活动时，生成时时彩夺奖号码
	 * 
	 * @return
	 */
	public int buildLotteryNumber(int productActivityId, int totalNumber, int shishicai) {
		List<OrderHistory> orderList = orderService.getOrderHistoryList(productActivityId, HISTORY_ORDER_NUM);
		
		long totalTime = 0;
		if(orderList != null && orderList.size() > 0) {
			for(OrderHistory oh: orderList) {
				long formatTime = oh.getTime_value();
				logger.info("formatTime {}", formatTime);
				totalTime += formatTime;
			}
		}
		logger.info("totalTime {}", totalTime);
		int lotteryNumber = (int) ((totalTime+shishicai)%totalNumber + 10000001);
		logger.info("lotteryNumber {}", lotteryNumber);
		return lotteryNumber;
	}


	/**
	 * 获取已经过期的活动列表
	 * @return
	 */
	public List<ProductActivity> getExpiredProductActivity(){
		return productActivityMapper.getExpiredProductActivity();
	}


	/**
	 * 处理已经过期的活动，进行退款
	 * @param pv
	 * @return
	 */
	@Transactional
	protected void doExpireProductActivity(ProductActivity pv){
		//商品活动过期
		if(this.updateExpire(pv.getId())) {
			//订单商品过期
			orderProductService.updateExpireByProductActivityId(pv.getId());

			List<OrderProduct> opList = orderProductService.getOrderProductByActivityId(pv.getId());
			if(opList != null) {
				for(OrderProduct op : opList) {
					int amount = op.getNumber() * op.getPrice();
					if(duobaoCoinService.returnUserDuobaoBalance(op.getUser_id(), amount, op.getId()) ) {
						//退款处理
						orderProductService.updateRefundById(op.getId());
					} else {
						logger.info("updateExpire user:{} returnUserDoubaoBalance:{} fail", op.getUser_id(), amount);
					}
				}
				updateRefund(pv.getId());
				logger.info("doExpireProductActivity ProductActivityId:{} success", pv.getId());

				//清除缓存
				activityService.deleteProductActivityCache(pv.getId(), pv.getProduct_id());
			}
		} else {
			logger.info("doExpireProductActivity ProductActivityId:{} fail", pv.getId());
		}
	}
}
