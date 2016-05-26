package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserExchangeIntegralLogMapper;
import com.cyhd.service.dao.db.mapper.UserThridPartIntegralMapper;
import com.cyhd.service.dao.po.UserExchangeIntegralLog;
import com.cyhd.service.dao.po.UserThridPartIntegral;


@Service
public class UserExchangeIntegralService extends BaseService {

	
	@Resource
	private UserExchangeIntegralLogMapper userExchangeIntegralLogMapper;
	
	@Resource
	private UserThridPartIntegralMapper userThridPartIntegralMapper;
	
	
	/**
	 * 金币兑换日志
	 * 
	 * @param int userid
	 * @param int start
	 * @param int size
	 * 
	 * @return List<UserExchangeIntegralLog>
	 */
	public List<UserExchangeIntegralLog> getListByUserId(int userid,int source,int clientType, int start, int size){
		if(source != Constants.INTEGAL_SOURCE_WANPU){
			source = Constants.INTEGAL_SOURCE_YOUMI;
		}
		return userExchangeIntegralLogMapper.getListByUserId(userid,source,clientType, start, size);
	}
	
	public int countUserExchangeIntegralLog(int userId,int source,int clientType){
		return userExchangeIntegralLogMapper.countUserExchangeIntegralLog(userId,source,clientType);
	}
	/**
	 * 联盟应用日志
	 * 
	 * @param int userid
	 * @param int start
	 * @param int size
	 * 
	 * @return List<UserExchangeIntegralLog>
	 */
	public List<UserThridPartIntegral> getUserThridPartIntegralList(int userid,int source,int clientType, int start, int size){
		
		List<UserThridPartIntegral> logs = null;
		if(source == Constants.INTEGAL_SOURCE_WANPU){
			logs = userThridPartIntegralMapper.getUserThridPartIntegralList(userid,source,clientType, start, size);
		}else{
			logs = userThridPartIntegralMapper.getUserThridPartIntegralListByJifen(userid,source,clientType, start, size);
		}
		
		List<UserThridPartIntegral> l = new ArrayList<UserThridPartIntegral>();
		String dateKey = new String();
		for(UserThridPartIntegral log : logs) {
			UserThridPartIntegral vo = log;
			
			String temp = DateUtil.format(log.getCreatetime(), "yyyyMMdd");
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
			l.add(vo);
		}
		return l;
	}
	
	public int countUserThridPartIntegral(int userId,int source,int clientType){
		if(source == Constants.INTEGAL_SOURCE_WANPU){
			return userThridPartIntegralMapper.countUserThridPartIntegral(userId,source,clientType);
		}else{
			return userThridPartIntegralMapper.countUserThridPartIntegralByJifen(userId,source,clientType); 
		}
	}
}
