package com.cyhd.service.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import org.springframework.stereotype.Service;

import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.UserInstalledAppMapper;
import com.cyhd.service.dao.po.UserInstalledApp;
import com.cyhd.service.util.RedisUtil;



@Service
public class UserInstalledAppService extends BaseService{

	@Resource
	private UserInstalledAppMapper userInstalledAppMapper;
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao userInstalledAppCache;
	
	public UserInstalledApp insert(int userId,int appId,String did,String agreement){
		UserInstalledApp userInstalledApp = new UserInstalledApp();
		userInstalledApp.setAgreement(agreement);
		userInstalledApp.setApp_id(appId);
		userInstalledApp.setUser_id(userId);
		userInstalledApp.setCreatetime(new Date());
		userInstalledApp.setDid(did);
		int id =0;
		
		try {
			//插入前没有做判断 插入数据库就可能存在相同的key 
			id = userInstalledAppMapper.insert(userInstalledApp);
		} catch (Exception e) {
//			if (logger.isInfoEnabled()) {
//				logger.info(" cause by:{}",e);
//			}
		}
		
		if(id > 0){
			String key = RedisUtil.buildUserInstallApp(userId);
			try {
				userInstalledAppCache.sadd(key, Integer.toString(appId));
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("redis set userInstalledApp,userId:{},appid:{},cause by:{}",userId,appId,e);
				}
			}
			userInstalledApp.setId(id);
			return userInstalledApp;
		}
		return null;
	}
	
	public Set<Integer> getListByUserId(int userId){
		Set<Integer> data =  new HashSet<Integer>();
		String key = RedisUtil.buildUserInstallApp(userId);
		
		try {
			Set<String> redisList = userInstalledAppCache.smembers(key);
			if(redisList != null && redisList.size() > 0){
				data = new HashSet<Integer>();
				for(String app_id:redisList){
					data.add(Integer.parseInt(app_id));
				}
			}
			if(data != null){
				return data;
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("redis get userInstalledApp,userId:{} :cause by:{}",userId,e);
			}
		}
				
		List<Integer> dataForDB = userInstalledAppMapper.getListByUserId(userId);
		data.addAll(dataForDB);
		
		if(dataForDB !=null && !dataForDB.isEmpty()){
			String[] appIds = new String[dataForDB.size()];
			for(int i = 0 ; i < dataForDB.size(); i++){
				appIds[i] = Integer.toString(dataForDB.get(i));
			}
			
			try {
				userInstalledAppCache.sadd(key, appIds);
				userInstalledAppCache.expire(key, 7 * Constants.DAY_SECONDS);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("redis set userInstalledApp,userid:{},:cause by:{}",userId,e);
				}
			}
		}
		
		return data;
	}

	/**
	 * 是否被预先导入的IDFA排重了
	 * @param appId
	 * @param idfa
	 * @return
	 */
	public boolean isPreFilteredByIDFA(int appId, String idfa) {
		boolean filtered = false;
		if (StringUtil.isNotBlank(idfa)) {
			try {
				filtered = userInstalledAppCache.sismember(RedisUtil.buildPreFilteredIDFAKey(appId), idfa);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("redis isPreFilteredByIDFA error, app id:{}, idfa:{}, error:{}", appId, idfa, e);
				}
			}
		}
		return filtered;
	}


	/**
	 * 保存预先排重的idfa，厂商预先排重未通过时，放进缓存，默认保存一周
	 * @param appId
	 * @param idfa
	 * @return
	 */
	public void addPreFilteredIDFA(int appId, String idfa){
		if (StringUtil.isNotBlank(idfa)) {
			try {
				String key = RedisUtil.buildPreFilteredIDFAKey(appId);
				userInstalledAppCache.sadd(key, idfa);
				userInstalledAppCache.expire(key, 7 * Constants.DAY_SECONDS);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("redis addPreFilteredIDFA error, app id:{}, idfa:{}, error:{}", appId, idfa, e);
				}
			}
		}
	}
}
