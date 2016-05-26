package com.cyhd.service.push.ios;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.notnoop.apns.APNS;
import com.notnoop.apns.ApnsNotification;
import com.notnoop.apns.ApnsService;
import com.notnoop.apns.ApnsServiceBuilder;
import com.notnoop.apns.EnhancedApnsNotification;
import com.notnoop.apns.PayloadBuilder;

/**
 * IOS push 发送处理者
 *
 * 错误码：
 * NO_ERROR(0),
    PROCESSING_ERROR(1),
    MISSING_DEVICE_TOKEN(2),
    MISSING_TOPIC(3),
    MISSING_PAYLOAD(4),
    INVALID_TOKEN_SIZE(5),
    INVALID_TOPIC_SIZE(6),
    INVALID_PAYLOAD_SIZE(7),
    INVALID_TOKEN(8),

    NONE(255),
    UNKNOWN(254);
 * @version 1.0
 */
@Service
public class IosPusher {

	@Resource
	PushServiceDelegate pushServiceDelegate;
	
    public static final Logger PUSHLOG = LoggerFactory.getLogger("push");
    
    private static final int IOS_BRAGE = 1;
    
   // private ApnsService iphoneService;
    
    private static AtomicInteger index = new AtomicInteger(1);
    
    private Map<String, ApnsService> iphoneServiceMap;
    
    public IosPusher(){
    	iphoneServiceMap = new ConcurrentHashMap<String, ApnsService>();
    }
    
    /**
     * 初始化 pushservice 
     * @param key
     */
//    @PostConstruct
//    public void initApnsService() {
//    	try{
//	        if(null==iphoneService){
//	            iphoneService = newApnsService(Configuration.getIPhonePushCer(),
//	                Configuration.getIPhonePushPwd());
//	        }
//    	}catch(Exception e){
//    		e.printStackTrace();
//    	}
//        if (iphoneService != null) {
//            iphoneService.start();
//        }
//    }
    
    public void initApnsServiceByBundleId(String bundleId){
    	
    	ApnsService iphoneService = null;
    	try{
	        if(iphoneServiceMap.get(bundleId) == null){
	        	//Configuration.getIPhonePushPwd()
	        	 iphoneService = newApnsService(Configuration.getIPhonePushCer(bundleId),
	        			 Configuration.getIPhonePushPwd());
	        	 iphoneServiceMap.put(bundleId, iphoneService);
	        }
	        
    	}catch(Exception e){
    		PUSHLOG.error("创建pushservice为null,cause by:{}",e);
    	}
        if (iphoneService != null) {
            iphoneService.start();
        }
    }
    
//    private void renewApnsService() {
//        if (iphoneService != null) {
//            iphoneService.stop();
//        }
//        initApnsService();
//    }
    
    private void renewApnsService(String bundleId) {
    	ApnsService iphoneService =  iphoneServiceMap.get(bundleId);
        if (iphoneService != null) {
            iphoneService.stop();
        }
    	initApnsServiceByBundleId(bundleId);
    }
    
    /**
     * 创建推送服务
     * @param pushCer
     * @param pushPwd
     * @return
     */
    private ApnsService newApnsService(final String pushCer,
            final String pushPwd) {
    	if(PUSHLOG.isInfoEnabled()){
    		PUSHLOG.info("init apns service, pushCer={}, pushPwd={}", pushCer, pushPwd);
    	}
        if (pushCer != null && pushCer.length() > 0) {
            ApnsServiceBuilder asb = APNS.newService().withCert(pushCer,
                    pushPwd);
            asb.withDelegate(pushServiceDelegate);
            asb.withNoErrorDetection();
            if (Configuration.PRODUCT.equalsIgnoreCase(Configuration
                    .getIOSPushServer())) {
                return asb.withProductionDestination().build();
            } else {
                return asb.withSandboxDestination().build();
            }
        }
        return null;
    }
    
    private int getIdentifier() {
        int ret = index.getAndIncrement();
        return Calendar.getInstance().get(Calendar.DAY_OF_YEAR) << 20
                | (ret & 0xFFFFF);
    }
    
    public boolean push2Apns(IosPushBean pushInfo) {
        String token = pushInfo.getToken();
        if (token == null || token.length() == 0 || token.equals("NULL")) {
            return false;
        }        
        //创建push对象
//        PayloadBuilder pb = APNS.newPayload().sound("push_default.mp3");
        PayloadBuilder pb = null ;
        pb = APNS.newPayload().sound("push_default.mp3");
        if(pushInfo.isNewsstand()) {
        	pb.forNewsstand();
        } else {
        	pushInfo.setBadge(IOS_BRAGE);
        }
        if (pushInfo.getBadge() > 0) {
            pb.badge(pushInfo.getBadge());
        }
        if (pushInfo.getAlertBody() != null && pushInfo.getAlertBody().length() > 0) {
            pb.alertBody(pushInfo.getAlertBody());
        }
        JSONObject params = pushInfo.getParams();
        if (params != null && params.size() > 0) {
            Iterator<String> it = params.keys();
            while (it.hasNext()) {
                String key = it.next();
                Object value = params.get(key);
                if (value != null) {
                    pb.customField(key, value);
                }
            }
        }
        String payload = pb.build();
        int identifier = getIdentifier();
        ApnsService iphoneService = iphoneServiceMap.get(pushInfo.getBundleId());
        if (pushInfo.getClientType() != null) {            
            if (iphoneService == null) {
               // initApnsService();
            	initApnsServiceByBundleId(pushInfo.getBundleId());
            	iphoneService = iphoneServiceMap.get(pushInfo.getBundleId());
            	if(iphoneService == null){
            		PUSHLOG.error("创建PushService 为null");
            		return false;
            	}
            
                iphoneService.testConnection();
            }
            try {
                ApnsNotification notify = new EnhancedApnsNotification(
                          identifier,
                          (int) (System.currentTimeMillis() / 1000 + 60 * 60),
                          token, payload); 
                iphoneService.push(notify);
                return true;
            } catch (Exception e) {
            	PUSHLOG.error("push to ios error",e);
                try {
                    long t1 = System.currentTimeMillis();
                    iphoneService.testConnection();
                    long t2 = System.currentTimeMillis();
                    PUSHLOG.info("testConnection iphoneservice," +
                       		" use time = " + (t2 - t1),e);
                } catch (Exception e1) {
                    PUSHLOG.error("renewApnsService  -- testConnection",e1);
                    renewApnsService(pushInfo.getBundleId());
                }
                return false;
            }
        }else{
            return false;
        }
    }
    
    public static void main(String[] args) {
        String bundleId = "com.miaoshuixiaozhushou.cn";

        IosPushBean pushInfo = new IosPushBean();
        pushInfo.setBundleId(bundleId);
        pushInfo.setToken("e5ee3bd2d276280ba66ee567294d1d15f9abbaed1fed38f739f52f42b2031fc6");
        pushInfo.setAlertBody("世界真美好，我想去看看");
        JSONObject params = new JSONObject();
        params.put("type", 111);
        pushInfo.setParams(params);

        IosPusher pusher = new IosPusher();
        boolean result = pusher.push2Apns(pushInfo);
        System.out.println("result:" + result);
        
    	//System.out.println(org.springframework.util.StringUtils.isEmpty("1.4.0"));
    	
        
    }

}
