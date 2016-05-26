package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.codehaus.groovy.antlr.treewalker.PreOrderTraversal;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.constants.PropertiesConstants;
import com.cyhd.service.dao.db.mapper.PropertiesMapper;
import com.cyhd.service.dao.po.Properties;
import com.cyhd.service.util.GlobalConfig;

/**
 * system key-value information service
 *
 */
@Service
public class PropertiesService extends BaseService {

	@Resource
	private PropertiesMapper propertiesMapper ;
	
	
	public String getAndroidVersion(){
		Properties keyvalue = propertiesMapper.get(PropertiesConstants.android_app_version, GlobalConfig.server_type) ;
		if(keyvalue == null)
			return null;
		return keyvalue.getCvalue();
	}
	
	public String getIosVersion(){
		Properties keyvalue = propertiesMapper.get(PropertiesConstants.ios_app_version, GlobalConfig.server_type) ;
		if(keyvalue == null)
			return null;
		return keyvalue.getCvalue();
	}

	/**
	 * 获取文章分享的域名
	 * @return
	 */
	public String getShareHosts(){
		Properties keyvalue = propertiesMapper.get(PropertiesConstants.article_share_hosts, GlobalConfig.server_type) ;
		if(keyvalue == null)
			return null;
		return keyvalue.getCvalue();
	}
	
	public boolean updateIosAppVersion(String content){
		Properties kv = new Properties() ;
		kv.setCkey(PropertiesConstants.ios_app_version); 
		kv.setScope(GlobalConfig.server_type);
		kv.setCvalue(content);
		kv.setEstate(Constants.ESTATE_Y);
		kv.setCreatetime(GenerateDateUtil.getCurrentDate()); 
		kv.setUpdatetime(GenerateDateUtil.getCurrentDate());
		return propertiesMapper.insert(kv)>=1;
	}
	
	public boolean updateAndroidAppVersion(String content){
		Properties kv = new Properties() ;
		kv.setCkey(PropertiesConstants.android_app_version); 
		kv.setScope(GlobalConfig.server_type);
		kv.setCvalue(content);
		kv.setEstate(Constants.ESTATE_Y);
		kv.setCreatetime(GenerateDateUtil.getCurrentDate()); 
		kv.setUpdatetime(GenerateDateUtil.getCurrentDate());
		return propertiesMapper.insert(kv)>=1;
	}
	
	
	public boolean updatePushChannels(String content){
		Properties kv = new Properties() ;
		kv.setCkey(PropertiesConstants.android_push_channels); 
		kv.setScope(GlobalConfig.server_type);
		kv.setCvalue(content); 
		kv.setEstate(Constants.ESTATE_Y);
		kv.setCreatetime(GenerateDateUtil.getCurrentDate()); 
		kv.setUpdatetime(GenerateDateUtil.getCurrentDate());
		
		return propertiesMapper.insert(kv)>=1;
	}
	
	public String getPushChannels(){
		Properties keyvalue = propertiesMapper.get(PropertiesConstants.android_push_channels, GlobalConfig.server_type) ;
		if(keyvalue == null)
			return null;
		return keyvalue.getCvalue();
	}
	
	public String[] getMemcachedHosts() {
		Properties keyvalue = propertiesMapper.get(PropertiesConstants.memcached_hosts, GlobalConfig.server_type) ;
		if(keyvalue == null)
			return null;
		if(StringUtils.isEmpty(keyvalue.getCvalue())){
			return null;
		}
		return keyvalue.getCvalue().split(",");
	}
	/**
	 * 获得限时任务和转发任务的更新时间提示
	 * @param key :
	 *  {@link PropertiesConstants#APP_TASK_HINT } 
	 *  or
	 *   {@link PropertiesConstants#ARTICLE_TASK_HINT}
	 *   <br/>
	 *   最好
	 * @return
	 */
	public String getTaskUpdateHint(String key){
		Properties keyvalue = propertiesMapper.get(key, GlobalConfig.server_type) ;
		if(keyvalue != null){
			return keyvalue.getCvalue();
		}
		return null;
	}
	/***
	 * 快速任务中的过滤列表
	 * @return
	 */
	public String getQuickTaskFiterList(){
		Properties kv = propertiesMapper.get(GlobalConfig.QUICK_TASK_FILTER_KEY_NAME, GlobalConfig.server_type);
		return kv == null?null:kv.getCvalue();
	}
	
	public String getPropertiesValue(String key){
		Properties kv = propertiesMapper.get(key,  GlobalConfig.server_type);
		return kv == null?null:kv.getCvalue();
	}
}
