package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.structure.LRUCache;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserExchangeIntegralLogMapper;
import com.cyhd.service.dao.po.UserExchangeIntegralLog;

@Transactional
@Service
public class UserExchangeIntegralLogService extends BaseService{

	@Resource
	private UserIntegalIncomeService userIntegalIncomeService;	
	
	@Resource
	private UserExchangeIntegralLogMapper userExchangeIntegralLogMapper;
	
	@Resource
	private UserIncomeService userIncomeService;
	
	private final LRUCache<Integer, String> tokenCache = new LRUCache<Integer, String>(0, 1024);
	
	private final Object[] lock = new Object[1024];
	{
		for(int i = 0; i < lock.length; i++){
			lock[i] = new Object();
		}
	}
	
	public UserExchangeIntegralLog exchange(int userId,int exchangeNum,String token,int source,String did,int clientType){
		StringBuilder sb = new StringBuilder(100);
		sb.append("UserId:").append(userId).append(",兑换数量:").append(exchangeNum);
		sb.append(",来源:").append(source==Constants.INTEGAL_SOURCE_WANPU?"金币":"积分").append(",客户端类型:");
		sb.append(clientType == Constants.platform_android?"android":"IOS").append(",did:").append(did);
		String cahceToken = tokenCache.get(userId);
		UserExchangeIntegralLog log = new UserExchangeIntegralLog();
		
		if(exchangeNum < 0){
			exchangeNum = -exchangeNum;
		}
		
		log.setRadio(Constants.INTEGAL_RADIO);
		log.setIntegral(exchangeNum);
		log.setSource(source);
		log.setUser_id(userId);
		log.setClient_type(clientType);
		
		String remark = "兑换失败";
		
		if(!checkToken(token) || token.equals(cahceToken)){
			log.setCode(Constants.INTEGAL_ERROR_CODE_TOKEN);
			log.setRemark("必要参数缺失或错误");
			logger.info(sb.toString()+"必要参数缺失或错误");
			return log;
		}

		//最少兑换数量：安卓：100，iOS：200
		int minExchangeNum =Constants.INTEGAL_MIN_EXCHANGE_NUM;
		if ( clientType == Constants.platform_ios ){
			minExchangeNum = Constants.INTEGAL_MIN_EXCHANGE_NUM_IOS;
		}
		if(exchangeNum < minExchangeNum){
			log.setRemark("最少兑换的数量是:"+ minExchangeNum);
			log.setCode(Constants.INTEGAL_ERROR_CODE_MIN);
			logger.info(sb.toString()+",数量不够");
			return log;
		}
		if(exchangeNum > Constants.INTEGAL_MAX_EXCHANGE_NUM){
			log.setRemark("最多兑换的数量是:"+Constants.INTEGAL_MAX_EXCHANGE_NUM);
			log.setCode(Constants.INTEGAL_ERROR_CODE_MAX);
			logger.info(sb.toString()+",数量太多");
			return log;
		}
		
		synchronized (lock[userId % lock.length]) {
			logger.info(sb.toString()+",兑换开始啦");
			//TODO 再次查询token 
			log.setDid(did);
			
			//TODO 此处需要修改 很严重 事务
			if(userIntegalIncomeService.updateIntegalIncome(userId, source, -exchangeNum,clientType)){
				logger.info("insert into money_user_integal_income success:user_id{},source:{},exchange_num{},start insert into:money_user_integal_income",userId,source,exchangeNum);
				int fen = (exchangeNum*100)/Constants.INTEGAL_RADIO;
				log.setRmb(fen);
				if(userExchangeIntegralLogMapper.insert(log ) > 0){
					logger.info(sb.toString()+"insert into money_user_integal_income success:start insert:money_user_income");
					//String invite_remark = "兑换金币收入";
					
					if(source==Constants.INTEGAL_SOURCE_WANPU){
						//invite_remark="兑换积分收入";
						userIncomeService.addExchangeIntegal(userId, fen,"兑换金币收入");
					}else{
						userIncomeService.addExchangeIntegalYouMi(userId, fen,"兑换积分收入");
					}
					
					
					tokenCache.put(userId, token);
					logger.info(sb.toString()+"insert:money_user_income success");
					remark = "兑换成功";
					log.setCode(0);
				}else{
					//没有操作成功 
					log.setRmb(0);
				}
			}else{
				logger.info(sb.toString()+"not enough balance");
				log.setRemark("余额不足");
				log.setCode(Constants.INTEGAL_ERROR_CODE_BALANCE);
			}
		}
		
		log.setRemark(remark);
		return log;
	}

/**
 * 空实现 <br/>
 * TODO 对token的检查
 * @param token
 * @return
 */
	private boolean checkToken(String token) {
		if(StringUtils.isBlank(token)){
			return false;
		}
		return true;
	}
		public static void main(String[] args) {
			int exchangeNum = 100;
			double change = 100.0 /1000;
			System.out.println(change);
			System.out.println(MoneyUtils.fen2yuan(exchangeNum / Constants.INTEGAL_RADIO));
		}
}
