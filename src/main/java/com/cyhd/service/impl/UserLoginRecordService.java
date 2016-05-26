package com.cyhd.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserLoginRecordMapper;
import com.cyhd.service.dao.po.UserLoginRecord;
import com.cyhd.service.util.RedisUtil;

@Service
public class UserLoginRecordService extends BaseService {

	@Resource
	private UserLoginRecordMapper userLoginRecordMapper;
	
	private ExecutorService addExecutor = null;
	
	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao loginRecodeCache;

	//同一ip登陆的时间间隔
	private final int loginRecodeIpTTL = 30;
	
	@PostConstruct
	public void init() {
		ThreadFactory threadFactory = new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("login_record_job_thread");
				return t;
			}
		};
		addExecutor = Executors.newFixedThreadPool(2, threadFactory);
	}

	@PreDestroy
	public void shutdown() {
		if(addExecutor != null){
			addExecutor.shutdown();
		}
	}
	
	public void add(int user_id, String name, String avatar, String country, String province, String city, String did, String idfa, 
			String appver, int devicetype, String model, String os, String net, String ticket, String ip){
		final UserLoginRecord record = new UserLoginRecord();
		record.setUser_id(user_id);
		record.setName(name);
		record.setAppver(appver);
		record.setAvatar(avatar);
		record.setCountry(country);
		record.setProvince(province);
		record.setCity(city);
		record.setDid(did);
		record.setIdfa(idfa);
		record.setDevicetype(devicetype);
		record.setModel(model);
		record.setOs(os);
		record.setNet(net);
		record.setTicket(ticket);
		record.setIp(ip);
		record.setCreatetime(new Date());
		
		addExecutor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					add(record);
				} catch (Exception e) {
					logger.error("add login record error!",e);
				}
			}
		});
	}
	
	private int add(final UserLoginRecord record) throws Exception{
		return userLoginRecordMapper.add(record);
	}

	/**
	 * 登陆时对ip做限制
	 * @param ip
	 * @return
	 */
	public boolean isExistIpByCache(String ip){

		String key = RedisUtil.buildLoginIpKey(ip);
		String lastTimeKey = RedisUtil.buildLastTimeLoginIpKey();
		boolean result = false;
		try {
			//同一个ip每30秒只能登录一次
			if (loginRecodeCache.exists(key)) {
				result =  true;
			}else {
				loginRecodeCache.set(key, "1", loginRecodeIpTTL);

				//检查上次登陆成功的ip，如果本次登录与上次登陆成功的ip相同，也不让登录
				String value = loginRecodeCache.get(lastTimeKey);
				if(value != null && value.equals(ip)){
					result = true;
				}else {
					loginRecodeCache.set(lastTimeKey, ip, loginRecodeIpTTL * 10);
				}
			}

		} catch (Exception e) {
			logger.error("", e);
		}
		
		return result;
	}
	
	public boolean isExistUserChangeFiveTimesIdfa(int userId){
		List<String> idfas = userLoginRecordMapper.getUserChangeFiveTimesIDFA(userId);
		return idfas != null && idfas.size() >= 5;
	}
	
	public String getUserLastLoginIp(int userId){
		String ip = userLoginRecordMapper.getUserLastLoginIp(userId);
		return ip;
	}
	
}
