package com.cyhd.service.impl.doubao;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.cyhd.common.util.Helper;
import com.cyhd.common.util.Pair;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil; 
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.doubao.OrderProductMapper;
import com.cyhd.service.dao.db.mapper.doubao.ProductActivityRuleMapper;
import com.cyhd.service.dao.po.doubao.OrderProduct;
import com.cyhd.service.dao.po.doubao.OrderProductLottery;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.vo.doubao.OrderProductVo;
import com.schooner.MemCached.MemcachedItem;


@Service
public class OrderProductService extends BaseService {
	@Resource
	private OrderProductMapper orderProductMapper; 
	
	@Resource
	private ProductActivityRuleMapper productActivityRuleMapper; 
	
	@Resource
	private ProductActivityCalculateService activityCalculateService;

	@Resource
	private ProductService productService;
	@Resource
	private OrderService orderService;
	
	@Resource
	private UserService userService;
	
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;

	/**
	 * 根据Id获取订单商品信息
	 * @param id
	 * @return
	 */
	public OrderProduct getOrderProductById(int id) {
		String key = CacheUtil.getOrderProductKey(id);
		OrderProduct op = (OrderProduct)memcachedCacheDao.get(key);
		if(op == null) {
			op = orderProductMapper.getOrderProductById(id);
			if (op != null){
				memcachedCacheDao.set(key, op, Constants.minutes_millis * 5);
			}
		}
		return op;
	}
	/**
	 * 更新订单商品缓存
	 * 
	 * @param id
	 */
	public boolean removeOrderProductCache(int id) {
		String key = CacheUtil.getOrderProductKey(id);
		memcachedCacheDao.remove(key);
		return true;
	}

	/**
	 * 根据用户和活动id获取用户所有的夺宝号
	 * @param userId
	 * @param productActivityId
	 * @return
	 */
	public List<OrderProductLottery> getLotteryByUserAndProductActivityId(int userId, int productActivityId, int start, int limit){
		if (limit == 0){
			return orderProductMapper.getAllLotteryByUserAndProductActivityId(userId, productActivityId);
		}else {
			return orderProductMapper.getLotteryByUserAndProductActivityId(userId, productActivityId, start, limit);
		}
	}

	/**
	 * 统计当前商品购买记录
	 * @return
	 */
	public int countProductBuyHistory(int productActivityId) {
		return orderProductMapper.countProductBuyHistory(productActivityId);
	} 
	
	/**
	 * 获取当前活动商品购买记录
	 * @return
	 */
	public List<OrderProduct> getOrderProductByActivityId(int productActivityId) {
		return orderProductMapper.getOrderProductByActivityId(productActivityId);
	}
		
	/**
	 * 获取活动商品购买记录
	 * @return
	 */
	public List<OrderProductVo> getOrderProductByActivityId(int productActivityId, int start, int size) {
		List<OrderProduct> orderProductList  =  orderProductMapper.getOrderProductListByActivityId(productActivityId,start,size);
		
		List<OrderProductVo> l = new ArrayList<OrderProductVo>();
		String dateKey = new String();
		for(OrderProduct op : orderProductList) {
			OrderProductVo vo = new OrderProductVo();
			
			String temp = DateUtil.format(op.getCreatetime(), "yyyyMMdd");
			vo.setDisplayDate(false);
			if(dateKey.isEmpty()) {
				dateKey = temp;
				vo.setDisplayDate(true);
			} else if(!dateKey.equals(temp)){
				if(l.size() > 0) {
					dateKey = temp;
					vo.setDisplayDate(true);
				}
			}
			if(op.getUser_id() > 0)
				vo.setUser(userService.getUserById(op.getUser_id()));
			vo.setOrderProduct(op);
			l.add(vo);
		}
		return l;
	} 



	/**
	 * 更新分享数据
	 * @param id
	 * @return
	 */
	public boolean updateShare(int id, int userId) {
		return orderProductMapper.updateShare(id, userId) > 0 && removeOrderProductCache(id);
	}

	/**
	 * 增加用户的夺宝记录，同时创建夺宝id
	 * 
	 * @param op
	 * @return
	 */
	public boolean createOrderProduct(OrderProduct op) {
		if(orderProductMapper.createOrderProduct(op) > 0) {
			return createOrderProductLottery(op);
		}
		return false;
		
	}

	/**
	 * 创建订单商品夺宝号<br/>
	 * 从memcached缓存里面取出待分配的夺宝号码列表，然后随机给用户分配<br/>
	 * 使用了memcached的CAS操作，保证取出--放回的操作是一致的
	 * 
	 * @param op
	 * @return
	 */
	public boolean createOrderProductLottery(OrderProduct op) {
		Pair<Long, List<Integer>> pair  = getRemainDuobaoNumber(op.getProduct_activity_id());
		List<Integer> lotteryNumberList = pair.getSecond();

		//如果剩余的夺宝号码不足，则直接返回false
		if (op.getNumber() > lotteryNumberList.size()){
			return false;
		}

		List<OrderProductLottery> oplList = new ArrayList<>();
		for(int i = 0; i< op.getNumber(); i++) {
			OrderProductLottery opl = new OrderProductLottery();
			opl.setOrder_sn(op.getOrder_sn());
			opl.setUser_id(op.getUser_id());
			opl.setOrder_product_id(op.getId());
			opl.setProduct_activity_id(op.getProduct_activity_id());

			//随机获取号码后，同时会删除当前获取的号码
			opl.setNumber(createDoubaoNumber(op.getProduct_activity_id(), lotteryNumberList));
			oplList.add(opl);
		}

		//继续缓存剩余的夺宝号码
		String key = CacheUtil.getProductLotteryNumberKey(op.getProduct_activity_id());
		boolean cacheSuccess = memcachedCacheDao.cas(key, lotteryNumberList, pair.getFirst());

		//如果CAS操作失败，则重新操作一次
		if(cacheSuccess) {
			//CAS操作成功后才保存到数据库
			return orderProductMapper.createOrderProductLottery(oplList) > 0;
		} else {
			createOrderProductLottery(op);
		}
		return false; 
	}

	/**
	 * 获取待分配的夺宝号码列表<br/>
	 * 首先从memcache中获取，如果获取不到，则重新生成夺宝号，并放到memcache<br/>
	 * 操作memcache使用了CAS方法，保证取出--放回操作是一致的，不会被别的并发操作打乱
	 * @param productActivityId
	 * @return
	 */
	protected Pair<Long, List<Integer>> getRemainDuobaoNumber(int productActivityId) {
		String key = CacheUtil.getProductLotteryNumberKey(productActivityId);
		MemcachedItem mi = memcachedCacheDao.gets(key);
		if( mi == null || mi.getValue() == null || ((Collection)mi.getValue()).size() == 0) {
			activityCalculateService.cacheLotteryNumber(productActivityId);
			mi = memcachedCacheDao.gets(key);
		}

		//最后使用new ArrayList, 因为后面有remove操作
		List<Integer> lotteryNumberList = new ArrayList<>((Collection)mi.getValue());
		long casUnique = mi.getCasUnique();

		return new Pair<>(casUnique, lotteryNumberList);
	}

	/**
	 * 创建夺宝号码
	 * @param productActivityId
	 * @return
	 */
	protected int createDoubaoNumber(int productActivityId, List<Integer> lotteryNumberList) {
		int index = ThreadLocalRandom.current().nextInt(lotteryNumberList.size());
	    int value = lotteryNumberList.get(index);

		//删除指定位置的号码
		lotteryNumberList.remove(index);

		return value;
	}


	/**
	 * 获取已生成的夺奖号码
	 * 
	 * @param productActivityId
	 * @return
	 */
	public Set<Integer> getOrderProductLottery(int productActivityId) {
		List<OrderProductLottery> list = orderProductMapper.getLotteryByProductActivityId(productActivityId);

		Set<Integer> lotterySet = new HashSet<>();
		if(list != null && list.size() > 0) {
			for(OrderProductLottery opl : list) {
				lotterySet.add(opl.getNumber());
			}
		}
		return lotterySet;
	}

	/**
	 * 根据活动及夺宝号码获取夺宝号信息
	 * @param productActivityId
	 * @param number
	 * @return
	 */
	public OrderProductLottery getProductActivityLotteryByNumber(int productActivityId, int number) {
		return orderProductMapper.getProductActivityLotteryByNumber(productActivityId, number);
	}

	/**
	 * 根据活动列表，获取用户参与活动的所有记录
	 * @return
	 */
	public List<OrderProduct> getUserOrderProductByActivities(int userId, List<Integer> activityList){
		if (activityList != null && activityList.size() > 0){
			String actStr = Helper.join(activityList, ',');

			return orderProductMapper.getUserOrderProductByActivities(userId, actStr);
		}
		return Collections.EMPTY_LIST;
	}

	/**
	 * 根据活动id列表，获取用户参与每个活动的记录，对用户参与记录进行分组，合并夺宝次数，合并后的活动记录为中奖记录或者最后一条夺宝记录
	 * @param userId
	 * @param activityList
	 * @return
	 */
	public Map<Integer, OrderProduct> getUserOrderProductGroupByActivities(int userId, List<Integer> activityList){
		List<OrderProduct> orderProductList = getUserOrderProductByActivities(userId, activityList);

		Map<Integer, OrderProduct> productMap = new HashMap<>();
		for (OrderProduct op : orderProductList){
			int actId = op.getProduct_activity_id();
			OrderProduct orderProduct = productMap.get(actId);

			if (orderProduct == null){
				productMap.put(actId, op);
			}else { //如果是已中奖的订单，则返回已中奖的订单信息，否则返回最近的一条
				if (op.isLottery() || (!orderProduct.isLottery() && op.getId() > orderProduct.getId())){
					op.setNumber(op.getNumber() + orderProduct.getNumber());
					orderProduct = op;
				}else {
					orderProduct.setNumber(op.getNumber() + orderProduct.getNumber());
				}
				productMap.put(actId, orderProduct);
			}
		}
		return productMap;
	}


	/**
	 * 设置某个夺宝订单已中奖，调用后需要清缓存
	 * @param id
	 * @return
	 */
	public boolean updateLotteryById(int id) {
		return orderProductMapper.updateLotteryById(id) > 0 && removeOrderProductCache(id);
	}
	/**
	 * 更新订单商品已揭晓的状态，调用后记得清缓存
	 * @param productActivityId
	 * @return
	 */
	public boolean updateAnnouncedByActivityId(int productActivityId) {
		return orderProductMapper.updateAnnouncedByActivityId(productActivityId) > 0;
	}

	/**
	 * 更新订单商品过期信息，调用后需要清缓存
	 * @param productActivityId
	 * @return
	 */
	public boolean updateExpireByProductActivityId(int productActivityId) {
		return orderProductMapper.updateExpireByProductActivityId(productActivityId) > 0;
	}
	/**
	 * 更新订单商品退款，调用后需要清缓存
	 * @param id
	 * @return
	 */
	public boolean updateRefundById(int id) {
		return orderProductMapper.updateRefundById(id) > 0 && removeOrderProductCache(id);
	}
	/**
	 * 获取用户总共购买的人次数，可能一次会买多次，会进行sum操作
	 * @param userId
	 * @param productActivityId
	 * @return
	 */
	public int countUserBuy(int userId, int productActivityId) {
		return orderProductMapper.countUserBuy(userId, productActivityId);
	}
	
	/**
	 * 更新订单商品信息
	 * @param id
	 * @return
	 */
	public boolean saveConsignee(int userId, int id, String consignee, String consignee_mobile,String address) {
		return orderProductMapper.saveConsignee(userId, id, consignee, consignee_mobile,address) > 0 && removeOrderProductCache(id);
	}
	
	/**
	 * 确认收货
	 * @param id
	 * @return
	 */
	public boolean confirmGoods(int userId, int id) {
		return orderProductMapper.confirmGoods(userId, id) > 0 && removeOrderProductCache(id);
	}
 }

