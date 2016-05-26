package com.cyhd.service.push.getui;

import static com.cyhd.service.push.PushConstants.newPushParam;

import java.util.List;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.service.push.PushBean;
import com.cyhd.service.push.PushConstants;
import com.cyhd.service.util.GlobalConfig;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.ITemplate;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.NotyPopLoadTemplate;
import com.gexin.rp.sdk.template.PopupTransmissionTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;


/**
 * 安卓消息推送
 *
 * @version 1.0
 */
@Service
public class GetuiPusher {

	public static final Logger PUSHLOG = LoggerFactory.getLogger("push");
	
	private static final String host = "http://sdk.open.api.igexin.com/apiex.htm";
	private static String appId_teacher = "pCnfaNlxvgAaxUoJWBa8nA";
	private static String appkey_teacher = "jKdyreWQWi8TFnnCvrKG7";
	private static String appmaster_teacher = "rQD3WNd8Df5tUkTF7l1312";
	
	private static String appId_parent = "x9Q0V1cj8WAigRDna3AiL1";
	private static String appkey_parent = "7hoiP4qU2V7lCHRJrrELo4";
	private static String appmaster_parent = "7wVk3PEVfz9EHPHiBo8G1";
	
    /**
     */
    public GetuiPusher() {
    	if(GlobalConfig.isDeploy){
    		appId_teacher = "qOXMJW3pj173fmN2B85vCA";
    		appkey_teacher = "7Xwwh7mlaa9fJoaYnVnfS9";
    		appmaster_teacher = "0Tqw5jzWFj7GJrA5apAD74";
    		
    		appId_parent = "rpZB6mCefi8K4CkPUSZnnA";
    		appkey_parent = "6fsR6NJYPKAnIt5uvsFKq2";
    		appmaster_parent = "q1BuC2Dk9g9XfZ7YvUM25A";
    	}
    	System.setProperty("gexin.rp.sdk.http.connection.timeout", String.valueOf(PushConstants.connect_timeout));
    	System.setProperty("gexin.rp.sdk.http.so.timeout", String.valueOf(PushConstants.so_timeout));
        push_teacher  = new IGtPush(host, appkey_teacher, appmaster_teacher);
        push_parent  = new IGtPush(host, appkey_parent, appmaster_parent);
    }

    // 推送主类
    private IGtPush push_teacher;
    
    private IGtPush push_parent;

    /**
     * @param pushInfo 支持单推和群推
     * @return
     */
    public String push(PushBean pushInfo) {
        if (pushInfo == null) {
            return "pushInfo is null";
        }
        List<String> tokens = pushInfo.getTokens();
        if (tokens == null || tokens.isEmpty()) {
            return "tokens.size is 0";
        }
        if (tokens.size() == 1) {
            return push2Single(pushInfo);
        } else {
            return push2List(pushInfo);
        }
    }
    
    protected IGtPush getPusher(PushBean pushInfo){
    	if(pushInfo.isPushToTeacher())
    		return push_teacher;
    	return push_parent;
    }
    protected String getAppId(PushBean pushInfo){
    	if(pushInfo.isPushToTeacher())
    		return appId_teacher;
    	return appId_parent;
    }
    private String getAppKey(PushBean pushInfo) {
    	if(pushInfo.isPushToTeacher())
    		return appkey_teacher;
    	return appkey_parent;
	}
    protected String push2Single(PushBean pushInfo) {
    	if(pushInfo.isIos())
    		return this.push2IosSingle(pushInfo);
    	else
    		return this.push2AndroidSingle(pushInfo);
    }
    
    protected String push2AndroidSingle(PushBean pushInfo){
    	String ret = push2AndroidNotifySingle(pushInfo);
    	push2AndroidTransmissionSingle(pushInfo);
    	return ret;
    }
    
    protected String push2AndroidNotifySingle(PushBean pushInfo) {
        try {
            ITemplate template = getAndroidNotificationTemplate(pushInfo);
            
            // 单推消息类型
            SingleMessage message = new SingleMessage();
            message.setData(template);
            message.setOffline(true); //用户当前不在线时，是否离线存储,可选
            message.setOfflineExpireTime(pushInfo.getExpireTime()); //离线有效时间，单位为毫秒，可选
            
            Target target = new Target();
            target.setAppId(getAppId(pushInfo));
            String token = pushInfo.getSingleToken();
            target.setClientId(token);
            
            // 单推
            IPushResult ret = getPusher(pushInfo).pushMessageToSingle(message, target);
            String info = "android notify push, pushInfo=" + pushInfo;
            String result = (String) ret.getResponse().get("result"); 
            if(PushConstants.status_ok.equals(result)) {
            	String status = (String) ret.getResponse().get("status");
            	if(PushConstants.status_successed_offline.equals(status)) {
            		result = "Android用户不在线" ;
            	}
            	PUSHLOG.info(info + " Push OK. APPID:" + this.getAppId(pushInfo) + ", msg="+result);
            }else{
            	PUSHLOG.error(info + " Push ERROR. APPID:" + this.getAppId(pushInfo) + ", msg="+result);
            }
            
            return result ;
        } catch (Exception e) {
            PUSHLOG.error("Android notify推送消息失败！", e);
            return "Android推送异常";
        }
    }
    
    protected String push2AndroidTransmissionSingle(PushBean pushInfo) {
        try {
            ITemplate template = getAndroidTransmissionTemplate(pushInfo);
            
            // 单推消息类型
            SingleMessage message = new SingleMessage();
            message.setData(template);
            message.setOffline(true); //用户当前不在线时，是否离线存储,可选
            message.setOfflineExpireTime(pushInfo.getExpireTime()); //离线有效时间，单位为毫秒，可选
            
            Target target = new Target();
            target.setAppId(getAppId(pushInfo));
            String token = pushInfo.getSingleToken();
            target.setClientId(token);
            
            // 单推
            IPushResult ret = getPusher(pushInfo).pushMessageToSingle(message, target);
            String info = "android notify push, pushInfo=" + pushInfo;
            String result = (String) ret.getResponse().get("result"); 
            if(PushConstants.status_ok.equals(result)) {
            	String status = (String) ret.getResponse().get("status");
            	if(PushConstants.status_successed_offline.equals(status)) {
            		result = "Android用户不在线" ;
            	}
            	//PUSHLOG.info(info + " Push OK. APPID:" + this.getAppId(pushInfo) + ", msg="+result);
            }else{
            	//PUSHLOG.error(info + " Push ERROR. APPID:" + this.getAppId(pushInfo) + ", msg="+result);
            }
            return result ;
        } catch (Exception e) {
            PUSHLOG.error("Android transmission推送消息失败！", e);
            return "Android推送异常";
        }
    }
    
    
    protected String push2IosSingle(PushBean pushInfo) {
        try {
        	TransmissionTemplate t = getIosTransmissionTemplate(pushInfo);

        	//APNTemplate t = new APNTemplate();
        	 //t.setPushInfo("aa", pushInfo.getBadge(), pushInfo.getAlertBody(), "default", "{}", "", "", "");
        	 
    		 SingleMessage sm = new SingleMessage();
    		 sm.setData(t);
    		 sm.setOffline(true); //用户当前不在线时，是否离线存储,可选
             sm.setOfflineExpireTime(pushInfo.getExpireTime()); //离线有效时间，单位为毫秒，可选
             
    		 IPushResult ret = getPusher(pushInfo).pushAPNMessageToSingle(getAppId(pushInfo), pushInfo.getSingleToken(), sm);
            
            // 单推
            String result = (String) ret.getResponse().get("result"); 
            if(PushConstants.status_ok.equals(result)) {
            	String status = (String) ret.getResponse().get("status");
            	if(PushConstants.status_successed_offline.equals(status)) {
            		result = "IOS用户不在线" ;
            	}
            }
            return result ;
        } catch (Exception e) {
            PUSHLOG.error("Ios推送消息失败！", e);
            return "Ios推送异常";
        }
        
    }
    
    public TransmissionTemplate getIosTransmissionTemplate(PushBean pushInfo) throws Exception {
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(getAppId(pushInfo));
        template.setAppkey(getAppKey(pushInfo));
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动。
        template.setTransmissionType(2);
        template.setTransmissionContent(pushInfo.getTransmissionContent());
     
        /*iOS 推送需要对该字段进行设置具体参数详见iOS模板说明*/
        template.setPushInfo("diandian", pushInfo.getBadge(), pushInfo.getAlertBody(), "default", pushInfo.getTransmissionContent(), "", "", "");
        /*template.setPushInfo("actionLocKey", 4, "message", "sound", 
        "payload", "locKey", "locArgs", "launchImage","ContentAvailable");*/
        return template;
    }
    
	protected String push2List(PushBean pushInfo) {
        try {
           
        } catch (Exception e) {
            //PUSHLOG.error("Android推送消息失败！", e);
            //return "Android推送消息异常";
        }
        return null;
    }
    
    /**
     * 弹出的透传消息
     * @return
     */
    public PopupTransmissionTemplate PopupTransmissionTemplateDemo(PushBean pushBean) {
		PopupTransmissionTemplate template = new PopupTransmissionTemplate();
		template.setAppId(getAppId(pushBean));
		template.setAppkey(getAppKey(pushBean));
		template.setText("");
		template.setTitle("");
		template.setImg("");
		template.setConfirmButtonText("");
		template.setCancelButtonText("");
		template.setTransmissionContent("111");
		template.setTransmissionType(1);

		return template;
	}

    /**
     * 透传消息
     * @return
     * @throws Exception
     */
	public TransmissionTemplate getAndroidTransmissionTemplate(PushBean pushBean)
			throws Exception {
		TransmissionTemplate template = new TransmissionTemplate();
		template.setAppId(getAppId(pushBean));
		template.setAppkey(getAppKey(pushBean));
		
		pushBean.getParams().put("title", pushBean.getAlertBody());
		template.setTransmissionType(2); // 收到消息是否立即启动应用，1为立即启动，2则广播等待客户端自启动
		template.setTransmissionContent(pushBean.getTransmissionContent());
		return template;
	}

	/**
	 * 点击通知打开网页
	 * @return
	 * @throws Exception
	 */
	public LinkTemplate linkTemplateDemo(PushBean pushInfo) throws Exception {
		LinkTemplate template = new LinkTemplate();
		template.setAppId(getAppId(pushInfo));
		template.setAppkey(getAppKey(pushInfo));
		template.setTitle("");
		template.setText("");
		template.setLogo("text.png");
		// template.setLogoUrl("");
		// template.setIsRing(true);
		// template.setIsVibrate(true);
		// template.setIsClearable(true);
		template.setUrl("http://www.baidu.com");
		// template.setPushInfo("actionLocKey", 1, "message", "sound",
		// "payload", "locKey", "locArgs", "launchImage");
		return template;
	}

	/**
	 * 点击通知启动应用
	 * @return
	 * @throws Exception
	 */
	public NotificationTemplate getAndroidNotificationTemplate(PushBean pushInfo)
			throws Exception {
		NotificationTemplate template = new NotificationTemplate();
		template.setAppId(getAppId(pushInfo));
		template.setAppkey(getAppKey(pushInfo));
		template.setTitle(pushInfo.getTitle());
		template.setText("");
		//template.setText(pushInfo.getAlertBody());
		template.setLogo("icon.png");
		// template.setLogoUrl("");
		// template.setIsRing(true);
		// template.setIsVibrate(true);
		// template.setIsClearable(true);
		template.setTransmissionType(1);
		template.setTransmissionContent(pushInfo.getTransmissionContent());
		return template;
	}

	/**
	 * 通知栏弹框下载模版
	 * @return
	 */
	public NotyPopLoadTemplate NotyPopLoadTemplateDemo(PushBean pushInfo) {
		NotyPopLoadTemplate template = new NotyPopLoadTemplate();
		// 填写appid与appkey
		template.setAppId(getAppId(pushInfo));
		template.setAppkey(getAppKey(pushInfo));
		// 填写通知标题和内容
		template.setNotyTitle("标题");
		template.setNotyContent("内容");
		// template.setLogoUrl("");
		// 填写图标文件名称
		template.setNotyIcon("text.png");
		// 设置响铃，震动，与可清除
		// template.setBelled(false);
		// template.setVibrationed(false);
		// template.setCleared(true);

		// 设置弹框标题与内容
		template.setPopTitle("弹框标题");
		template.setPopContent("弹框内容");
		// 设置弹框图片
		template.setPopImage("http://www-igexin.qiniudn.com/wp-content/uploads/2013/08/logo_getui1.png");
		template.setPopButton1("打开");
		template.setPopButton2("取消");

		// 设置下载标题，图片与下载地址
		template.setLoadTitle("下载标题");
		template.setLoadIcon("file://icon.png");
		template.setLoadUrl("http://gdown.baidu.com/data/wisegame/c95836e06c224f51/weixinxinqing_5.apk");
		template.setActived(true);
		template.setAutoInstall(true);
		template.setAndroidMark("");
		return template;
	}

    public static void main(String[] args) {
        PushBean pushInfo = new PushBean();
        pushInfo.setPushToTeacher(true);
        //pushInfo.setIos();
        pushInfo.addToken("9d5f1326900261d5a2cb6ca05b49f144");
        
        pushInfo.setTitle("这是标题");
        pushInfo.setAlertBody("测试ios消息4444");
        
        JSONObject params = newPushParam(1);
		params.put("order_id", 100001001);
		params.put("match_id",200000101);
        pushInfo.setParams(params);
        
        GetuiPusher pusher = new GetuiPusher();
        System.err.println(pusher.push2Single(pushInfo));
    }
    
}
