package com.cyhd.service.dao.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.MongoDBAbstractDao;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.MongoDBUtil;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;

@Service(MongoDBUtil.stat_db)
public class MongoDBStatDaoImpl extends MongoDBAbstractDao {

	@Override
	//@PostConstruct
	public void init() {
		try {
			List<ServerAddress> addresses = new ArrayList<ServerAddress>() ;
			String mongodbHosts = GlobalConfig.mongodb_hosts ;
			String hosts[] = mongodbHosts.split(",") ;
			for(String host : hosts) {
				String ip[] = host.split(":") ;
				addresses.add(new ServerAddress(ip[0], Integer.parseInt(ip[1]))) ;
			}
			// options
			Builder builder = MongoClientOptions.builder() ;
			builder.connectTimeout(1 * 1000) ;  // 获取连接超时时间  1 秒
			if(GlobalConfig.isDeploy)
				builder.socketTimeout(500); // 请求超时时间  100毫秒
			else 
				builder.socketTimeout(1000) ; // 测试环境，请求超时时间  1000毫秒
			
			m = new MongoClient(addresses, builder.build()) ;
			db = m.getDB(getDbName()) ;
			if(logger.isWarnEnabled())
				logger.warn("mongodb stat dao init ok! hosts={}", addresses);
		} catch (UnknownHostException e) {
			logger.error("init mongo db client error!", e);
		}
	}
	
	protected String getDbName(){
		return "pinche";
	}

}
