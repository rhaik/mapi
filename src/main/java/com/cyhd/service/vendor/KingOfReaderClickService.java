package com.cyhd.service.vendor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.util.CollectionUtil;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("kingOfReaderClickService")
public class KingOfReaderClickService implements IVendorClickService{

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {

		return clickApp(vendor, null, app, appTask, clientInfo);
	}
	
	public boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo)  {
		String url = null;
		try{
			String callback = genCallbackUrl(vendor, app, clientInfo.getIdfa());
			long timestamp = new Date().getTime()/1000;
			String source = "aso001cljd";
			List<String> signList = new ArrayList<>(8);
			signList.add("appId="+ app.getAppstore_id());
			signList.add("idfa="+clientInfo.getIdfa());
			signList.add("source="+source);
			signList.add("ip="+ clientInfo.getIpAddress());
			signList.add("timestamp="+ timestamp);
			signList.add("callback="+ callback);
			
			Collections.sort(signList);
			
			String joinStr = CollectionUtil.join(signList, "");
			String sign = MD5Util.getMD5(joinStr+"yPOEqiYGbncun7DX");
			
			Map<String, String> parames = new HashMap<String, String>(8);
			parames.put("appId",app.getAppstore_id());
			parames.put("idfa",clientInfo.getIdfa());
			parames.put("source",source);
			parames.put("ip",clientInfo.getIpAddress());
			parames.put("timestamp",String.valueOf(timestamp));
			parames.put("callback",callback);
			parames.put("sign",sign);
			
			url = vendor.getClick_url();
			String response = null;
			logger.info("请求2345点击接口开始：user:{},request:{},idfa:{}",user,url,clientInfo.getIdfa());
			response = HttpUtil.postByForm(url, parames);
			logger.info("请求2345点击接口结束：user:{},response:{},idfa:{}",user,response,clientInfo.getIdfa());
			JSONObject json = JSONObject.fromObject(response);
			if(json != null){
				if(json.has("code")){
					int code = json.optInt("code");
					//404已安装
					if(code == 404){
						if(user!=null){
							try {
								userInstalledAppService.insert(user.getId(),app.getId(), clientInfo.getDid(), app.getAgreement());
							} catch (Exception e) {}
							logger.info("user:{},idfa:{},已安装",user,clientInfo.getIdfa());
						}
					}
					return code == 0;
				}
			}
		} catch (Exception e) {
			logger.error("请求2345点击接口异常：request:{},idfa:{},cause by:{}",url,clientInfo.getIdfa(),e.getMessage());
			return false;
		}
		return false;
	}
}
