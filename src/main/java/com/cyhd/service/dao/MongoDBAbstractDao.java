package com.cyhd.service.dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

public abstract class MongoDBAbstractDao {
	
	protected static Logger logger = LoggerFactory.getLogger(MongoDBAbstractDao.class);
	
	// 连接数据库
	protected MongoClient m  ;
	// 数据库
	protected DB db ;
	
	public abstract void init();
	
	protected abstract String getDbName();
	
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
		DBCollection collection = getDbCollection(collectionName) ; 
		return collection.insert(obj) ;
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
		
		DBCollection collection = getDbCollection(collectionName) ; 
		return collection.update(criteria, apply, upsert, multi) ;
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
