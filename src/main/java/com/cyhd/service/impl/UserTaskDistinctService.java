package com.cyhd.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.User;


@Service
public class UserTaskDistinctService extends BaseService {

	private static Logger servicelog = LoggerFactory.getLogger("third");
	
	private volatile boolean falg = false;
	
	@Resource
	private UserService userService;
	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Resource
	private AppTaskService appTaskService;
	
	private int start = 0;
	public void startDistinct() {
		try{
			if(falg){
				logger.warn("startDistinct is running!");
				return;
			}
			falg = true;
			logger.info("start startDistinct task!");
			int i = 0;
			int size = 500;
			while(i < 5) {
				List<User> list = userService.getList(start, size);
				logger.info("startDistinct task {}!", size);
				if(list == null || list.size()==0) {
					break;
				}
				if(distinct(list)) {
					logger.info("startDistinct task success {}!", size);
				}
				start = start + size;
				++i;
			}
			 
			logger.info("end startDistinct task!");
		}catch(Exception e){
			logger.error("startDistinct error!", e);
		}finally {
			falg = false;
		}
	}
	
	public boolean distinct(List<User> list) {
		StringBuilder idfaStr = new StringBuilder();
		HashMap<String, User> map = new HashMap<String, User>();
		for(User u:list) { 
			idfaStr.append(u.getIdfa()).append(",");
			map.put(u.getIdfa(), u);
			
		}
		idfaStr.deleteCharAt(idfaStr.lastIndexOf(",")); 
		String url = "http://cp.api.youmi.net/midiapi/querya/";
		
		String appId = "787786130";
		HashMap<String, String> params = new HashMap<String, String>(); 
		params.put("idfa", idfaStr.toString());
		params.put("appid", appId); 
		
		App app = appTaskService.getAppByAppStoreId(appId);
		String rs ="";
		try {
			servicelog.info("url:{},params:{}", url, params.toString());
			rs = HttpUtil.postByForm(url, params);
			servicelog.info("retrun: {}", rs);
			JSONObject json = new JSONObject(rs);  
			if(json != null && !json.has("c")) {
				Iterator iterator = json.keys();
				while (iterator.hasNext()) {
		            String key = (String) iterator.next();
		            String value = json.getString(key); 
		            if("1".equals(value)) {
		            	User u = map.get(key);
		            	userInstalledAppService.insert(u.getId(), app.getId(), u.getDid(), app.getAgreement());
		            }
		        }
				return true;
			}
			return false;
		} catch(Exception e) {
			servicelog.info("distinct error:" + e.getMessage());
			return false;
		}
	}
}
