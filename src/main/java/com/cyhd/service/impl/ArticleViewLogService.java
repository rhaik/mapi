package com.cyhd.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.util.CacheUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.ArticleViewLogMapper;
import com.cyhd.service.dao.po.ArticleViewLog;
import com.cyhd.service.util.RedisUtil;

@Service
public class ArticleViewLogService extends BaseService{

	//单个ip允许的最大阅读量
	private static final Integer MAX_IP_READ_NUM = 15;

	/**
	 * 同一个IP，30秒内浏览只算一次
	 */
	private final static int IP_TTL = 30 * 1000;

	/**
	 * 用memcache记录上次访问的IP
	 */
	@Resource(name = CacheUtil.MEMCACHED_RESOURCE)
	private CacheDao memcachedCacheDao;

	@Resource
	private ArticleViewLogMapper articleViewLogMapper;
	
	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao logCache;

	/**默认缓存时间是5天 */
	private final int ttl = Constants.DAY_SECONDS*5;
	
	public int insert(int article_id,int task_user_id,String task_user_unionid,String view_unionid,Date date,String view_openid, String ip){
		
//		if(this.isViewed(article_id, view_unionid)){
//			return -1;
//		}
		
		ArticleViewLog log = new ArticleViewLog();
		log.setArticle_id(article_id);
		log.setCreatetime(date);
		log.setTask_user_id(task_user_id);
		log.setTask_user_unionid(task_user_unionid);
		log.setView_unionid(view_unionid);
		//新增记录是谁 可以获取用户信息
		log.setView_openid(view_openid);
		log.setIp(ip);
		
		int id = articleViewLogMapper.insert(log );
		
		if(id > 0 ){
			this.addToCache(RedisUtil.buildArticleLogKey(article_id, view_unionid));
		}
		return id;
	}

	/***
	 * 此文章下 该unionId是不是查看过
	 * @param article_id
	 * @param unionId
	 * @return <code>true</code> 查看过
	 */
	public boolean isViewed(int article_id,String unionId, String ip){
		String key = RedisUtil.buildArticleLogKey(article_id, unionId);
		try {
			String value = logCache.get(key);
			if(StringUtils.isNotBlank(value)){
				return true;
			}
		} catch (Exception e) {
			if(logger.isDebugEnabled()){
				logger.debug("get viewed log for redis:cause by :{}",e);
			}
		}
		
		Integer id =  articleViewLogMapper.existByUser(article_id, unionId);
		if(id != null && id > 0){
			this.addToCache(key);
			return true;
		}

		if (!StringUtil.isBlank(ip)) {
			String ipKey = CacheUtil.getIPCacheKey(ip);
			if (memcachedCacheDao.isExist(ipKey)){
				logger.info("viewer ip too frequent, ip:{}", ip);
				return true;
			}

			//记录阅读者ip
			memcachedCacheDao.set(ipKey, Boolean.TRUE, IP_TTL);

			//检查是否超过单个ip的最大阅读量
			Integer ipReadCount = articleViewLogMapper.getReadCountByIp(article_id, ip);
			if (ipReadCount != null && ipReadCount > MAX_IP_READ_NUM) {
				return true;
			}
		}
		return false;
	}
	
	private void addToCache(String key){
		try {
			logCache.set(key, "1", ttl );
		} catch (Exception e) {
			if(logger.isDebugEnabled()){
				logger.debug("add viewed unionid into redis ，cause by:{}",e);
			}
		}
	}
}
