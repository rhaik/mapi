package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserEnchashmentMapper;
import com.cyhd.service.dao.po.UserEnchashment;
import com.cyhd.service.dao.po.UserEnchashmentAccount;
import com.cyhd.service.dao.po.UserEnchashmentAccountLog;
import com.cyhd.service.dao.po.UserEnchashmentLog;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.UserEnchashmentVo;


@Service
public class UserEnchashmentService extends BaseService {

	/**
	 * 微信提现的开始和结束时间
	 */
	public final static int WEIXIN_START_HOUR = -1;
	public final static int WEIXIN_END_HOUR = -1;


	/**
	 * 提现的阶段
	 */
	public enum EnchashmentStage {

		ENCHASH10(1000, 0, 0), ENCHASH12(1200, 200, 3), ENCHASH15(1500, 200, 0), ENCHASH20(2000, 500, 0), ENCHASH30(3000, 500, 0), ENCHASH50(5000, 500, 0), ENCHASH100(10000, 1000, 0);

		//0表示都展示，1表示只在微信展示，2表示只在支付宝展示
		int type = 0;

		//该阶段的提现金额（分）
		int amount;

		//该阶段返还金币的数量(个)
		int coins;

		EnchashmentStage(int amount, int coins, int type){
			this.amount = amount;
			this.coins = coins;
			this.type = type;
		}

		public int getAmount() {
			return amount;
		}

		public int getCoins() {
			return coins;
		}

		/**
		 * 根据提现金额获取提现的阶段
		 * @param amount
		 * @return
		 */
		public static EnchashmentStage getStageByAmount(int amount) {
			EnchashmentStage[] stages = EnchashmentStage.values();
			for (int i = stages.length -1; i >= 0; --i ){
				if (amount == stages[i].amount){
					return stages[i];
				}
			}
			return null;
		}

		/**
		 * 在某种提现方式下，是否有效
		 * @param tp
		 * @return
		 */
		public boolean isValidOfType(int tp){
			return this.type == 0 || this.type == tp;
		}

		public int getType() {
			return type;
		}
	}

	@Resource
	private UserEnchashmentMapper userEnchashmentMapper;
	
	@Resource
	private UserService userService;

	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao userEnchashmentCache;

	//微信提现总金额
	LiveAccess<Integer> weixinTotalAmount = null;

	//微信提现每日限额： 5000元
	public final static int WEIXIN_ENCHASH_LIMIT = 50000;

	/**
	 * 用户提现列表
	 * 
	 * @param int userid
	 * @param int start
	 * @param int size
	 * 
	 * @return List<UserEnchashmentVo>
	 */
	public List<UserEnchashmentVo> getUserEnchashmentList(int userid, int start, int size){
		List<UserEnchashment> userEnchashmentList = userEnchashmentMapper.getUserEnchashment(userid, start, size);
		
		List<UserEnchashmentVo> vos = new ArrayList<UserEnchashmentVo>();
		
		String dateKey = new String();
		for(UserEnchashment userEnchashment : userEnchashmentList){
			UserEnchashmentVo vo = new UserEnchashmentVo();
			
			String temp = DateUtil.format(userEnchashment.getMention_time(), "yyyyMMdd");
			vo.setDisplayDate(false);
			if(dateKey.isEmpty()) {
				dateKey = temp;
				vo.setDisplayDate(true);
			} else if(!dateKey.equals(temp)){
				if(vos.size() > 0) {
					dateKey = temp;
					vo.setDisplayDate(true);
				}
			}
			
			vo.setUser(userService.getUserById(userEnchashment.getUser_id()));
			vo.setUserEnchashment(userEnchashment);
			vos.add(vo);
		}
		return vos;
	}

	/**
	 * 获取用户最近一次的提现记录
	 * @param userid
	 * @return
	 */
	public UserEnchashment getUserLatestEnchashment(int userid){
		List<UserEnchashment> enchashmentList =  userEnchashmentMapper.getUserEnchashment(userid, 0, 1);
		if (enchashmentList != null && enchashmentList.size() > 0){
			return enchashmentList.get(0);
		}
		return null;
	}
	/**
	 * 获取用户日志总数
	 * @param userid
	 * @return
	 */
	public int getUserEnchashmentCount(int userid) {
		return userEnchashmentMapper.getUserEnchashmentCount(userid);
	}
	/**
	 * 保存
	 * 
	 * @param UserEnchashment userEnchashment
	 * 
	 * @return boolean
	 */
	public boolean save(UserEnchashment userEnchashment){
		 if(userEnchashmentMapper.add(userEnchashment) > 0) {
			 //如果是微信提现，则清除微信提现总金额的缓存
			 if (userEnchashment.getType() == UserEnchashment.ACCOUNT_TYPE_WX){
				 weixinTotalAmount = null;
			 }

			 UserEnchashmentLog log = new UserEnchashmentLog();
			 log.setUser_enchashment_id(userEnchashment.getId());
			 log.setOperator(userEnchashment.getUser_id());
			 log.setType(userEnchashment.getType());
			 log.setOperator_time(new Date());
			 log.setRemarks(userEnchashment.getStatus() == UserEnchashment.STATUS_INIT? "申请提现" : "申请提现，自动审核通过");
			 log.setStatus(userEnchashment.getStatus());
			 return userEnchashmentMapper.addUserEnchashmentLog(log) > 0;
		 }
		 return false;
	}
	/**
	 * 根据id获取数据
	 * @param id
	 * @return
	 */
	public UserEnchashment getById(int id) {
		return userEnchashmentMapper.getById(id);
	}
	/**
	 * 获取用户提现账号
	 * 
	 * @param user_id
	 * @return
	 */
	public UserEnchashmentAccount getUserEnchashmentAccount(int user_id) {
		return userEnchashmentMapper.getEnchashmentAccountByUserId(user_id);
	}
	/**
	 * 更新用户提现账号
	 * 
	 * @param user_id
	 * @return
	 */
	public boolean addOrUpdateEnchashmentAccount(UserEnchashmentAccount account) {
		UserEnchashmentAccountLog log = new UserEnchashmentAccountLog();
		log.setAlipay_account(account.getAlipay_account());
		log.setAlipay_name(account.getAlipay_name());
		log.setUser_id(account.getUser_id());
		log.setWx_bank(account.getWx_bank());
		log.setWx_bank_name(account.getWx_bank_name());
		log.setCreatetime(account.getUpdatetime());
		boolean flag = userEnchashmentMapper.addOrUpdateEnchashmentAccount(account) > 0 && userEnchashmentMapper.addEnchashmentAccountLog(log) > 0;
		return flag;
	}
	
	public UserEnchashmentAccount getAccountByAlipay_account(String alipay_account){
		return userEnchashmentMapper.getUserEnchashmentAccountByAlipay_account(alipay_account);
	}
	
	public int countAccountByAlipay_account(String alipay_account){
		return this.userEnchashmentMapper.countAccountByAilipay_account(alipay_account);
	}
	
	public UserEnchashment getUserEnchashmentTopByAccount(String account){
		return this.userEnchashmentMapper.selectLogByAlipayAccount(account);
	}
	/***
	 * 账号是不是被封禁的
	 * @param alipayAccount
	 * @param accountName
	 * @return
	 */
	public boolean isBlacklist(String alipayAccount,String accountName){
		String key = RedisUtil.buildUserBlacklistAccount(alipayAccount);
		String value = null;
		try {
			value = this.userEnchashmentCache.get(key);
			if(accountName.equals(value)){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Integer userId = this.userEnchashmentMapper.selectLogByAlipayAccountAndMasked(alipayAccount, accountName, UserEnchashmentAccount.IS_MASKED);
		if(userId != null && userId.intValue() > 0){
			try {
				this.userEnchashmentCache.set(key, accountName);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
	
	public List<UserEnchashment> getEnchashSuccessUsers(int start, int size){
		return this.userEnchashmentMapper.getEnchashSuccessUsers(start, size);
	}


	/**
	 * 设置该条提现记录已奖励金币
	 * @param eid
	 * @return
	 */
	public boolean setRewarded(int eid){
		return userEnchashmentMapper.setRewarded(eid) > 0;
	}

	public List<UserEnchashment> getEnchashSuccessUsersByTime(Date successStart,Date successEnd){
		return this.userEnchashmentMapper.getEnchashSuccessUsersByTime(successStart, successEnd);
	}

	/**
	 * 获取一段时间内的提现总金额
	 * @param type
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public int getTotalEnchashAmount(int type, Date startTime, Date endTime){
		Integer total = userEnchashmentMapper.getTotalEnchashAmount(type, startTime, endTime);
		return total == null? 0 : total.intValue();
	}

	/**
	 * 获取今日微信提现总金额
	 * @return
	 */
	public int getWeixinTodayEnchashAmount(){
		int amount = 0;
		if (weixinTotalAmount != null && weixinTotalAmount.getElement() != null){
			amount = weixinTotalAmount.getElement();
		}else {
			amount = getTotalEnchashAmount(UserEnchashment.ACCOUNT_TYPE_WX, DateUtil.getTodayStartDate(), DateUtil.getTodayEndDate());
			weixinTotalAmount = new LiveAccess<>(Constants.minutes_millis, amount);
		}
		return amount;
	}

	/**
	 * 是否在微信提现的时间范围中
	 * @return
	 */
	public boolean isInWeixinEnchashTime(){
		int hour = DateUtil.getHour(GenerateDateUtil.getCurrentDate());
		if (hour >= WEIXIN_START_HOUR && hour <= WEIXIN_END_HOUR){
			return true;
		}
		return false;
	}


	/**
	 * 是否达到微信提现的限额
	 * @return
	 */
	public boolean isUnderWeixinLimit(){
		return getWeixinTodayEnchashAmount() < WEIXIN_ENCHASH_LIMIT;
	}
	public boolean setAutoCheckPassed(int uid){
		return userEnchashmentMapper.setAutoPassed(uid) > 0;
	}

	/**
	 * 设置用户提现账户的评分
	 * @param uid
	 * @param score
	 * @param detail
	 * @return
	 */
	public boolean  setEncashAccountScore(int uid, int score, String detail){
		return userEnchashmentMapper. setEncashAccountScore(uid, score, detail) > 0;
	}

	/**
	 * 设置用户提现的评分
	 * @param uid
	 * @param score
	 * @param detail
	 * @return
	 */
	public boolean setUserEncashScore(int uid, int score, String detail){
		return userEnchashmentMapper. setUserEncashScore(uid, score, detail) > 0;
	}
}
