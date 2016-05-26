package com.cyhd.service.dao;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;


/**
 * 存储普通数据
 * @author luckyee
 *
 */
@Service(RedisUtil.NAME_ALIYUAN)
public class JedisCommonDao extends AbstractJedisDao {
	
	protected JedisPool pool = null;
	
	protected synchronized JedisPool getJedisPool(){
		if (pool == null) {
			String ip = GlobalConfig.jedis_common_server;
            int port = GlobalConfig.jedis_common_port;
            String passwd = GlobalConfig.jedis_common_pwd;
			try{
	            JedisPoolConfig config = new JedisPoolConfig();
	            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
	            config.setMaxIdle(5);
	            config.setMaxWaitMillis(1000);
	            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
	            config.setTestOnBorrow(true);
	            if(StringUtils.isEmpty(passwd))
	            	pool = new JedisPool(config, ip, port);
	            else
	            	pool = new JedisPool(config, ip, port, 3000, passwd);
	            logger.info(getName() + " init jedis ok, ip={}, port={}, pwd={}", ip, port, passwd);
			}catch(Exception e){
				logger.error(getName() + " init jedis ok, ip="+ip + ", port="+port + ", pwd=" + passwd, e);
			}
        }
		return pool;
	}

	@Override
	protected String getName() {
		return RedisUtil.NAME_ALIYUAN;
	}
	
}

