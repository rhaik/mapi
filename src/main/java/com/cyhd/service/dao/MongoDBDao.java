package com.cyhd.service.dao;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.cyhd.service.util.GlobalConfig;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;
import com.mongodb.WriteResult;

@Component
public class MongoDBDao {
	
	static Logger logger = LoggerFactory.getLogger(MongoDBDao.class);
	
	// 连接数据库
	private MongoClient m  ;
	// 数据库
	private DB db ;
	
	@PostConstruct
	public void init(){
		try {
			// mongo hosts
			List<ServerAddress> addresses = new ArrayList<ServerAddress>() ;
			String mongodbHosts = GlobalConfig.mongodb_hosts ;
			String hosts[] = mongodbHosts.split(",") ;
			for(String host : hosts) {
				String ip[] = host.split(":") ;
				addresses.add(new ServerAddress(ip[0], Integer.parseInt(ip[1]))) ;
			}
			// options
			Builder builder = MongoClientOptions.builder() ;
			builder.connectTimeout(1 * 2000) ;  // 获取连接超时时间  1 秒
			if(GlobalConfig.isDeploy)
				builder.socketTimeout(1500); // 请求超时时间  100毫秒
			else 
				builder.socketTimeout(1500) ; // 测试环境，请求超时时间  1000毫秒
			
			m = new MongoClient(addresses, builder.build()) ;
			db = m.getDB("pinche") ;
		} catch (UnknownHostException e) {
			logger.error("init mongo db client error!", e);
		}
	}
	
	/**
	 * 获得数据库里的集合对象
	 * @param collectionName
	 * @param db
	 * @return
	 */
	private DBCollection getDbCollection(String collectionName) {
		return db.getCollection(collectionName) ;
	}
	
	public void createIndex(BasicDBObject obj, String collectionName){
		DBCollection collection = getDbCollection(collectionName) ;
		collection.createIndex(obj);
	}
	
	/**
	 * 插入数据
	 * @param obj
	 * @param collectionName
	 */
	public WriteResult insert(BasicDBObject obj, String collectionName) {
		try{
			DBCollection collection = getDbCollection(collectionName) ; 
			return collection.insert(obj) ;
		}catch(Exception e){
			logger.error("Mongodb dao insert error",e);
		}
		return null;
	}
	
	/**
	 * 修改数据 
	 * @param criteria  修改的条件
	 * @param apply  修改的数据
	 * @param upsert  true 如果数据没有,则插入一条数据； false 如果数据没有，不插入一条数据
	 * @param multi   true 如果数据多条，则修改多条； false  如果数据多条，只修改一条
	 * @param collectionName
	 */
	public WriteResult update(BasicDBObject criteria, BasicDBObject apply, boolean upsert, boolean multi, String collectionName) {
		try{
			DBCollection collection = getDbCollection(collectionName) ; 
			return collection.update(criteria, apply, upsert, multi) ;
		}catch(Exception e){
			logger.error("Mongodb dao update error",e);
			return null;
		}
	}
	
	/**
	 * 删除数据
	 * @param criteria   删除的条件
	 * @param collectionName
	 */
	public WriteResult delete(BasicDBObject criteria, String collectionName) {
		
		DBCollection collection = getDbCollection(collectionName) ; 
		return collection.remove(criteria) ;
	}
	
	public DBCursor query(BasicDBObject criteria, String collectionName, int size) {
		
		DBCollection collection = getDbCollection(collectionName) ; 
		return collection.find(criteria).limit(size) ;
	}
	
	public DBCursor query(BasicDBObject criteria,BasicDBObject ret,BasicDBObject orderby, String collectionName, int size) {
		
		DBCollection collection = getDbCollection(collectionName) ; 
		return collection.find(criteria,ret).sort(orderby).limit(size) ;
	}
	
	public DBObject queryOne(BasicDBObject criteria, String collectionName) {
		DBCollection collection = getDbCollection(collectionName) ; 
		return collection.findOne(criteria) ;
	}
	
	public DBObject queryFirst(BasicDBObject orderby, String collectionName) {
		DBCollection collection = getDbCollection(collectionName) ;
		DBCursor dbCursor = collection.find().sort(orderby).limit(1) ;
		if(dbCursor.hasNext())
			return dbCursor.next() ;
		else 
			return null ;
	}
	
}
