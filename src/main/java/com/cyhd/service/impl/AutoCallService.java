/*
 */
package com.cyhd.service.impl;

import java.util.Date;
import java.util.HashMap;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.IdGenerator;
import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.Pair;
import com.cyhd.common.util.job.AsyncJob;
import com.cyhd.common.util.job.JobHandler;
import com.cyhd.service.dao.db.mapper.AutoCallRecordMapper;
import com.cyhd.service.dao.po.AutoCallRecord;
import com.cyhd.service.util.GlobalConfig;

/**
 * 400 电话呼叫
 */
@Service
public class AutoCallService extends BaseService{ 
   
	private static final String ENTERPRISE_ID = "3002957";
	private static final String IVR_ID = "146";
	private static final String USER_NAME = "chuangyihudong";
	private static final String PWD = "cyhd_vo_2015";
	
    @Resource
	AutoCallRecordMapper autoCallRecordMapper;
    
	@Resource 
    private SmsService smsService;
	
	private IdGenerator idGen = new IdGenerator(GlobalConfig.server_id);
	
	private AsyncJob<CallModel> callJob4Auth = new AsyncJob<CallModel>("语音验证码", new JobHandler<CallModel>() {
		@Override
		public boolean handle(CallModel t) {
			if(t.getMobile().startsWith("999")){
				return false;
			}
			return makeCall(t.getType(), t.getOrderId(), t.getMobile(), t.getContent(),0,t.getIvrId(),t.getCode());
		}
	}, 20);
	
	public boolean loginAuthCode(String mobile,String code){
		CallModel model = new CallModel();
		model.setIvrId(IVR_ID);
		model.setCode(code);
		model.setMobile(mobile);
		model.setOrderId(System.currentTimeMillis());
		model.setContent("语音验证码");
	    return callJob4Auth.offer(model);
	}

    
    public AutoCallRecord getAutoCall(long orderId, int type){
    	return autoCallRecordMapper.getCallRecordByResource(orderId, type);
    }
    
    public Pair<Integer, String> getAutoCallState(long orderId){
    	AutoCallRecord record = autoCallRecordMapper.getCallRecordByResource(orderId, 1);
    	int state = AutoCallRecord.STATE_UNCALLED;
    	if(record == null){
    		state = AutoCallRecord.STATE_UNCALLED;
    	}else{
    		state = record.getCallstate();
    	}
    	String msg = AutoCallRecord.getCallStateDesc(state);
    	return new Pair<Integer, String>(state, msg);
    }
    
    /**
	 * 
	 * @param type 定义呼叫类型，目前是1
	 * @param resourceId 资源id，通常是订单id
	 * @param extraInfo 保留字段
	 * @param phone 被自动呼叫的号码
	 * @param subphone 分机号，为null或者""时表示没有分机
	 * @param content 呼叫内容
	 * @param oprid mis后台操作人员id
	 * @param ivrId 新增的语音验证码 后新增字段
	 * @return
	 * @throws TTYCException 
	 */
    protected boolean makeCall(int type, long resourceId, String phone, String content, long oprid,String ivrId,String code){
    	logger.info("make call: resourceid=" + resourceId + ", to="+phone + ", content=" + content+", opr="+oprid);
    	boolean result = false;
    	int ret = -1;
    	AutoCallRecord record = null; //autoCallRecordMapper.getCallRecordByResource(resourceId, type);
    	if(record == null){
	    	long uid = idGen.getNextId();
	    	record = new AutoCallRecord();
	        record.setId(uid);
	        record.setType(type);
	        record.setResourceid(resourceId);
    	}
    	record.setContent(content);
        record.setCreatetime(new Date());
        record.setCallstate(AutoCallRecord.STATE_UNCALLED);
        record.setExtrainfo("");
        record.setCallcount(1);
        record.setPhone(phone);
        record.setSubphone("");
        record.setOprid(oprid);
        
        boolean useSMS = false;//出现未知异常
        String response = null;
        try{
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("enterpriseId", ENTERPRISE_ID);
            params.put("interfaceType", "webCall");
            params.put("customerNumber", phone);
            
            params.put("userName", USER_NAME);
            params.put("pwd", MD5Util.getMD5(PWD));
//            
            params.put("sync", "1");
           //params.put("ivrId", 42+"");
          // params.put("ivrId", 307+"");//这个是原有的
            params.put("ivrId", ivrId);//很重要的流程 所以要动态输入
            
            //语音验证码 新增
            if(StringUtils.isNotBlank(code)){
            	params.put("paramNames", "code");
            	params.put("paramTypes", "1");
            	params.put("code", code);
            }
            
            String url= "http://ivoice.ccic2.com/interface/entrance/OpenInterfaceEntrance";
           // String url = "http://1010.ccic2.com/interface/entrance/OpenInterfaceEntrance";
           
            autoCallRecordMapper.insert(record);
           
            try{
            	int timeOut = 10000;
            	if(StringUtils.isNotBlank(code)){
            		timeOut = 37000;
            	}
            	
            	response = HttpUtil.get(url, params, timeOut);
            	 JSONObject responseJson = JSONObject.fromObject(response);
                 logger.info("拨打电话,response:{},mobile:{}",response,phone);
                
                 if(responseJson != null){
                 	if(responseJson.get("result") != null){
                 		ret = responseJson.getInt("result");
                 		if(ret == 1){
                 			result =  true;
                 		}
                 		
                 		if( !(ret == 0 || ret == 1)){ //ret＝0:手机空号，停机等
                 			if(StringUtils.isNotBlank(code)){//如果出现异常 那就发短信
                         		useSMS = true;
                         	}
                 		}
                 	}
                 }
            }catch(Exception ee){
            	logger.error("拨打电话异常, 电话:"+phone, ee);
            	if(StringUtils.isNotBlank(code)){//如果出现异常 那就发短信
            		useSMS = true;
            	}
            }
            logger.info("make call, url=" + url + ", params=" + params + ", ret=" + response);
           
        }catch(Exception e){
        	//e.printStackTrace();
            logger.error("AutoCallService.makeCall(uri,username,password) error,response:{}", e,response);
            if(StringUtils.isNotBlank(code)){//如果出现异常 那就发短信
        		useSMS = true;
        	}
        }finally{
        	if(useSMS){
        		smsService.sendVercode(phone, code);
        	}
        	if(result){
        		record.setCallstate(AutoCallRecord.STATE_REACHABLE);
        	}else{
        		record.setCallstate(AutoCallRecord.STATE_UNREACHABLE);
        	}
        	record.setCalltime(new Date());
        	autoCallRecordMapper.afterCall(record);
        }
        return result;
    }
    
    static class CallModel{
		private long orderId;
		private int type;
		private String mobile;
		private String content;
		
		//语音验证码新增类型
		//ivrId不同，流程不同
		private String ivrId;
		
		//语音验证码 新增内容：验证码
		private String code;
		
		
		public long getOrderId() {
			return orderId;
		}
		public void setOrderId(long orderId) {
			this.orderId = orderId;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getIvrId() {
			return ivrId;
		}
		public void setIvrId(String ivrId) {
			this.ivrId = ivrId;
		}
		public String getCode() {
			return code;
		}
		public void setCode(String code) {
			this.code = code;
		}
		
	}
    
    
}
