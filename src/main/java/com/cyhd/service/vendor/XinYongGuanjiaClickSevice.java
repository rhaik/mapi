package com.cyhd.service.vendor;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.web.common.ClientInfo;

@Service("xinYongGuanjiaClickSevice")
public class XinYongGuanjiaClickSevice implements IVendorClickService {

	@Resource
	private UserInstalledAppService userInstalledAppService;
	
	@Override
	public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
		String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback_xygj.3w";
	    if (!GlobalConfig.isDeploy){
	    	callbackUrl = "http://www.mapi.lieqicun.cn/www/vendor/callback_xygj.3w";
	    }	
		try{
			String callBackURL = genCallbackUrlByUrlNotUrlEncoder(vendor, app, clientInfo.getIdfa(), callbackUrl );
			Map<String, String> signBeforeParames = new HashMap<>();
			signBeforeParames.put("idfa", clientInfo.getIdfa());
			signBeforeParames.put("appid", "3000"); 
			//"创力聚点" %E5%88%9B%E5%8A%9B%E8%81%9A%E7%82%B9
			signBeforeParames.put("channel", "创力聚点");
			signBeforeParames.put("callback", callBackURL);
			signBeforeParames.put("timestamp", String.valueOf(System.currentTimeMillis()/1000));
			String beforeSign = RequestSignUtil.getSortedRequestString(signBeforeParames)+"ASO@xygj";
			String sign = MD5Util.getMD5(beforeSign);
			signBeforeParames.put("channel", "%E5%88%9B%E5%8A%9B%E8%81%9A%E7%82%B9");
			signBeforeParames.put("callback", URLEncoder.encode(callBackURL, "utf-8"));
			signBeforeParames.put("sign", sign);
			
			logger.info("请求信用卡管家点击开始,idfa:{},request:{}",clientInfo.getIdfa(),signBeforeParames);
			String response = HttpUtil.get(vendor.getClick_url(), signBeforeParames);
			logger.info("请求信用卡管家点击结束,idfa:{},response:{}",clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			
			if(json != null){
				/**
				 * errorCode说明：
  				   1000     系统错误     
				   101*    第*<参数序号>个参数错误
				   1021     idfa已经激活过
				   1022     活动推广表中已存在
				   0000     成功添加
				 */
				String data = json.optString("errorCode");
				if("1021".equals(data)){
					try {
						userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
					} catch (Exception e) {}
				}
				return "0000".equals(data);
			}
			
		}catch(Exception e){
			logger.info("请求信用卡管家点击结束,idfa:{},cause:",clientInfo.getIdfa(),e);
		}
		return false;
	}

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask, ClientInfo clientInfo) {
		return onClickApp(vendor,null, app, appTask, clientInfo);
	}
public static void main(String[] args) throws UnsupportedEncodingException {
	String data = "3.0.0";
	System.out.println(URLEncoder.encode(data, "utf-8"));
}
}
