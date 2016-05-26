package com.cyhd.service.impl.doubao;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.UserIncomeLogMapper;
import com.cyhd.service.dao.db.mapper.doubao.ProductActivityMapper;
import com.cyhd.service.dao.db.mapper.doubao.ProductActivityRuleMapper;
import com.cyhd.service.dao.po.doubao.*;
import com.cyhd.service.impl.BaseService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.vo.doubao.ProductActivityVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class ProductActivityService extends BaseService {


	//默认的待揭晓列表长度
	private static final int DEFAULT_ANNOUNCING_SIZE = 2;

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
	UserDuobaoCoinService duobaoCoinService;

	@Resource
	UserIncomeLogMapper userIncomeLogMapper;

	@Resource
	ThirdShishicaiService thirdShishicaiService;

	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;


	private volatile boolean isAnnouncing = false;

	//最新中奖的活动，缓存1分钟
	private LiveAccess<List<ProductActivityVo>> latestLotteryActivityList = null;

	//最近待揭晓的活动，缓存1分钟
	private LiveAccess<List<ProductActivityVo>> latestAnnouncingActivityList = null;

	/**
	 * 根据列表类型获取夺宝任务
	 *
	 * @param type
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ProductActivityVo> getProductActivityByType(int type, int start, int size) {
		List<ProductActivity> plist = new ArrayList<ProductActivity>();
		switch (type) {
			case ProductActivity.TYPE_ANNOUNCING:
				plist = productActivityMapper.getAnnouncingProductActivity(start, size);
				break;
			case ProductActivity.TYPE_LEASTRESIDUAL:
				plist = productActivityMapper.getLeastResidualProductActivity(start, size);
				break;
			case ProductActivity.TYPE_MAXPRICE:
				plist = productActivityMapper.getProductActivityOrderByMaxPrice(start, size);
				break;
			case ProductActivity.TYPE_MINPRICE:
				plist = productActivityMapper.getProductActivityOrderByMinPrice(start, size);
				break;
			default:
				plist = productActivityMapper.getProductActivityOrderByTime(start, size);
				break;
		}

		return plist.stream().map(this::getVoByProductActivity).collect(Collectors.toList());
	}

	/**
	 * 获取进行中或待揭晓状态已卖完的product列表
	 * @return
	 */
	public List<ProductActivity> getSoldoutProductList(){
		return productActivityMapper.getSoldoutProductList();
	}

	/**
	 * 根据id获取最新一期activity
	 * @param productId
	 * @return
	 */
	public ProductActivity getLatestProductActivityByProductId(int productId) {
		String key = CacheUtil.getLatestActivityKey(productId);
		ProductActivity activity = (ProductActivity)memcachedCacheDao.get(key);
		if (activity == null){
			activity = productActivityMapper.getLatestProductActivityByProductId(productId);

			if (activity != null) {
				memcachedCacheDao.set(key, activity, Constants.minutes_millis);
			}
		}
		return activity;
	}

	/**
	 * 获取最近待揭晓的记录列表，默认为2条记录，如果有揭晓中，但是不足2条的会，会再取剩余少的记录
	 * @return
	 */
	public List<ProductActivityVo> getLatestAnnouncingList(){
		if (latestAnnouncingActivityList == null || latestAnnouncingActivityList.getElement() == null){
			List<ProductActivityVo> activityVoList = getProductActivityByType(ProductActivity.TYPE_ANNOUNCING, 0, DEFAULT_ANNOUNCING_SIZE);

			//如果待揭晓的活动少于DEFAULT_ANNOUNCING_SIZE，则再取剩余较少的活动
			if (activityVoList.size() < DEFAULT_ANNOUNCING_SIZE){
				List<ProductActivityVo> leastRemainVoList = getProductActivityByType(ProductActivity.TYPE_LEASTRESIDUAL, 0, DEFAULT_ANNOUNCING_SIZE - activityVoList.size());
				activityVoList.addAll(leastRemainVoList);
			}

			latestAnnouncingActivityList = new LiveAccess<>(Constants.minutes_millis, activityVoList);
		}
		return latestAnnouncingActivityList.getElement();
	}

	/**
	 * 获取某一用户参与过的夺宝活动
	 * @param userId
	 * @param status  status=0 所有记录 status=1 进行中，包括待揭晓的  status=3 已揭晓但未中奖  status=10 已中奖
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ProductActivityVo> getUserJoinedActivities(int userId, int status, int start, int size){
		List<ProductActivity> activityList = productActivityMapper.getUserJoinedActivities(userId, status, start, size);
		return activityList.stream().map(this::getVoByProductActivity).collect(Collectors.toList());
	}

	/**
	 * 统计用户参与过的活动
	 * @return
	 */
	public int countUserJoinedActivites(int userId, int status){
		return productActivityMapper.countUserJoinedActivites(userId, status);
	}

	/**
	 * 更新购买数
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateBuyNumber(int id, int number) {
		return productActivityMapper.updateBuyNumber(id, number) > 0;
	}


	public ProductActivityVo getVoByProductActivity(ProductActivity pa){
		ProductActivityVo vo = new ProductActivityVo();
		vo.setProductActivity(pa);
		vo.setProduct(productService.getProductById(pa.getProduct_id()));

		//获取中奖的用户信息
		if(pa.getLottery_user() > 0) {
			vo.setUser(userService.getUserById(pa.getLottery_user()));
		}
		//中奖的订单商品
		if(pa.getLottery_order_product() > 0) {
			vo.setOrderProduct(orderProductService.getOrderProductById(pa.getLottery_order_product()));
		}
		return vo;
	}


	/**
	 * 根据夺宝活动ID获取商品信息
	 * @param activityId
	 * @return
	 */
	public ProductActivityVo getVoByActivityId(int activityId) {
		ProductActivity activity = getProductActivityById(activityId);
		return getVoByProductActivity(activity);
	}


	/**
	 * 清除活动商品缓存
	 * 
	 * @param activityId
	 */
	public void deleteProductActivityCache(int activityId, int productId) {
		memcachedCacheDao.remove(CacheUtil.getProductActivityKey(activityId));
		memcachedCacheDao.remove(CacheUtil.getLatestActivityKey(productId));

		latestAnnouncingActivityList = null;
		latestLotteryActivityList = null;
	}
	/**
	 * 根据ID获取商品信息
	 * @param activityId
	 * @return
	 */
	public ProductActivity getProductActivityById(int activityId) {
		String key = CacheUtil.getProductActivityKey(activityId);
		ProductActivity pv = (ProductActivity)memcachedCacheDao.get(key);
		if(pv == null) {
			pv = productActivityMapper.getProductActivityById(activityId);
			memcachedCacheDao.set(key, pv);
		}
		return pv;
	}
	/**
	 * 获取最新一期活动商品
	 * @return
	 */
	public ProductActivityVo getLastProductActivity() {
		String key = CacheUtil.getHotProductActivityKey();
		ProductActivityVo vo = (ProductActivityVo)memcachedCacheDao.get(key);
		if(vo == null) {
			ProductActivity pa = productActivityMapper.getLastProductActivity();
			vo = getVoByProductActivity(pa);
			memcachedCacheDao.set(key, vo);
		}
		return vo;
	}
	
	/**
	 * 统计往期揭晓记录
	 * @return
	 */
	public int countHistoryActivity(int productId) {
		return productActivityMapper.countHistoryActivity(productId);
	}

	/**
	 * 根据商品ID获取往期揭晓记录
	 * 
	 * @param productId
	 * @param start
	 * @param size
	 * @return
	 */
	public List<ProductActivityVo> getHistoryOrderActivityList(int productId,int start, int size) {
		List<ProductActivity> productActivityList = productActivityMapper.getHistoryActivityList(productId, start, size);
		return productActivityList.stream().map(this::getVoByProductActivity).collect(Collectors.toList());
	}


	/**
	 * 获取最近中奖的夺宝活动
	 * @return
	 */
	public List<ProductActivityVo> getLatestLotteryList(){
		if (latestLotteryActivityList == null || latestLotteryActivityList.getElement()  == null) {
			List<ProductActivity> activityList = productActivityMapper.getLotteryActivityList(0, 5);
			long now = System.currentTimeMillis();
			List<ProductActivityVo> voList = activityList.stream().filter(activity -> (now - activity.getLottery_time().getTime()) < Constants.day_millis).map(this::getVoByProductActivity).collect(Collectors.toList());
			latestLotteryActivityList = new LiveAccess<>(Constants.minutes_millis, voList);
		}
		return latestLotteryActivityList.getElement();
	}

	/**
	 * 获取用户在某时间之后的中奖纪录
	 * @param id
	 * @param lastLotteryDate
	 * @return
	 */
	public ProductActivity getUserLatestLottery(int userId, Date lastLotteryDate) {
		if (lastLotteryDate == null){
			lastLotteryDate = DateUtil.getTodayStartDate();
		}
		return productActivityMapper.getUserLatestLottery(userId, lastLotteryDate);
	}
}
