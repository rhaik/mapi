package com.cyhd.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserIntegalIncomeMapper;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserIntegalIncome;
import com.cyhd.service.dao.po.UserThridPartIntegral;

@Transactional
@Service
public class UserIntegalIncomeService extends BaseService {

	@Resource
	private UserIntegalIncomeMapper userIntegalIncomeMapper;
	
	@Resource
	private UserFriendService userFriendService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserMessageService userMessageService;
	@Resource
	private UserThridPartIntegralService userThridPartIntegralService;
	
//	public boolean createNewIntegalIncome(int userId){
//		return userIntegalIncomeMapper.createNewRecord(userId) > 0;
//	}
	/**
	 * 
	 * @param userId
	 * @param source
	 * @param income 负数表示兑换
	 * @return
	 */
	public boolean updateIntegalIncome(int userId,int source,int income,int clientType){
		UserIntegalIncome userIntegalIncome = userIntegalIncomeMapper.getIntegalByUserBysource(userId,source,clientType);
		boolean isNew = userIntegalIncome == null;
		
		if(isNew){
			userIntegalIncome = new UserIntegalIncome();
			userIntegalIncome.setEstate(Constants.ESTATE_Y);
			userIntegalIncome.setUser_id(userId);
			userIntegalIncome.setSource(source);
		}
		//记录最后一次的客户端
		userIntegalIncome.setClient_type(clientType);
		
		String preSnapshoot = "修改数据前:"+getSnapshoot(userIntegalIncome)+",收入:"+income;
		logger.info(preSnapshoot);
		
		if(income < 0){
			if(userIntegalIncome.getBalance() < Constants.INTEGAL_MIN_EXCHANGE_NUM || (userIntegalIncome.getBalance() + income < 0)){
				return false;
			}
			userIntegalIncome.setExchange(userIntegalIncome.getExchange() - income);
		}else{
			userIntegalIncome.setIncome(userIntegalIncome.getIncome()+income);
		}
		
		userIntegalIncome.setBalance(userIntegalIncome.getBalance() + income);
		
		preSnapshoot = "修改数据后，待修改数据库:"+getSnapshoot(userIntegalIncome)+",收入:"+income;
		logger.info(preSnapshoot);
		boolean flag = false;
		
		if (isNew) {
			//有点点不敢去相信on duplicate key update
			flag =  userIntegalIncomeMapper.insertOrUpdate(userIntegalIncome) > 0;
		}else{
			flag = userIntegalIncomeMapper.update(userIntegalIncome) > 0;
		}
		logger.info("用户:{},操作数据库-积分的状态:{}",userIntegalIncome.getUser_id(),flag);
		return flag;
	}
	
	
	public UserIntegalIncome getIntegalIncomeBySource(int userId,int source,int clientType){
		return userIntegalIncomeMapper.getIntegalByUserBysource(userId,source,clientType);
	}
	
	public List<UserIntegalIncome> getIntegalIncome(int userId){
		return userIntegalIncomeMapper.getIntegalByUser(userId);
	}
	
	private String getSnapshoot(UserIntegalIncome userIntegalIncome){
		StringBuilder sb = new StringBuilder(120);
		sb.append("用户:").append(userIntegalIncome.getUser_id());
		sb.append(",余额:").append(userIntegalIncome.getBalance()).append(",总收入:").append(userIntegalIncome.getIncome());
		sb.append(",兑换:").append(userIntegalIncome.getExchange()).append(",积分来源:").append(userIntegalIncome.getSource());
		sb.append(",平台:").append(userIntegalIncome.getClient_type()).append(userIntegalIncome.getClient_type() == Constants.platform_ios?":IOS":":Android");
		return sb.toString();
	}
	
	public void addShare(int userid,int amount,int source,String appName,int messageSource,int clientType){
		User user = userService.getUserById(userid);
		int invitor = userFriendService.getInvitor(user.getId());
		if(invitor <= 0){
			logger.info("用户UserId:{},没有师傅",user.getId());
		}else{
			int shareAmount = userFriendService.getShareAmount(invitor, amount);
			logger.info("用户UserId:{},有师傅Id:{}，分成数量：{}",user.getId(),invitor, shareAmount);

			if(shareAmount > 0 && updateIntegalIncome(invitor, source, shareAmount, clientType)){
				UserThridPartIntegral integral = new UserThridPartIntegral();
				integral.setAd_name("好友分成");
				integral.setPoints(shareAmount);
				integral.setSource(Constants.INTEGAL_SOURCE_SHARE);
				integral.setUser_id(invitor);
				integral.setClient_type(clientType);
				integral.setOrder_id(System.currentTimeMillis()+""+invitor);
				integral.setRandom_code(appName);
				integral.setKey(String.valueOf(userid));
				if (this.userThridPartIntegralService.add(integral)) {
					userMessageService.addIntegarShareMessage(user, invitor, appName, amount, shareAmount, 1, messageSource,clientType);
				}
			}
		}
	}

	/**
	 * 用户补发金币
	 * @return
	 */
	public  boolean reissueIntegal(User user,int amount,String reason,int clientType){
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(String.valueOf(user.getUser_identity()));
		integral.setAd_name(reason);
		integral.setPoints(amount);
		integral.setClient_type(clientType);
		integral.setSource(Constants.INTEGAL_SOURCE_SYSTEM);
		integral.setUser_id(user.getId());
		integral.setOrder_id(System.currentTimeMillis()+""+(int)(Math.random()*1000));
		logger.info("补发金币操作金币数量:userid:{},amount:{},reason:{}",user.getId(),amount,reason);
		if(this.updateIntegalIncome(user.getId(), Constants.INTEGAL_SOURCE_WANPU, amount, clientType)){
			logger.info("补发金币操作金币数量成功,插入第三方金币:userid:{},amount:{},reason:{}",user.getId(),amount,reason);
			if( this.userThridPartIntegralService.add(integral)){
				logger.info("补发金币操作金币数量成功,插入第三方金币成功:userid:{},amount:{},reason:{}",user.getId(),amount,reason);
				userMessageService.addReissueIntegalMessage(user.getId(), amount, reason, clientType);
				return true;
			}
		}
		return false;
	}

	/**
	 * 系统奖励或者返还金币
	 * @param userId 用户id
	 * @param amount 金币数量
	 * @param reason 原因
	 * @param clientType 客户端类型
	 * @return
	 */
	public boolean addRewardedIntegral(int userId, int amount, String reason, int clientType, String orderId){
		if (amount == 0){
			return false;
		}
		User user = userService.getUserById(userId);
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(String.valueOf(user.getUser_identity()));
		integral.setAd_name(reason);
		integral.setPoints(amount);
		integral.setClient_type(clientType);
		integral.setSource(Constants.INTEGAL_SOURCE_SYSTEM);
		integral.setUser_id(userId);
		integral.setOrder_id(orderId);
		logger.info("addRewardIntegral:userid:{},amount:{},reason:{}",userId, amount, reason);

		if(updateIntegalIncome(userId, Constants.INTEGAL_SOURCE_WANPU, amount, clientType)){
			logger.info("addRewardIntegral success:userid:{},amount:{},reason:{}", userId, amount, reason);
			userThridPartIntegralService.add(integral);
			//添加发送信息
			userMessageService.notifyIntegralMessage(user.getId(), integral.getAd_name(), integral.getPoints(),Constants.INTEGAL_SOURCE_WANPU,integral.getClient_type());
			return true;
		}
		return false;
	}
}
