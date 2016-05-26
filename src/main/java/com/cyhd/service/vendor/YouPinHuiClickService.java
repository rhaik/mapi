package com.cyhd.service.vendor;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppVendor;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.web.common.ClientInfo;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 优品惠的点击服务
 * Created by hy on 9/24/15.
 */
@Service("YouPinHuiClickService")
public class YouPinHuiClickService implements IVendorClickService{

    @Override
    public boolean onClickApp(AppVendor vendor, User user, App app, AppTask appTask, ClientInfo clientInfo) {
        boolean result = false;

        String clickUrl = vendor.getClick_url();
        Map<String, String> params = new HashMap<>();
        params.put("ip", clientInfo.getIpAddress());
        params.put("from", "4");
        params.put("appid", String.valueOf(app.getId() * 11 + 997));
        params.put("ifa", clientInfo.getIdfa());
        params.put("mac", "02:00:00:00:00:00");

        //回调地址
        String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "http://mapi.lieqicun.cn/www/vendor/callback.3w";
        }
        params.put("callback_url", callbackUrl);

        try {
            String response = HttpUtil.get(clickUrl, params);

            logger.info("YouPinHuiClickService click, response:{}, user:{}, app:{}", response, user, app);

            JSONObject json = JSONObject.fromObject(response);

            //调用成功
            if (json != null && json.has("code") &&  "0".equals(json.getString("code"))){
                result = true;
            }

            logger.info("YouPinHuiClickService click, result:{}", result);
        } catch (Exception e) {
            logger.error("YouPinHuiClickService click error, user:{}, app:{}, error:{}", user, app, e);
        }

        return result;
    }

	@Override
	public boolean onClickApp(AppVendor vendor, App app, AppTask appTask,
			ClientInfo clientInfo) {
		 boolean result = false;

        String clickUrl = vendor.getClick_url();
        Map<String, String> params = new HashMap<>();
        params.put("ip", clientInfo.getIpAddress());
        params.put("from", "4");
        params.put("appid", String.valueOf(app.getId() * 11 + 997));
        params.put("ifa", clientInfo.getIdfa());
        params.put("mac", "02:00:00:00:00:00");

        //回调地址
        String callbackUrl = "http://third.miaozhuandaqian.com/www/vendor/callback.3w";
        if (!GlobalConfig.isDeploy){
            callbackUrl = "http://mapi.lieqicun.cn/www/vendor/callback.3w";
        }
        params.put("callback_url", callbackUrl);

        try {
            String response = HttpUtil.get(clickUrl, params);

            logger.info("YouPinHuiClickService click, response:{}, idfa:{}, app:{}", response, clientInfo.getIdfa(), app);

            JSONObject json = JSONObject.fromObject(response);

            //调用成功
            if (json != null && json.has("code") &&  "0".equals(json.getString("code"))){
                result = true;
            }

            logger.info("YouPinHuiClickService click, result:{}", result);
        } catch (Exception e) {
            logger.error("YouPinHuiClickService click error, idfa:{}, app:{}, error:{}", clientInfo.getIdfa(), app, e);
        }

        return result;
	}
}
