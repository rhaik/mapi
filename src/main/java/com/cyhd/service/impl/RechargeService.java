package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.AppTaskChannelMapper;
import com.cyhd.service.dao.db.mapper.RechargeMapper;
import com.cyhd.service.dao.db.mapper.UserIncomeLogMapper;
import com.cyhd.service.dao.db.mapper.UserIncomeMapper;
import com.cyhd.service.dao.db.mapper.UserTaskChannelMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.Recharge;
import com.cyhd.service.dao.po.RechargeDenomination;
import com.cyhd.service.dao.po.RechargeLog;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.dao.po.UserIncomeLog;
import com.cyhd.service.vo.RechargeVo;

@Service
public class RechargeService extends BaseService { 
	
	//缓存面值信息
	private CacheLRULiveAccessDaoImpl<List<RechargeDenomination>> cacheRechargeList = new CacheLRULiveAccessDaoImpl<List<RechargeDenomination>>(Constants.hour_millis * 1, 1024);
	
	@Resource
	private RechargeMapper rechargeMapper;
	@Resource
	private UserIncomeMapper userIncomeMapper;
	@Resource
	private UserIncomeLogMapper userIncomeLogMapper;
	@Resource
	private AppTaskChannelMapper appTaskChannelMapper;
	@Resource
	private UserTaskChannelMapper userTaskChannelMapper;
	
	@Resource
	UserMessageService userMessageService;
	
	/**
	 * 根据ID获取面值
	 * 
	 * @param id
	 * @return
	 */
	public RechargeDenomination getRechargeDenomination(int id) {
		return rechargeMapper.getRechargeDenomination(id);
	}
	/**
	 * 获取面值列表
	 * @return
	 */
	public List<RechargeDenomination> getRechargeDenominationList(int channel) {
		String cacheKey = "Recharge_" + channel;
		List<RechargeDenomination> list = cacheRechargeList.get(cacheKey);
		if(list == null) {
			list = rechargeMapper.getRechargeDenominationList(channel);
			cacheRechargeList.set(cacheKey, list);
		}
		return list;
	}
	/**
	 * 获取面值列表
	 * @return
	 */
	public List<RechargeVo> getRechargeListByUserId(int userId) {
		List<Recharge> reList = rechargeMapper.getRechargeListByUserId(userId);
		
		List<RechargeVo> vos = new ArrayList<RechargeVo>();
		
		String dateKey = new String();
		for(Recharge re : reList){
			RechargeVo vo = new RechargeVo();
			
			String temp = DateUtil.format(re.getCreatetime(), "yyyyMMdd");
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
			vo.setRecharge(re);
			vos.add(vo);
		}
		return vos;
	}
	/**
	 * 充值
	 * 
	 * @param recharge
	 * @return
	 */
	public boolean recharge(Recharge recharge ) {
		 if(rechargeMapper.add(recharge) > 0) {
			 //减去余额
			 return userIncomeMapper.addUserRecharge(recharge.getUser_id(), recharge.getPay_amount()) > 0;
		 }
		 return false;
	}
	public boolean updateStatusById(int id, int status, String msg) {
		if(rechargeMapper.updateStatus(id, status, 0, msg) > 0) {
			RechargeLog log = new RechargeLog();
			log.setRecharge_id(id);
			log.setStatus(status);
			log.setRemarks(msg.isEmpty() ? "创建充值订单" : msg);
			return rechargeMapper.addRechargeLog(log) > 0;
		}
		return false;
	}
	/**
	 * 获取订单数据
	 * @param orderSn
	 * @return
	 */
	public Recharge getByOrderSn(long orderSn) {
		return rechargeMapper.getByOrderSn(orderSn);
	}
	/**
	 * 更新状态
	 * @return
	 */
	public boolean updateStatus(long orderSn, String status, int total_price, String remarks) {
		int statusInt;
		if(status.equals("success"))  {
			statusInt = Recharge.ORDER_STATUS_SUCCESS;
			remarks = "充值成功";
		} else {
			statusInt = Recharge.ORDER_STATUS_FAIL;
		}
		Recharge re = rechargeMapper.getByOrderSn(orderSn);
		if(re == null) return false;
		if(rechargeMapper.updateRechargeStatusById(re.getId(), statusInt, total_price,remarks) > 0) {
			if(status.equals("success")) {	//成功 
				//充值成功金额
				if(userIncomeMapper.addUserRechargeTotal(re.getUser_id(), re.getPay_amount()) > 0) {
				
					//余额变更日志
					UserIncomeLog userIncomelog = new UserIncomeLog();
					userIncomelog.setAction(UserIncome.INCOME_TYPE_RECHARGE);
					userIncomelog.setAmount(re.getPay_amount());
					userIncomelog.setFriend_level(0);
					userIncomelog.setFrom_user(0);
					userIncomelog.setOperator_time(new Date());
					userIncomelog.setRemarks("手机充值");
					userIncomelog.setType(0);
					userIncomelog.setUser_id(re.getUser_id());
					userIncomelog.setUser_task_id(0);
					userIncomeLogMapper.add(userIncomelog);
				}
				
				//发送消息通知
				userMessageService.notifyMobileRechargeMessage(re.getUser_id(), re.getMobilephone(), re.getValue());
			} else  {	//失败
				userIncomeMapper.returnRechargeUserBalance(re.getUser_id(), re.getPay_amount());
				
			}
			RechargeLog log = new RechargeLog();
			log.setRecharge_id(re.getId());
			log.setStatus(statusInt);
			log.setRemarks("回调:"+(remarks!=null ? remarks: ""));
			return rechargeMapper.addRechargeLog(log) > 0;
		}
		return false;
	}
	/**
	 * 生成订单号
	 * @param mobile
	 */
	public long createOrderSn() {
		long orderSn = System.currentTimeMillis();
		while(rechargeMapper.getByOrderSn(orderSn) != null) {
			orderSn = System.currentTimeMillis();
		}
		return orderSn;
	}
	
}
