package com.cyhd.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.web.common.ClientInfo;

@Service
public class SystemService {

	@Resource
	private PropertiesService propertiesService ;
	
	public LiveAccess<String> integralWall_android = new LiveAccess<String>(Constants.half_hour_millis, null);
	
	/***
	 * 获得客户端积分墙的配置
	 * @param clientInfo
	 * @return
	 */
	public String getClientIntegralWallConf(ClientInfo clientInfo){
		String data = null;
		//非ios 就是安卓
		if(!clientInfo.isIos()){
			data = integralWall_android.getElement();
			if(data == null){
				//数据库存放的数据格式是 -> 小乐任务:5,小有任务:5,小趣任务:4,小点任务:4,小万任务:4,小贝任务:4,小迪任务:4
				data = propertiesService.getPropertiesValue(GlobalConfig.INTEGAL_SHOW_ANDROID_CONF);
				if(data != null){
					String[] kvs = data.split(",");
					StringBuilder sb = new StringBuilder(128);
					sb.append("{");
					String[] kvSrc = null;
					for(String kv:kvs){
						kvSrc = kv.split(":");
						if(kvSrc.length == 2){
							sb.append('"').append(kvSrc[0].trim()).append('"').append(':').append(kvSrc[1].trim());
							sb.append(',');
						}
					}
					sb.deleteCharAt(sb.lastIndexOf(","));
					sb.append("}");
					data = sb.toString();
					integralWall_android = new LiveAccess<String>(Constants.half_hour_millis, data);
				}
			}
		}
		return data;
	}
}
