package com.cyhd.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.SmsDao;
import com.cyhd.service.util.GlobalConfig;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.service.dao.impl.SmsBaiwuDao;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserEnchashment;


@Service
public class SmsService extends BaseService {

	@Resource
	SmsDao smsDao;
	
	@Resource
	private UserEnchashmentService userEnchashmentService;
	
	@Resource
	private UserService userService;
	

	public boolean sendDuobaoSuccessSms(User user, String productName){
		String url = GlobalConfig.isDeploy? "http://t.cn/Rb8RJff" : "http://t.cn/Rb8Rfs2";
		if (user.getDevicetype() == Constants.platform_android){
			url = "";
		}
		String content = String.format("恭喜您一元夺宝成功获得奖品“%s”请您3日内至一元夺宝个人中心-中奖记录确认收货地址%s ", productName, url);

		boolean result = false;
		if (StringUtil.isNotBlank(user.getMobile())) {
			result = smsDao.sendSms(user.getMobile(), content, 2, 1);
		}
		logger.info("send duobao success sms to user:{}, content:{}, result:{}", user, content, result);

		return result;
	}

	public void sendVercode(String phone, String code) {

	}
	
	public int sendSms(int start, int size){
		List<UserEnchashment> users = userEnchashmentService.getEnchashSuccessUsers(start, size);
		return  executeSendUsers(users);
	}

	public boolean sendSingle(UserEnchashment ue){
		User u = userService.getUserById(ue.getUser_id());
		
		if(!u.isIos()){
			return false;
		}
		
		String mobile = u.getMobile();
		if(StringUtils.isEmpty(mobile)){
			logger.error("userId {}, mobile is null", ue.getUser_id());
			return false;
		}
		String content = String.format("亲，恭喜你在%s使用支付宝提现%s元成功，更多赚钱任务请继续关注我们，下载地址：http://t.cn/RUBoVdi 。", 
				DateUtil.format(ue.getMention_time(),  "yyyy-MM-dd"), MoneyUtils.fen2yuanS(ue.getAmount()));
		boolean result = smsDao.sendSms(mobile, content, 1, 1);
		return result;
	}
	
	public int sendSms(Date successStart,Date successEnd){
		if(successStart == null || successEnd == null){
			throw new IllegalArgumentException("参数错误,开始与结束时间不能为空");
		}
		List<UserEnchashment> users = this.userEnchashmentService.getEnchashSuccessUsersByTime(successStart, successEnd);
		return executeSendUsers(users);
	}

	protected  int executeSendUsers(List<UserEnchashment> users){
		if(users== null){
			return -1;
		}
		int count = 0;
		for(UserEnchashment ue : users){
			boolean result = sendSingle(ue);
			if(result){
				count++;
			}
		}
		return count;
	}
	
	/***
	 * 一元夺宝 发货提醒
	 * @param mobile
	 * @param content
	 * @return
	 */
	public boolean sendSendGoodsPrompt(String mobile,String content){
		logger.info("send goods prompt,mobile:{},content:{}",mobile,content);
		boolean result = false;
		if(StringUtil.isNotBlank(mobile)){
			result = smsDao.sendSms(mobile, content, 2, 1); 
		}
		logger.info("send goods prompt,mobile:{},content:{},send state:{}",mobile,content,result);
		return result;
	}
}
