/*
 * Copyright (c) 2012-2022 mayi.com
 * All rights reserved.
 * 
 */
package com.cyhd.service.dao.db.ds;
 
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Description:动态数据源，供数据源切换使用
 * @Creator zhangwenbin
 * @Date 2012-12-17
 * @version 1.0
 * if you have any problem,please contact zhangwenbin@mayi.com
 */ 
public class DynamicDataSource extends AbstractRoutingDataSource {
 
    @Override
     protected Object determineCurrentLookupKey() {
         return DataSourceSwitcher.getDataSource();
     }
 
}