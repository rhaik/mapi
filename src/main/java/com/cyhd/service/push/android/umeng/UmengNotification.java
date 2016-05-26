package com.cyhd.service.push.android.umeng;

import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.service.push.PushResult;

public abstract class UmengNotification {
	// This JSONObject is used for constructing the whole request string.
	protected final JSONObject rootJson = new JSONObject();
	
	// This object is used for sending the post request to Umeng
	//protected HttpClient httpclient = new DefaultHttpClient();
	
	// The host
	protected static final String host = "http://msg.umeng.com";
	
	// The upload path
	protected static final String uploadPath = "/upload";
	
	// The post path
	protected static final String postPath = "/api/send";
	
	// The app master secret
	protected String appMasterSecret;
	
	// The user agent
	protected final String USER_AGENT = "Mozilla/5.0";
	
	protected static final Logger logger = LoggerFactory.getLogger("push");
	// Keys can be set in the root level
	protected static final HashSet<String> ROOT_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"appkey", "timestamp", "type", "device_tokens", "alias", "alias_type", "file_id", 
			"filter", "production_mode", "feedback", "description", "thirdparty_id"}));
	
	// Keys can be set in the policy level
	protected static final HashSet<String> POLICY_KEYS = new HashSet<String>(Arrays.asList(new String[]{
			"start_time", "expire_time", "max_send_num"
	}));
	
	// Set predefined keys in the rootJson, for extra keys(Android) or customized keys(IOS) please 
	// refer to corresponding methods in the subclass.
	public abstract boolean setPredefinedKeyValue(String key, Object value) throws Exception;
	public void setAppMasterSecret(String secret) {
		appMasterSecret = secret;
	}
	public PushResult send() throws Exception {
        String url = host + postPath;
        String postBody = rootJson.toString();
        String sign = DigestUtils.md5Hex(("POST" + url + postBody + appMasterSecret).getBytes("utf8"));
        url = url + "?sign=" + sign;
//        HttpPost post = new HttpPost(url);
//        post.setHeader("User-Agent", USER_AGENT);
//        StringEntity se = new StringEntity(postBody, "UTF-8");
//        post.setEntity(se);
//        // Send the post request and get the response
//        HttpResponse response = client.execute(post);

        	HttpClient client = new HttpClient();
        	PostMethod post = new PostMethod(url);
        	post.setRequestHeader("User-Agent", USER_AGENT);
        	RequestEntity requestEntity = new StringRequestEntity(postBody, "text/json", "utf-8");
			post.setRequestEntity(requestEntity );
			int code = client.executeMethod(post);
        	PushResult pushRet = new PushResult();
        	if(code == HttpStatus.SC_OK){
        		String resp = post.getResponseBodyAsString();
	        	logger.info("友盟返回:"+resp);
	        	if(StringUtils.isNotBlank(resp)){
	        		JSONObject obj = new JSONObject(resp);
	        		// "ret":"SUCCESS/FAIL",
	        		if(obj != null){
	        			String ret = obj.getString("ret");
	        			if("SUCCESS".equalsIgnoreCase(ret)){
	        				pushRet.setSuccess(true);
	        				logger.info(postBody+"ok");
	        			}else{
	        				pushRet.setSuccess(false);
	        				try{
	        					JSONObject json = obj.getJSONObject("data");
	        					pushRet.setErrcode(json.getInt("error_code"));
	        				}catch(Exception e){
	        					logger.error("发送信息出错:requestBody:{},\ncause by:{}",postBody,e);
	        				}
	        			}
	        		}
	        	}
        	}else{
        		String resp = post.getResponseBodyAsString();
        		logger.error("发送失败:response:{}",resp);
        		pushRet.setErrcode(code);
        		pushRet.setErrMessage(resp);
        		pushRet.setSuccess(false);
        	}
        	/***
        	 * {
  "ret":"SUCCESS/FAIL", // 返回结果，"SUCCESS"或者"FAIL"
  "data": 
    {
      // 当"ret"为"SUCCESS"时,包含如下参数:
          // 当type为unicast、listcast或者customizedcast且alias不为空时:
          "msg_id":"xx" 
          // 当type为于broadcast、groupcast、filecast、customizedcast
          且file_id不为空的情况(任务)
          "task_id":"xx"

      // 当"ret"为"FAIL"时,包含如下参数:
      "error_code":"xx" // 错误码详见附录I。

      //如果开发者填写了thirdparty_id, 接口也会返回该值。
      "thirdparty_id": "xx"
    }  
}

        	 */
//        int status = response.getStatusLine().getStatusCode();
//        System.out.println("Response Code : " + status);
//        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//        StringBuffer result = new StringBuffer();
//        String line = "";
//        while ((line = rd.readLine()) != null) {
//            result.append(line);
//        }
//        System.out.println(result.toString());
//        if (status == 200) {
//            System.out.println("Notification sent successfully.");
//        } else {
//            System.out.println("Failed to send the notification!");
//        }
        return pushRet;
    }
	
	
}
