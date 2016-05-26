package com.cyhd.service.dao;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;


/**
 * 自己搭建的redis
 * @author luckyee
 *
 */
@Service(RedisUtil.NAME_SELF)
public class JedisSelfCacheDao extends AbstractJedisDao {
	
	protected JedisPool pool = null;
	
	protected synchronized JedisPool getJedisPool(){
		if (pool == null) {
			String ip = GlobalConfig.jedis_self_server;
            int port = GlobalConfig.jedis_self_port;
			try{
	            JedisPoolConfig config = new JedisPoolConfig();
	            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
	            config.setMaxIdle(5);
	            config.setMaxWaitMillis(1000);
	            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	            config.setTestOnBorrow(true);
	            pool = new JedisPool(config, ip, port);
	            logger.info(getName() + " init jedis ok, ip={}, port={}, pwd={}", ip, port);
			}catch(Exception e){
				logger.error(getName() + " init jedis ok, ip="+ip + ", port="+port + ", pwd=", e);
			}
        }
		return pool;
	}

	@Override
	protected String getName() {
		return RedisUtil.NAME_SELF;
	}
	
}

