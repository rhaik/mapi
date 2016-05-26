package com.cyhd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserThridPartIntegralMapper;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.dao.po.UserThridPartIntegral;
import com.cyhd.service.util.RedisUtil;


@Service
public class UserThridPartIntegralService extends BaseService{

	@Resource
	private UserIntegalIncomeService userIntegalIncomeService;
	
	@Resource
	private UserExchangeIntegralLogService userExchangeIntegralLogService;
	
	@Resource
	private UserExchangeIntegralService userExchangeIntegralService;
	
	@Resource
	private UserThridPartIntegralMapper userThridPartIntegralMapper;
	
	@Resource
	private UserService userService;
	
	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao thridPartIntegralCache;
	
	@Resource
	private UserMessageService userMessageService;
	//插入积分 修改积分余额 
	public boolean insert(UserThridPartIntegral integral){
		User user = userService.getUserByIdentifyId(Integer.parseInt(integral.getKey()));
		logger.info("或的User:{}",user);
		integral.setUser_id(user.getId());
		//简单处理 是ios 并且 如果存在 itunes_id的话 就去进行作弊检查
		if(integral.getClient_type() == Constants.platform_ios && StringUtil.isNotBlank(integral.getItunes_id())){
			filterCheat(user.getId(),integral.getKey(), integral.getItunes_id(), integral.getAdv_id(), integral.getSource());
		}
		//插入第三方积分记录表
		//修改积分收入表
		if(this.add(integral)){
			logger.info("插入money_user_thrid_part_integral数据库成功");
			int income = integral.getPoints();
			//不是万普统一为积分
			int source = integral.getSource() == Constants.INTEGAL_SOURCE_WANPU?Constants.INTEGAL_SOURCE_WANPU:Constants.INTEGAL_SOURCE_YOUMI;
			//哎 以前没有弄好 丢
			//ios接的积分墙 都是金币
			if(integral.getSource() == Constants.INTEGAL_SOURCE_YOUMI_IOS 
					|| integral.getSource() == Constants.INTEGAL_SOURCE_DIANJOY_IOS
					||integral.getSource() == Constants.INTEGAL_SOURCE_DIANRU_IOS
					||integral.getSource() == Constants.INTEGAL_SOURCE_QUMI_IOS
					||integral.getSource() == Constants.INTEGAL_SOURCE_SYSTEM){
				source = Constants.INTEGAL_SOURCE_WANPU;
			}
			
			boolean ret =  userIntegalIncomeService.updateIntegalIncome(user.getId(),source  , income,integral.getClient_type());
			logger.info("insert into money_user_integal_income：status:{}",ret);
			if(ret){
				userIntegalIncomeService.addShare(user.getId(), income, Constants.INTEGAL_SOURCE_WANPU, integral.getAd_name(),UserSystemMessage.TYPE_INTEGAL_SHARE_JINBI, integral.getClient_type());
				userMessageService.notifyIntegralMessage(user.getId(), integral.getAd_name(), integral.getPoints(),source,integral.getClient_type());
			}
			return ret;
		}
//		else{
//			logger.info("插入第三方积分记录表异常失败：{}");
//		}
		
		return false;
		
	}
	/**直接插入数据库*/
	public boolean add(UserThridPartIntegral integral){
		return userThridPartIntegralMapper.insert(integral) > 0;
	}
	/**
	 *点乐去重
	 *true:已存在
	 **/
	public boolean repeatByDianJoy(String device_id,String pack_name,int trade_type,String task_id){
		Long id =  userThridPartIntegralMapper.repeatByDianJoy(device_id, pack_name, trade_type, task_id) ;
		return id != null ;
		
	}
	/***
	 * 后台只取前两次结果
	 * @param key
	 * @param adv_id
	 * @param source
	 * @return
	 */
	public List<Long> repeatByWanPuIOS(String key,String adv_id,int source){
		List<Long> ids = userThridPartIntegralMapper.repeatByWanPuIOS(key,adv_id,source);
		return ids ;
	}
	/***
	 * 米迪排重
	 * @param trand_no
	 * @return
	 */
	public boolean repeatByOrderId(String trand_no,int source){
		return  userThridPartIntegralMapper.repeatByOrderId(trand_no,source) != null;
	}
	
	/**
	 * 将有米的积分兑换为金币？
	 * @param jifen
	 * @return
	 */
	public int convert(int jifen){
		return jifen;
	}
	
	
	public void filterCheat(int userId,String user_identify,String appId,String adv_id,int source){
		String key = RedisUtil.buildIntegralWarKey( userId, appId);
		boolean cheat = false;
		try {
			if(thridPartIntegralCache.exists(key)){
				cheat = true;
			}
		} catch (Exception e) {}
		
		if(cheat){
			throw new RuntimeException("已经做过的任务");
		}
		
		filterCheatByUserAndApp(userId, appId, key);
		
		if(StringUtil.isNotBlank(adv_id)){
			List<Long> ids = userThridPartIntegralMapper.repeatByWanPuIOS(user_identify, adv_id, source);
			if(ids != null && ids.size() >= 2){
				addTocache(key);
				throw new RuntimeException("已经做过的任务");
			}
		}
		
	}
	
	private void addTocache(String key){
		try {
			thridPartIntegralCache.set(key, "1", Constants.DAY_SECONDS*15);
		} catch (Exception e) {}
	}
	private void filterCheatByUserAndApp(int userId,String appId,String key){
		boolean cheat = false;
		Integer times = userThridPartIntegralMapper.repeatByUserAppExistThirdTimes(userId, appId);
		cheat =  times != null && times >= 2;
		if(cheat){
			addTocache(key);
			throw new RuntimeException("已经做过的任务");
		}
		
	}
}
