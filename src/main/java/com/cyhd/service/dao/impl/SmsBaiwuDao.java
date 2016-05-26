package com.cyhd.service.dao.impl;

import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.SmsDao;
import com.cyhd.service.util.SmsUtil;

/**
 * 百悟短信接入：
 * 联系人：
 * @author luckyee
 *
 */
@Service
public class SmsBaiwuDao implements SmsDao {

	private static final Logger logger = LoggerFactory.getLogger("sms");
	
	//@Resource
	//private SmsRecordService smsRecordService ;

	private String subPath = "http://cloud.baiwutong.com:8080/post_sms.do";
	private static String ENCODING = "UTF-8";
	
	private String userId = "wj1262";
	private String password = "7698tj";
	private String busi = "1755961531";
	private String md5 = "646b70e553087b071376f462c16b8379";
	
	private static final int timeout = 5000;

	@Override
	public boolean sendSms(String mobile, String content, int type, int channel) {
		return sendSms(new String[] { mobile }, content, type, channel );
	}

	@Override
	public boolean sendSms(String[] mobiles, String content, int type , int channel) {
		
		String logmessage = "SmsBaiwuDao sendSms type:" + type + ", mobiles:" + StringUtils.join(mobiles, ",") + ",content:" + content + ",channel:" + channel + "," ;
		
		if (content == null) {
			logger.error(logmessage + "sendSms parameter error! content==null or no mobile destination!");
			return false;
		}
		// 转字符串 并过滤测试帐号
		String ms = SmsUtil.converMobiles(mobiles);
		if(ms.length() == 0) {// 全部为测试帐号
			return true ;
		}
		if (logger.isInfoEnabled()) {
			logger.info(logmessage + "start");
		}
		String responseCode = "unknown error" ;
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("id", userId);
			params.put("MD5_td_code",md5 );
			params.put("mobile", ms);
			params.put("msg_content", content);
			params.put("corp_msg_id", "");
			params.put("ext", "");

			String response = HttpUtil.postByForm(subPath, params, timeout, timeout, 1, ENCODING);
//			if (logger.isInfoEnabled()) {
//				logger.info(logmessage + " SendSms returns: " + response);
//			}
			responseCode=response;
			boolean success = response.startsWith("0#");
//			DocumentBuilder parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//			Document document = parser.parse(new InputSource(new StringReader(response)));
//			Element root = document.getDocumentElement();
//			responseCode = root.getTextContent();
			if(success){
				if(logger.isInfoEnabled())
					logger.info(logmessage + "SendSms success.");
				// 添加sms record
				//smsRecordService.addDownSms(type, Constants.server_type_bw, StringUtils.join(mobiles, ","), mobiles.length, content, Constants.sms_record_success, "") ;
				return true;
			}else
				logger.error(logmessage + "SendSms error, returns {}.", response);
		} catch (Exception e) {
			logger.error(logmessage + " error", e);
		}
		//添加sms record
		//smsRecordService.addDownSms(type, Constants.server_type_bw, StringUtils.join(mobiles, ","), mobiles.length, content, Constants.sms_record_fail, responseCode) ;
		return false;
	}
	
	public int queryBalance(){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("id", userId);
			params.put("pwd",password );
			String response = HttpUtil.postByForm("http://cloud.baiwutong.com:8080/get_balance.do", params, timeout, timeout, 1, ENCODING);
			if (logger.isInfoEnabled()) {
				logger.info("Baiwu SendSms returns: {}", response);
			}
			
			boolean success = response.startsWith("ok#");
			int num = 0;
			if(success){
				num = Integer.parseInt(response.substring(3));
			}
			return num;
		} catch (Exception e) {
			logger.error("Baiwu query balance error", e);
		}
		return 0;
	}
	
	public int queryStatus(){
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("corp_id", userId);
			params.put("user_id", userId);
			params.put("corp_pwd",password );
			String response = HttpUtil.postByForm("http://cloud.baiwutong.com:8080/post_report.do", params, timeout, timeout, 1, ENCODING);
			if (logger.isInfoEnabled()) {
				logger.info("Baiwu SendSms returns: {}", response);
			}
			
			boolean success = response.startsWith("0");
			int num = -1;
			if(success){
				num = 0;
			}
			return num;
		} catch (Exception e) {
			logger.error("Baiwu query balance error", e);
		}
		return 0;
	}
	
	public static void main(String[] args) throws Exception{
		
		SmsBaiwuDao dao = new SmsBaiwuDao();
		String content = "亲，恭喜你在%s使用支付宝提现%s元成功，更多赚钱任务请继续关注我们，下载地址：http://t.cn/RUBoVdi 。" ;
		//System.out.println(dao.queryBalance());
		///System.out.println(dao.sendSms(new String[]{"18612693280"}, content, Constants.sms_record_type_down_other, Constants.sms_channel_common));
		
		int num = dao.queryStatus();
		while(num != 0){
			System.out.println(num);
			num = dao.queryStatus();
			Thread.sleep(1000);
		}
	}
}
