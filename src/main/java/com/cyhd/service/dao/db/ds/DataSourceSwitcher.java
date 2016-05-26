/*
 * Copyright (c) 2012-2022 mayi.com
 * All rights reserved.
 * 
 */
package com.cyhd.service.dao.db.ds;

 import java.util.HashSet;
import java.util.Random;

import org.springframework.util.Assert;
 
/**
 * Description:数据源切换控制器
 * @Creator zhangwenbin
 * @Date 2012-12-17
 * @version 1.0
 * if you have any problem,please contact zhangwenbin@mayi.com
 */
public class DataSourceSwitcher {
     @SuppressWarnings("rawtypes")
     private static final ThreadLocal contextHolder = new ThreadLocal();
    
     @SuppressWarnings("unchecked")
     public static void setDataSource(String dataSource) {
         Assert.notNull(dataSource, "dataSource cannot be null");
         contextHolder.set(dataSource);
     }
 
     public static void setdefault() {  
         contextHolder.remove();  
     } 
     
     public static void setMaster() {  
         contextHolder.remove();  
         contextHolder.set("master");  
     }       
     
     public static void setSlave() {  
         contextHolder.remove();  
         contextHolder.set("slave");  
     }  
     
     public static String getDataSource() {
         return (String) contextHolder.get();
     }
 
     public static void clearDataSource() {
         contextHolder.remove();
     }
    
     public static void  main(String agr[]){
    	Random random = new Random();
    	HashSet<Integer> set = new HashSet<Integer>();
    	while(true){
    		int a = random.nextInt(3);
    		set.add(a);
    		if(set.size()==3)
    		break;
    	}
    	System.out.println(set);    	
    }
     
     
     
 }