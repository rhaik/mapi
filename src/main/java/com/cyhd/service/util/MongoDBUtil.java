package com.cyhd.service.util;

import java.util.Date;

import com.mongodb.DBObject;

public class MongoDBUtil {
	
	public static final String stat_db = "stat";
	public static final String lbs_db = "lbs";
    
    /**
     * 经纬度－千米系数，即K经纬度值表示一公里
     * 
     * double R = 6371.004;
     * double B = Math.PI * (R/180); // 111.1949964577288
     * K = 1/B; // 0.008993210412845813
     * 
     */
    public static final double K = 0.00899321;
	
	public static long toTimeStamp(Date date){
		return date.getTime();
	}
	
	public static Date toDate(String ts){
		long t = Long.parseLong(ts);
		return new Date(t);
	}
	
	public static String getString(DBObject dbObject, String key){
		if(dbObject.containsField(key)){
			Object v = dbObject.get(key);
			if(v == null)
				return null;
			return v.toString();
		}
		return null;
	}
	
	public static int getInt(DBObject dbObject, String key){
		if(dbObject.containsField(key)){
			Object v = dbObject.get(key);
			if(v == null)
				return 0;
			return Integer.parseInt(v.toString());
		}
		return 0;
	}
	
	public static long getLong(DBObject dbObject, String key){
		if(dbObject.containsField(key)){
			Object v = dbObject.get(key);
			if(v == null)
				return 0;
			return Long.parseLong(v.toString());
		}
		return 0;
	}
	
	public static Double getDouble(DBObject dbObject, String key){
		if(dbObject.containsField(key)){
			Object v = dbObject.get(key);
			if(v == null)
				return 0D;
			return Double.parseDouble(v.toString());
		}
		return 0D;
	}
	
	public static Date getDate(DBObject dbObject, String key){
		if(dbObject.containsField(key)){
			Object v = dbObject.get(key);
			if(v == null)
				return null;
			return toDate(v.toString());
		}
		return null;
	}
	
	
}
