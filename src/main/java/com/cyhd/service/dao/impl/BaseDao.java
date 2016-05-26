/*
 * Copyright (c) 2012-2022 mayi.com
 * All rights reserved.
 * 
 */
package com.cyhd.service.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.service.dao.db.ds.DataSourceSwitcher;

/**
 * Description:manager层基础类，引入一些基本的dao层操作管理对象，所有的数据源切换，只能在manager层处理
 * @Creator zhangwenbin
 * @Date 2012-12-17
 * @version 1.0
 * if you have any problem,please contact zhangwenbin@mayi.com
 */

public class BaseDao {
	
	static Logger logger = LoggerFactory.getLogger(BaseDao.class);
    
    public void setDataSource(String dataSource) {
//        DBLOG.info("setDataSource:"+dataSource);
        DataSourceSwitcher.setDataSource(dataSource);
    }

    public void setdefault() {  
//        DBLOG.info("setDataSource:master");
        DataSourceSwitcher.setdefault();
    } 
    
    public void setMaster() {  
//        DBLOG.info("setDataSource:master");
        DataSourceSwitcher.setMaster();
    }       
    
    public void setSlave() {
//        DBLOG.info("setDataSource:slave");
        DataSourceSwitcher.setSlave();  
    }  
    
    public String getDataSource() {
        return DataSourceSwitcher.getDataSource();
    }

    public void clearDataSource() {
        DataSourceSwitcher.clearDataSource();
    }
    
}
