package com.cyhd.service.vendor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONObject;

@Service("weiLeClickService")
public class WeiLeClickService implements IVendorClickService {

	private final String KEY = "39c917b0c2c552721ea2bd18302034b2";
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, user, app, appTask, clientInfo);
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return clickApp(vendor, null, app, appTask, clientInfo);
	}
	
	public boolean clickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		String userid = user == null ? "下游渠道": String.valueOf(user.getId());
		StringBuilder sBuilder= new StringBuilder(320);
		//http://cp.weile.com/api/ASOfengbao/cpid/20001/
		//long cpid = 20001;
		long timestamp = System.currentTimeMillis()/1000;
		
		sBuilder.append(vendor.getClick_url());
		sBuilder.append("?appid=").append(app.getAppstore_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		sBuilder.append("&timestamp=").append(timestamp);
		
		Map<String, String> parameters = new HashMap<>();
		parameters.put("appid", app.getAppstore_id());
		parameters.put("idfa", clientInfo.getIdfa());
		parameters.put("ip", clientInfo.getIpAddress());
		parameters.put("timestamp", timestamp+"");
		
		//这个StringBuilder不多于 httputil中也要去拼接 也不知道里面会不会修改Map的值
		String preSign = RequestSignUtil.getSortedRequestString(parameters);
		String signData = MD5Util.getMD5(preSign+KEY);
		
		sBuilder.append("&sign=").append(signData);
		String url = sBuilder.toString();
		String response = null;
		
		try{
			logger.info("请求微乐开始,request:{},userId:{}",url,userid);
			response = HttpUtil.get(url, null);
			logger.info("请求微乐结束,response:{},request:{},userId:{}",response,url,userid);
			JSONObject jsonObject = JSONObject.fromObject(response);
			return jsonObject != null && jsonObject.optInt("code", 1) == 0;
		}catch(Exception exception){
			logger.info("请求微乐异常,request:{},response:{},userId:{},cause by:{}",url,response,userid,exception);
		}
		return false;
	}
}
