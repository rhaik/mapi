package com.cyhd.service.push;

import static com.cyhd.service.push.PushConstants.EARLIEST_PUSH_TIME;
import static com.cyhd.service.push.PushConstants.LATEST_PUSH_TIME;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.dao.po.User;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.Device;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.impl.DeviceService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.push.android.AndroidPushBean;
import com.cyhd.service.push.android.AndroidPusher;
import com.cyhd.service.push.android.umeng.UmengPusherUM;
import com.cyhd.service.push.getui.GetuiPusher;
import com.cyhd.service.push.ios.IosPushBean;
import com.cyhd.service.push.ios.IosPusher;
import com.cyhd.service.util.GlobalConfig;

/**
 * APP推送
 */
@Service
public class AppPushDelegate {

	public static final Logger PUSHLOG = LoggerFactory.getLogger("push");
    
	@Resource
    private IosPusher iosPusher;
	
    @Resource
    private DeviceService deviceService;
    
//    @Resource
//    private IosPusher iosPusher;
//    
//    @Resource
//    IXinTuiPusher ixinPusher;
    
    @Resource
    private UserService userService ;
    
    @Resource
    private GetuiPusher getuiPusher;
    
    @Resource
    private AndroidPusher androidPusher;
    
    public boolean push_use_getui = true;
    public boolean push_use_ixin = false;
    private volatile boolean loading = false;
    
    @PostConstruct
    public void reloadChannels(){
//    	if (loading)
//			return;
//    	try{
//    		loading = true;
//	    	String channels = propertySerivce.getPushChannels();
//			if(!StringUtils.isEmpty(channels)){
//				push_use_getui = false;
//				push_use_ixin = false;
//				String[] chs = channels.split(",");
//				for(String s:chs){
//					s = s.trim();
//					if(s.equalsIgnoreCase("getui")){
//						push_use_getui = true;
//					}else if(s.equalsIgnoreCase("ixin")){
//						push_use_ixin = true;
//					}
//				}
//			}
//    	}catch(Exception e){
//    		PUSHLOG.error("load android push channel error!", e);
//    	}finally{
//    		if(!push_use_getui && !push_use_ixin){
//				PUSHLOG.warn("load push channel error, set ixin push to true!");
//				push_use_ixin = true;
//			}
//    		loading = false;
//    		PUSHLOG.warn("android push channals getui={}, ixin={}", push_use_getui, push_use_ixin);
//    	}
    }
    
    public boolean isPushTime(long userid){
		Calendar cal = GregorianCalendar.getInstance();
		int t = (cal.get(Calendar.HOUR_OF_DAY) * 3600 + cal.get(Calendar.MINUTE) * 60) * 1000;
		return t > EARLIEST_PUSH_TIME && t < LATEST_PUSH_TIME;
	}
    
    public static PushResult getDefaultPushResult(){
    	PushResult result = new PushResult();
    	result.setErrcode(PushConstants.errcode_unknown);
        result.setSuccess(false);
        result.setClientType("unknown");
        return result;
    }
    /**
     * @see #push(long, String, JSONObject, boolean)
     * @param userId
     * @param alertBody
     * @param params
     * @return
     */
    PushResult push(long userId, String alertBody, JSONObject params){
    	return push(userId, alertBody, params, false);
    }
    
    /**
     * @param userId 推送用户ID
     * @param alertBody 推送消息内容
     * @param params 参数 例：{"data" : "value"}（不限于此，但必须为json格式。内容由客户端和服务器共同决定）
     * <b style="color:red;">需要向Android发送需要加上clientType,{@link UserSystemMessage#PUSH_CLIENT_TYPE_ANDROID}</b>
     * @param forcePush ,如果用户退出，是否还要push
     * @return
     */
    public PushResult push(long userId, String alertBody, JSONObject params, boolean forcePush) {
    	PushResult result = getDefaultPushResult();
    	String trackPush = "";
        if (userId <= 0) {
            PUSHLOG.error("推送消息用户ID无效！userId：" + userId + "，alertBody：" + alertBody);
            result.setErrMessage("推送用户ID无效");
            result.setErrcode(PushConstants.errcode_param_wrong);
            return result;
        }
        if(!isPushTime(userId)){
        	result.setErrMessage("不在用户设定PUSH时间内"); 
        	result.setErrcode(PushConstants.errcode_condition_filter);
        	PUSHLOG.warn("Do not push message because not in the push time gap！userId：" + userId + "，alertBody：" + alertBody);
            return result;
		}
        
        final String prefix = "Push delegate userid=" + userId;
        List<Device> devices = deviceService.getDevicesByUserId(userId);
        PUSHLOG.info(prefix + " get devices= {}", devices);
        if(devices == null || devices.isEmpty()){
        	PUSHLOG.warn("推送用户无注册设备！userId：" + userId + "，alertBody：" + alertBody);
        	result.setErrMessage("无注册设备");
        	result.setErrcode(PushConstants.errcode_device_not_exist);
            return result;
        }
      
        String errMessage = "";
        boolean success = false;
       // boolean isSingleDriver = devices.size() == 1;
        
        for(Device device : devices){
        	//PUSHLOG.info(prefix + " push device={}", device);
	        if(!device.isBind()){
	        	if(!forcePush){
	        		PUSHLOG.warn(trackPush+"推送用户已退出登陆！userId：" + userId + "，alertBody：" + alertBody);
	            	result.setErrMessage("用户退出登陆");
	            	result.setErrcode(PushConstants.errcode_logout);
	            	if(device.isIosDevice()) {
	            		result.setClientType("iPhone"); 
	            	} else {
	            		result.setClientType("Android");
	            	}
	                return result;
	        	}
	        }
	        int pushClientType = 0;
//	        if(params.has("type")){
//	        	pushType = params.getInt("type");
//	        }
	        if(params.has("clientType")){
	        	pushClientType = params.getInt("clientType");
	        }
	        boolean pushIos = pushClientType == 0 || pushClientType==UserSystemMessage.PUSH_CLIENT_TYPE_IOS || pushClientType==UserSystemMessage.PUSH_CLIENT_TYPE_ALL ;
	        boolean pushAndroid = pushClientType==UserSystemMessage.PUSH_CLIENT_TYPE_ANDROID|| pushClientType==UserSystemMessage.PUSH_CLIENT_TYPE_ALL;
	        
	        String token = device.getToken();
	        if (device.isIosDevice() ) {
	        	if(pushIos && StringUtil.isNotBlank(device.getBundle_id())){
	        		PUSHLOG.info("苹果设备不推送");
		        	result.setClientType("iPhone");
		            IosPushBean iosPushBean = new IosPushBean();
		            iosPushBean.setAlertBody(alertBody);
		            iosPushBean.setParams(params);
		            iosPushBean.setToken(token);
		            iosPushBean.setBundleId(device.getBundle_id());
		            //iosPushBean.setNewsstand(true);
		            
		             //User user = userService.getUserById((int)device.getUserid()) ;
		            boolean partSuccess = iosPusher.push2Apns(iosPushBean);
		            if (partSuccess) {
		            	if(PUSHLOG.isInfoEnabled()){
		            		PUSHLOG.info(trackPush+"push to ios ok! push bean=" + iosPushBean);
		            	}
		                result.setSuccess(true);
		            } else {
		            	result.setErrMessage("ios push异常");
		            	result.setErrcode(PushConstants.errcode_ios);
		            	if(PUSHLOG.isErrorEnabled())
		                	PUSHLOG.error(trackPush+"push to ios error! push bean=" + iosPushBean);
		            }
		            return result;
	        	}
	        } else if(pushAndroid){
	        	
	        	boolean pushUseUmeng = GlobalConfig.pusherIsUmeng();//GlobalConfig.pusherIsUmeng()||(device.getUserid() % 2 == 1);
	        	result.setClientType("android");
	        	//GlobalConfig.pusherIsUmeng()
	        	if(pushUseUmeng){
	        		UmengPusherUM pusher = new UmengPusherUM();
	        		try {
	        			if(StringUtils.isBlank(token) || device.isTokenUM() == false|| token.length() != 44){
	        				PUSHLOG.info("推送不是友盟:token:{},userId:{},tokenType:{}",token,device.getUserid(),device.getTokentype());
	        				continue;
	        			}
						PushResult pr = pusher.push(token, GlobalConfig.PUSH_TITLE, alertBody,params);
						result.setSuccess(pr.isSuccess());
						if(pr.isSuccess() == false){
							result.setErrMessage(pr.getErrMessage());
							result.setErrcode(pr.getErrcode());
						}
					} catch (Exception e) {
						result.setSuccess(false);
						result.setErrMessage(e.getMessage());
						PUSHLOG.error(trackPush+",友盟推送失败,userId:{},token:{},alert:{}" ,device.getUserid(),device.getToken(),alertBody );
					}
	        	}else{
	        		//增加token的长度判断 44 是友盟
	        		if(StringUtils.isBlank(token) || device.isTokenGetui()==false || token.length() == 44){
	        			PUSHLOG.info("不是极光推送,token:{},userId:{},tokenType:{}",token,device.getUserid(),device.getTokentype());
	        			continue;
	        		}
	        		AndroidPushBean androidPushBean = new AndroidPushBean();
	        		androidPushBean.setAlertBody(alertBody);
	        		androidPushBean.setTitle("");
	        		androidPushBean.setDeviceToken(token);
	        		androidPushBean.setParams(params);
	        		cn.jpush.api.push.PushResult pushResult = androidPusher.push(androidPushBean);
		        	if(pushResult.isResultOK()){
		        		if(PUSHLOG.isInfoEnabled()){
		        			PUSHLOG.info(trackPush+"push to android ok! push bean=" + androidPushBean);
		        		}
		        		result.setPushRecordId(pushResult.msg_id);
		        		result.setSuccess(true);
		        	}else{
		        		result.setErrMessage("android push异常");
		            	result.setErrcode(PushConstants.errcode_ios);
		            	if(PUSHLOG.isErrorEnabled())
		                	PUSHLOG.error(trackPush+"push to android error! push bean=" + androidPushBean);
		            }
	        	}
	            return result;
	        }
        }
        if(errMessage.length() > 0){
        	errMessage = errMessage.substring(0, errMessage.length() - 1);
        }
        result.setErrMessage(errMessage);
        if(!success){
        	result.setErrcode(PushConstants.errcode_android);
        }
        result.setSuccess(success);
        return result;
    }
    
}
