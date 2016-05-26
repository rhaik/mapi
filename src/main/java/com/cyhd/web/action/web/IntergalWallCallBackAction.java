package com.cyhd.web.action.web;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.MoneyUtils;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.UserThridPartIntegral;
import com.cyhd.service.impl.IdMakerService;
import com.cyhd.service.impl.UserThridPartIntegralService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.HMACSHA1;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.util.UserAgentUtil;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.util.YoumiSign;
import com.google.gson.JsonObject;

import net.sf.json.JSONObject;

@Controller
@RequestMapping("/web/callback")
public class IntergalWallCallBackAction extends BaseAction {
	
	private static Logger logger = LoggerFactory.getLogger("jifen");
	
	@Resource
	private UserThridPartIntegralService userThridPartIntegralService;
	
	@Resource
	private IdMakerService idMakerService;
	
	@RequestMapping(value="wanpu.3w",produces="text/json; charset=UTF-8")
	@ResponseBody
	public String recive(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		 String host = request.getHeader("Host");
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		String ip = RequestUtil.getIpAddr(request);
		logger.info("万普callback start，ip:{},host:{}",ip,host);
		String message="无效数据";
		boolean success = false;
		
		String adv_id = request.getParameter("adv_id");//被下载应用ID
		String app_id = request.getParameter("app_id");//万普平台注册应用id
		String key = request.getParameter("key");//调用积分墙时传入的key或者用户id
		String udid = request.getParameter("udid");//设备唯码
		String openudid = request.getParameter("open_udid");	//设备open_udid
		String bill = request.getParameter("bill");//价格
		if(bill == null){bill = "";}
		String points = request.getParameter("points");//积分
		if(points == null){points = "";}
		String ad_name = request.getParameter("ad_name");//下载的应用名称
		String statustr = request.getParameter("status");//状态
		int status = 0;
		if(statustr == null){statustr="";}
		String activate_time = request.getParameter("activate_time");//激活时间
		//========以下参数为旧版验证数据的参数，可以废弃此方法加密
		String order_id = request.getParameter("order_id");				//订单号
		String random_code = request.getParameter("random_code");//随机串
	//	String secret_key = request.getParameter("secret_key");//验证密钥
		//========新版验证
		String wapskey = request.getParameter("wapskey");//数据加密串
		String itunes_id = ServletRequestUtils.getStringParameter(request, "itunes_id","");
		String callBackKey = GlobalConfig.callBackKey;		//回调密钥
		//当满足价格、积分不为空且不为0和状态码为1时才为有效数据
		if(!bill.equals("0") && !points.equals("0") && statustr.equals("1")){
			status = Integer.parseInt(statustr);
			String activeTmp  = URLEncoder.encode(activate_time, "UTF-8");//激活时间在传输时会自动解码，加密是要用url编码过的，如果没有自动解码请忽略转码
			
			//加密并判断密钥
			String plaintext = adv_id+app_id+key+udid+bill+points+activeTmp+order_id+callBackKey;
			String keys = MD5Util.getMD5(plaintext);
			//判断和wapskey是否相同
			if(keys.equalsIgnoreCase(wapskey)){
				//成功接收数据
				message = "成功接收";
				success=true;

				List<Long> idsList = this.userThridPartIntegralService.repeatByWanPuIOS(key, adv_id, Constants.INTEGAL_SOURCE_WANPU);
				if(idsList != null && idsList.size() >= 2){
					logger.error("万普多次用户不给金币，query:{}",query);
				}else{
					
					
					UserThridPartIntegral integral = new UserThridPartIntegral( adv_id, app_id, key,udid,
							openudid, Float.parseFloat(bill), Integer.parseInt(points), ad_name, status,
							activate_time, order_id, random_code, ip, Constants.INTEGAL_SOURCE_WANPU,Constants.platform_ios);
					try {
						integral.setItunes_id(itunes_id);
						
						if(this.userThridPartIntegralService.insert(integral)){
							logger.info("万普入库成功"+query);
						}else{
							logger.error("万普入库失败"+query);
						}
					} catch (Exception e) {
						logger.error("万普通知入库异常:UserThridPartIntegral:" +query+ ", cause by:{}",e);
					}
				}
			}

		}
		
		JsonObject json = new JsonObject();
		json.addProperty("message", message);
		json.addProperty("success", success);
		return json.toString();
	}
	
	@RequestMapping(value="wanpu_android.3w",produces="text/json; charset=UTF-8")
	@ResponseBody
	public String reciveByAndroid(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		
		logger.info("万普android:callback start，");
		String message="无效数据";
		boolean success = false;
		
		String adv_id = request.getParameter("adv_id");//被下载应用ID
		String app_id = request.getParameter("app_id");//万普平台注册应用id
		String key = request.getParameter("key");//调用积分墙时传入的key或者用户id
		String udid = request.getParameter("udid");//设备唯码
		String bill = request.getParameter("bill");//价格
		if(bill == null){bill = "";}
		String points = request.getParameter("points");//积分
		if(points == null){points = "";}
		String ad_name = request.getParameter("ad_name");//下载的应用名称
		String statustr = request.getParameter("status");//状态
		String type = request.getParameter("type");//状态
		if(type == null){ type = "";}
		int status = 0;
		if(statustr == null){statustr="";}
		String activate_time = request.getParameter("activate_time");//激活时间
		//========以下参数为旧版验证数据的参数，可以废弃此方法加密
		String order_id = request.getParameter("order_id");				//订单号
		String random_code = request.getParameter("random_code");//随机串
		//String secret_key = request.getParameter("secret_key");//验证密钥
		//========新版验证
		String wapskey = request.getParameter("wapskey");//数据加密串
		String callBackKey = GlobalConfig.callBackKey;		//回调密钥
		//当满足价格、积分不为空且不为0和状态码为1时才为有效数据
		if(!bill.equals("0") && !points.equals("0") && statustr.equals("1")){
			status = Integer.parseInt(statustr);
			String activeTmp  = URLEncoder.encode(activate_time, "UTF-8");//激活时间在传输时会自动解码，加密是要用url编码过的，如果没有自动解码请忽略转码
			
			//加密并判断密钥
			String plaintext = adv_id+app_id+key+udid+bill+points+activeTmp+order_id+callBackKey;
			String keys = MD5Util.getMD5(plaintext);
			//判断和wapskey是否相同
			if(keys.equalsIgnoreCase(wapskey)){
				//成功接收数据
				message = "成功接收";
				success=true;

				String ip = RequestUtil.getIpAddr(request);
				//将分转化为元
				float f_bill= (float) MoneyUtils.fen2yuan((long)Float.parseFloat(bill));
				
				UserThridPartIntegral integral = new UserThridPartIntegral( adv_id, app_id, key,udid,
						type, f_bill, Integer.parseInt(points), ad_name, status,
						activate_time, order_id, random_code, ip, Constants.INTEGAL_SOURCE_WANPU,Constants.platform_android);
				try {
					if(this.userThridPartIntegralService.insert(integral)){
						logger.info("入库成功:"+query);
					}else{
						logger.error("入库失败:"+query);
					}
				} catch (Exception e) {
					response.setStatus(500);
					logger.error("万普通知入库异常:UserThridPartIntegral:" +query+ ", cause by:{}",e);
				}
			}

		}
		
		JsonObject json = new JsonObject();
		json.addProperty("message", message);
		json.addProperty("success", success);
		String ret =json.toString();
		return ret;
	} 
	
	@RequestMapping(value="youmi_cb_android.3w",produces="text/json; charset=UTF-8")
	@ResponseBody
	public String reciveByYoumiForAndroid(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		
		logger.info("有米安卓callBack:");
		String order = request.getParameter("order");
		String app = request.getParameter("app");
		String ad = URLDecoder.decode(request.getParameter("ad"), "utf-8");
		String user = URLDecoder.decode(request.getParameter("user"),"utf-8");
		int chn = ServletRequestUtils.getIntParameter(request, "chn",0);
		int  points = ServletRequestUtils.getIntParameter(request, "points",0);
		String sig = request.getParameter("sig");
		
		String adid = request.getParameter("adid");
		String pkg = request.getParameter("pkg");
		String device = URLDecoder.decode(request.getParameter("device"),"utf-8");
		String activate_time = request.getParameter("time");
		float price = ServletRequestUtils.getFloatParameter(request, "price", 0);
		int trade_type = ServletRequestUtils.getIntParameter(request, "trade_type", 0);
		//String _fb = request.getParameter("_fb");
		
		String ip =RequestUtil.getIpAddr(request);
		
		if(checkYoumiSignSrcret(order, app, user, chn, ad, points, sig)){
			float f_bill= price;
			int f_points = points;
			UserThridPartIntegral integral = new UserThridPartIntegral(adid, pkg, user,
					device, device, f_bill, f_points,
					ad, Constants.ESTATE_Y, activate_time,
					order, "", ip, 
					Constants.INTEGAL_SOURCE_YOUMI, Constants.platform_android);
			integral.setTrade_type(trade_type);
			try {
				if(this.userThridPartIntegralService.insert(integral)){
					logger.info("有米安卓 入库成功:"+query);
				}else{
					logger.error("有米安卓 入库失败:"+query);
				}
			} catch (Exception e) {
				response.setStatus(500);
				logger.error("有米安卓通知异常:UserThridPartIntegral:" +query+ ", cause by:{}",e);
			}
			return "ok";
			/**
			 * String adv_id, String app_id,
			String key, String udid, String open_udid, float bill, int points,
			String ad_name, int status, String activate_time, String order_id,
			String random_code, String ip, int source,int clientType
			 */
		}else{
			logger.error("有米校验密钥没有通过");
		}
		return "error";
	}
	/**
	 *
	 *order=YM150827QAvD8FvYed&app=1de342397eaf3072&ad=%E5%BD%93%E5%BD%93%E7%BD%91&pkg=com.dangdang.buy2&user=62249516&chn=0&points=215&price=0&time=1440668013&device=865290020459609&adid=7632&trade_type=1&sig=2f923b6a
 
	 * @param order
	 * @param app
	 * @param user
	 * @param chn
	 * @param ad
	 * @param points
	 * @param sig
	 * @return
	 */
	private boolean checkYoumiSignSrcret(String order , String  app , String  user , int  chn ,String  ad, int points,String sig){
		StringBuilder sb = new StringBuilder(100);
		sb.append(GlobalConfig.callBackey_youmi).append("||").append(order).append("||").append( app ).append( "||").append(user).append("||" ).append(chn).append("||").append(ad ).append("||" ).append(points);
		logger.info("MD5前:{}",sb.toString());
		return MD5Util.getMD5(sb.toString()).substring(12, 20).equals(sig);
	}
	
	@RequestMapping(value="dr_xid_android.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reciveByDianRu(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		
		String ip = RequestUtil.getIpAddr(request);
		logger.info("点入android回调:ip:{}",ip);
		JSONObject json = new JSONObject();
		//唯一标识 ID 
		String hashid = request.getParameter("hashid");
		//开发者应用 ID 
		String appid = request.getParameter("appid");
		//广告 ID 
		String adid = request.getParameter("adid");
		//广告名称（urlencode
		String adname = request.getParameter("adname");
		try {
			adname = URLDecoder.decode(adname, "utf-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		//开发者设置的用户 ID（SDK 中设置，可能会urlencode）
		int userid = ServletRequestUtils.getIntParameter(request, "userid",0);
		//mac
		String mac = request.getParameter("mac");
		//设备唯一标识（IMEI） 
		String deviceid = request.getParameter("deviceid");
		//渠道来源 
		String source = request.getParameter("source");
		//积分 
		int point  = ServletRequestUtils.getIntParameter(request, "point", 0);
		//时间戳 
		long time  = ServletRequestUtils.getLongParameter(request, "time", 0);
		//签名结果
		String checksum  = request.getParameter("checksum");
		
		if(StringUtils.isBlank(hashid)
				||StringUtils.isBlank(appid)
				||StringUtils.isBlank(adname)
				||userid <= 0
				||StringUtils.isBlank(deviceid)
				||"dianru".equalsIgnoreCase(source) == false
				||StringUtils.isBlank(checksum)
				||time <= 0){
			json.accumulate("message ", "参数不合法");
			json.accumulate("success", "false");
			logger.error("点入Android传入参数不合法,query:{}",query);
			return json.toString();
		}
		//不想去写一个方法 这样太乱啦 方法参数名太长
		StringBuilder sb = new StringBuilder(200);
		sb.append("?");
		sb.append("hashid=").append(hashid);
		sb.append("&appid=").append(appid);
		sb.append("&adid=").append(adid);
		sb.append("&adname=").append(adname);
		sb.append("&userid=").append(userid);
		sb.append("&mac=").append(mac);
		sb.append("&deviceid=").append(deviceid);
		sb.append("&source=").append(source);
		sb.append("&point=").append(point);
		sb.append("&time=").append(time);
		sb.append("&appsecret=").append(GlobalConfig.CALLBACK_KEY_ANDROID_DIANRU);
		
		String md5 = MD5Util.getMD5(sb.toString());
		if(checksum.equals(md5) == false){
			json.accumulate("message ", "加密参数不合法");
			json.accumulate("success", "false");
			logger.error("点入Android传入参数检查加密不合法,query:{}",query);
			return json.toString();
		}
		
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setSource(Constants.INTEGAL_SOURCE_DIANRU);
		integral.setClient_type(Constants.platform_android);
		integral.setKey(userid+"");
		integral.setPoints(point);
		integral.setAd_name(adname);
		integral.setAdv_id(adid);
		integral.setUdid(deviceid);
		integral.setIp(ip);
		integral.setOrder_id(hashid);
		//就不去新增字段啦 就把开发者应用ID存在这个字段上 就不存开发者ID啦 浪费数据
		integral.setOpen_udid(mac);
		integral.setActivate_time((time*1000)+"");
		integral.setStatus(Constants.ESTATE_Y);
		try {
			if(this.userThridPartIntegralService.insert(integral)){
				logger.info("点入android回调 插入数据库成功,query:{}",query);
			}else{
				logger.error("点入android回调 插入数据库失败，没有出现异常,query:{}",query);
			}
		} catch (Exception e) {
			logger.error("插入数据库失败，cause by:{},query:{}",e,query);
		}
		
		json.accumulate("message ", "OK");
		json.accumulate("success", "true");
		return json.toString();
	}
	//
	@RequestMapping(value="qm_ris_android.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByQuMi(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//这个是不需要的
		JSONObject json = new JSONObject();
		
		String ip = RequestUtil.getIpAddr(request);
		String query = request.getQueryString();
		
		StringBuilder sb = new StringBuilder(200);
		sb.append("趣米安卓,IP:").append(ip);
		try {//方便看
			sb.append(",query:").append(URLDecoder.decode(query, "utf-8"));
		} catch (Exception e) {
			sb.append(",query:").append(query);
		}
		
		//积分订单ID：该值是唯一的，如果开发接收到相同的订单号，那说明该订单已经存在。
		String order=request.getParameter("order");
		//趣米开发者应用ID
		int app=ServletRequestUtils.getIntParameter(request, "app",0);
		//广告名称，如果是应用类型的广告则是应用名，广告名称，如果是应用类型的广告则是应用名，
		String ad=ServletRequestUtils.getStringParameter(request, "ad","");
		//用户ID：开发者设置的积分用户的ID，例如开发者的应用是有登录功能的，则可以使用登录后的用户ID来替代趣米默认的用户标识。
		int user = ServletRequestUtils.getIntParameter(request, "user", 0);
		//设备ID：android是 imei，IOS 7.0以下是macid，IOS 7及以上是 Advertising Identifier (IDFA)。
		String device = request.getParameter("device");
		//此参数只适用于：ios为积分墙。表示微信里面“普通用户的标识”
		//String openid = request.getParameter("openid");
		//用户可以赚取的积分
		int points = ServletRequestUtils.getIntParameter(request, "points", 0);
		//产生效果的时间
		int time = ServletRequestUtils.getIntParameter(request, "time", 0);
		//参数签名
		String sig = request.getParameter("sig");
		//该签名在sig的基础上取消掉广告名称验证（原因：ad名是utf8编码，部分开发者测试教麻烦
		String sig2 = request.getParameter("sig2");
		
		try {
			ad = URLDecoder.decode(ad, "utf-8");
		} catch (Exception e) {
			logger.error("趣米还原ad,cause by:{},src_ad:{},{}",e,ad,sb.toString());
		}
		
		if(StringUtils.isBlank(order)
				||app <= 0
				||StringUtils.isBlank(ad)
				|| user <= 0
				|| StringUtils.isBlank(device)
				||points <= 0){
			logger.error(sb.toString());
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "关键参数为null");
			return json.toString();
		}
		
		StringBuilder sigSb = new StringBuilder(200);
		sigSb.append(GlobalConfig.CALLBACK_KEY_ANDROID_QUMI);
		sigSb.append("||").append(order);
		sigSb.append("||").append(app);
		sigSb.append("||").append(ad);
		sigSb.append("||").append(user);
		sigSb.append("||").append(device);
		sigSb.append("||").append(points);
		sigSb.append("||").append(time);
		
		String sign = MD5Util.getMD5(sigSb.toString()).substring(8, 24);
		if(sign.equals(sig) == false){
			logger.error("加解密参数不对 自签名1:{},{}",sign,sb.toString());
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "加解密参数不对");
			return json.toString();
		}
		
		sigSb.delete(0, sigSb.length());
		sigSb.append(GlobalConfig.CALLBACK_KEY_ANDROID_QUMI);
		sigSb.append("||").append(order);
		sigSb.append("||").append(app);
		sigSb.append("||").append(user);
		sigSb.append("||").append(device);
		sigSb.append("||").append(points);
		sigSb.append("||").append(time);
		
		sign = MD5Util.getMD5(sigSb.toString()).substring(8, 24);
		if(sign.equals(sig2) == false){
			logger.error("加解密参数不对 自签名2:{},{}",sign,sb.toString());
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "加解密参数不对");
			return json.toString();
		}
		logger.info("趣米通过检查，操作开始");
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(user+"");
		integral.setOrder_id(order);
		integral.setPoints(points);
		integral.setAd_name(ad);
		integral.setApp_id(app+"");
		integral.setClient_type(Constants.platform_android);
		integral.setSource(Constants.INTEGAL_SOURCE_QUMI);
		integral.setIp(ip);
		integral.setActivate_time(time+"");
		integral.setStatus(Constants.ESTATE_Y);
		integral.setUdid(device);
		
		try {
			if(this.userThridPartIntegralService.insert(integral)){
				logger.info(sb.toString()+",趣米入库成功");
			}else{
				logger.info(sb.toString()+",趣米入库失败");
			}
		} catch (Exception e) {
			logger.error("趣米入库失败:{},cause by:{}",query,e);
		}
		
		json.accumulate("code", 0);
		json.accumulate("message", "OK");
		return json.toString();
	}
	
	@RequestMapping(value="bd_idk_android.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByBeiDuo(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String ip = RequestUtil.getIpAddr(request);
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		String useridStr = request.getParameter("userid");
		String ad_name = request.getParameter("ad_name");
		int app_id = ServletRequestUtils.getIntParameter(request, "app_id", 0);
		float currency = ServletRequestUtils.getFloatParameter(request, "currency", 0);
		float ratio = ServletRequestUtils.getFloatParameter(request, "ratio", 0);
		long time_stamp = ServletRequestUtils.getLongParameter(request, "time_stamp", 0);
		String ad_packname = request.getParameter("ad_packname");
		String token = request.getParameter("token");
		int  trade_type = ServletRequestUtils.getIntParameter(request, "trade_type", 0);
		//要去重
		//如有需要请以{device_id, ad_packname, trade_type, time_stamp }来进行去重。
		int userId = 0;
		try {
			userId = Integer.parseInt(useridStr);
		} catch (Exception e) {
			
		}
		JSONObject json = new JSONObject();
		if(userId <= 0
				||app_id <= 0
				||currency <= 0
				||ratio <= 0
				||time_stamp <= 0
				||StringUtils.isBlank(ad_packname)
				||StringUtils.isBlank(token)
				||trade_type < 0
				||StringUtils.isBlank(ad_name)){
			logger.error("贝多,关键参数为null,ip:{},query:{}",ip,query);
			json.accumulate("code", -1);
			json.accumulate("message", "关键参数为null");
			response.setStatus(403);
			return json.toString();
		}
		
		String sign = MD5Util.getMD5(time_stamp+GlobalConfig.CALLBACK_KEY_ANDROID_BEIDUO);
		if(token.equals(sign) == false){
			logger.error("贝多,加密参数不合法,ip:{},query:{}",ip,query);
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "参数为不合法");
			return "403";
		}
		
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(useridStr);
		integral.setAd_name(ad_name);
		integral.setApp_id(app_id+"");
		integral.setClient_type(Constants.platform_android);
		integral.setSource(Constants.INTEGAL_SOURCE_BEIDUO);
		integral.setPoints((int)currency);
		integral.setActivate_time(time_stamp+"");		
		integral.setIp(ip);
		//没有订单id用现在的时间创建一个
		integral.setOrder_id(new Date().getTime()+useridStr);
		integral.setStatus(Constants.ESTATE_Y);		
		integral.setTrade_type(trade_type);
		//将倍率放在random_code
		integral.setRandom_code(String.valueOf(ratio));
		try {
			if(userThridPartIntegralService.insert(integral)){
				logger.info("贝多,保存成功,ip:{},query:{}",ip,query);
			}else{
				logger.error("贝多,保存失败,没有出现异常,ip:{},query:{}",ip,query);
			}
		} catch (Exception e) {
			logger.error("贝多,保存失败,出现异常,cause by:{},ip:{},query:{}",e,ip,query);
		}
		
		json.accumulate("code", 0);
		json.accumulate("message", "OK");
		return json.toString();
	}
	
	@RequestMapping(value="djoy_a_skaow.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByDianJoy(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			 query = request.getQueryString();
		}
		String ip = RequestUtil.getIpAddr(request);
		//60.28.204.228~60.28.204.254
		if(ip.startsWith("60.28.204") == false){
			logger.error("点乐通知：来源ip有误:IP:{},query:{}",ip,query);
			return "403";
		}
		int lastIpNum = Integer.parseInt(ip.substring(ip.lastIndexOf(".")+1));
		if(lastIpNum < 228 || lastIpNum > 254 ){//254 是不用判断的 最大就是255 还是组播地址
			logger.error("点乐通知：末尾ip号有误,IP:{},query:{}",ip,query);
			return "403";
		}
		
		String key = ServletRequestUtils.getStringParameter(request, "snuid", "");
		String device_id = ServletRequestUtils.getStringParameter(request, "device_id", "");
		String app_id = ServletRequestUtils.getStringParameter(request, "app_id", "");
		int currency = ServletRequestUtils.getIntParameter(request, "currency", 0);
		float app_ratio = ServletRequestUtils.getFloatParameter(request, "app_ratio", 0);
		long time_stamp = ServletRequestUtils.getLongParameter(request, "time_stamp", 0);
		String ad_name = ServletRequestUtils.getStringParameter(request, "ad_name","");
		String pack_name = ServletRequestUtils.getStringParameter(request, "pack_name","");
		String token = ServletRequestUtils.getStringParameter(request, "token","");
		String task_id = ServletRequestUtils.getStringParameter(request, "task_id","");
		int trade_type = ServletRequestUtils.getIntParameter(request, "trade_type", 0);
		
		if(StringUtils.isBlank(key)
			||StringUtils.isBlank(device_id)
			||StringUtils.isBlank(app_id)
			||StringUtils.isBlank(ad_name)
			||StringUtils.isBlank(pack_name)
			||StringUtils.isBlank(token)
			||trade_type <= 0){
			logger.error("点乐通知：关键参数为null,IP:{},query:{}",ip,query);
			return "403";
		}
		
		String secret = MD5Util.getMD5(time_stamp+GlobalConfig.CALLBACK_KEY_ANDROID_DIANJOY);
		if(secret.equals(token) == false){
			logger.error("点乐通知：加密校验不合法,IP:{},query:{}",ip,query);
			return "403";
		}
		//TODO 请以{device_id, pack_name, trade_type，task_id}来进行去重
		//数据库存储的字段是 udid,adv_id,trade_type,open_udid
		if(userThridPartIntegralService.repeatByDianJoy(device_id, pack_name, trade_type, task_id)){
			logger.error("点乐通知：已存在的数据:IP:{},query:{}",ip,query);
			//修改 重复通知算成功成功的响应 虽然没有重复的通知
			return "200";
		}
		
		Date createtime = new Date(time_stamp*1000);;
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(key);
		integral.setPoints(currency);
		integral.setAd_name(ad_name);
		integral.setClient_type(Constants.platform_android);
		integral.setSource(Constants.INTEGAL_SOURCE_DIANJOY);
		integral.setIp(ip);
		integral.setTrade_type(trade_type);
		integral.setCreatetime(createtime);
		integral.setStatus(Constants.ESTATE_Y);
		integral.setUdid(device_id);
		integral.setAdv_id(pack_name);
		//生成一个订单号 放在没什么用 就不生成啦
		//integral.setOrder_id(String.valueOf(idMakerService.getTimedId()));
		//把倍率放在Random_code
		integral.setRandom_code(String.valueOf(app_ratio));
		//就不去新增字段啦 
		integral.setOpen_udid(task_id);
		
		try {
			if(userThridPartIntegralService.insert(integral)){
				logger.info("点乐通知：入库成功:IP:{},query:{}",ip,query);
				return "200";
			}else{
				logger.error("点乐通知：入库失败:IP:{},query:{}",ip,query);
				return "403";
			}
		} catch (Exception e) {
			logger.error("点乐通知：入库异常:IP:{},query:{},cause by:{}",ip,query,e);
			return "200";
		}
	}
	
	@RequestMapping(value="midi_rta_mkdi.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByMiDi(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//响应值 是http的状态 
		String query = request.getQueryString();
		String ip = RequestUtil.getIpAddr(request);
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		
		String id = ServletRequestUtils.getStringParameter(request, "id","");
		String trand_no = ServletRequestUtils.getStringParameter(request, "trand_no","");
		int cash = ServletRequestUtils.getIntParameter(request, "cash",0);
		String imei = ServletRequestUtils.getStringParameter(request, "imei","");
		String buildleId = ServletRequestUtils.getStringParameter(request, "bundleId","");
		String param0 = ServletRequestUtils.getStringParameter(request, "param0","");
		String appName = ServletRequestUtils.getStringParameter(request, "appName","");
		int trade_type = ServletRequestUtils.getIntParameter(request, "scoreType", 0);
		String sign = ServletRequestUtils.getStringParameter(request, "sign","");
		
		if(StringUtils.isBlank(id)
				||StringUtils.isBlank(trand_no)
				||StringUtils.isBlank(buildleId)
				||StringUtils.isBlank(sign)
				||StringUtils.isBlank(appName)
				||StringUtils.isBlank(param0)){
			
			logger.warn("米迪回调参数不对,ip:{},query:{}",ip,query);
			return "403";
		}
		
		StringBuilder sb = new StringBuilder(200);
		sb.append(id).append(trand_no).append(cash).append(param0==null?"":param0).append(GlobalConfig.CALLBACK_KEY_ANDROID_MIDI);
		if(MD5Util.getMD5(sb.toString()).equals(sign) == false){
			logger.warn("米迪回调签名不对,ip:{},query:{}",ip,query);
			return "403";
		}
		
		//add 去重
		if(this.userThridPartIntegralService.repeatByOrderId(trand_no,Constants.INTEGAL_SOURCE_MIDI)){
			logger.warn("米迪回调,已存在的订单,ip:{},query:{}",ip,query);
			return "403";
		}
		
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(param0);
		integral.setIp(ip);
		integral.setSource(Constants.INTEGAL_SOURCE_MIDI);
		integral.setClient_type(Constants.platform_android);
		integral.setAd_name(appName);
		integral.setPoints(cash);
		integral.setOrder_id(trand_no);
		integral.setStatus(Constants.ESTATE_Y);
		integral.setTrade_type(trade_type);
		integral.setApp_id(buildleId);
		integral.setUdid(imei);
		integral.setOpen_udid(imei);
		integral.setAdv_id(id);
		try {
			if(this.userThridPartIntegralService.insert(integral)){
				logger.info("米迪回调,入库成功,ip:{},query:{}",ip,query);
				return "200";
			}else{
				logger.error("米迪回调,入库失败,没出现异常,ip:{},query:{}",ip,query);
				return "403";
			}
		} catch (Exception e) {
			logger.error("米迪回调,入库失败,出现异常,ip:{},query:{},cause by:{}",ip,query,e);
			//response.setStatus(403);
			return "200";
		}
	}
	
	@RequestMapping(value="duomeng_ejd_odjdf.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByDuoMeng(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String ip = RequestUtil.getIpAddr(request);
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		StringBuilder sb = new StringBuilder(500);
		sb.append("多盟回调,ip:").append(ip);
		sb.append(",query:").append(query);
		
		String orderid = request.getParameter("orderid");
		String pubid = request.getParameter("pubid");
		String ad =URLDecoder.decode(ServletRequestUtils.getStringParameter(request,"ad",""),"utf-8");
		int adid = ServletRequestUtils.getIntParameter(request, "adid", 0);
		String key = request.getParameter("user");
		String device = request.getParameter("device");
		int channel = ServletRequestUtils.getIntParameter(request, "channel", 0);
		float price = ServletRequestUtils.getFloatParameter(request, "price", 0);
		int point = ServletRequestUtils.getIntParameter(request, "point", 0);
		long ts = ServletRequestUtils.getLongParameter(request, "ts", 0);
		String sign = ServletRequestUtils.getStringParameter(request, "sign","");
		String pkg = URLDecoder.decode(ServletRequestUtils.getStringParameter(request, "pkg", ""),"utf-8");
		int	 action = ServletRequestUtils.getIntParameter(request, "action", 0);
		String action_name = URLDecoder.decode(ServletRequestUtils.getStringParameter(request,"action_name",""),"utf-8");
		JSONObject json = new JSONObject();
		if(StringUtils.isBlank(orderid)
				||StringUtils.isBlank(sign)
				||StringUtils.isBlank(ad)
				||StringUtils.isBlank(key)
				||"96ZJ0J3Qzfs33wTORH".equals(pubid) == false){
			sb.append("关键参数为空");
			logger.warn(sb.toString());
			json.accumulate("message", "关键参数为空");
			json.accumulate("code", -1);
			return json.toString();
		}
		
		Set<String> params= new TreeSet<String>();
		params.add("action="+ String.valueOf(action));
		params.add("action_name="+ action_name);
		params.add("ad="+ ad);
		params.add("adid="+ String.valueOf(adid));
		params.add("channel="+ String.valueOf(channel));
		params.add("device="+ device);
		params.add("orderid="+ orderid);
		params.add("pkg="+ pkg);
		params.add("point="+ String.valueOf(point));
		params.add("price="+ String.valueOf(price));
		params.add("pubid="+ pubid);
		params.add("ts="+ String.valueOf(ts));
		params.add("user="+ key);		
		
		StringBuilder sortSb = new StringBuilder(500);
		for(String tmp:params){
			sortSb.append(tmp);
		}
		
		String secret = sortSb.toString();
		
		if(sign.equals(MD5Util.getMD5(secret+GlobalConfig.CALLBACK_KEY_ANDROID_DUOMENG)) == false){
			sb.append(",加密参数不对");
			json.accumulate("message", "加密参数不对");
			json.accumulate("code", -1);
			logger.warn(sb.toString());
			return json.toString();
		}
		
		//检查是不是重复
		if(this.userThridPartIntegralService.repeatByOrderId(orderid,Constants.INTEGAL_SOURCE_DUOMENG)){
			sb.append("已存在的订单");
			json.accumulate("message", "OK");
			json.accumulate("code", 0);
			logger.warn(sb.toString());
			return json.toString();
		}
		
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(key);
		integral.setPoints(point);
		integral.setAd_name(ad);
		integral.setBill(price);
		integral.setOrder_id(orderid);
		integral.setIp(ip);
		integral.setClient_type(Constants.platform_android);
		integral.setSource(Constants.INTEGAL_SOURCE_DUOMENG);
		integral.setApp_id(pkg);
		integral.setTrade_type(action);
		integral.setAdv_id(pubid);
		
		try{
			Date activate_time = new Date(ts*1000);
			integral.setActivate_time(DateUtil.format(activate_time, "yyyy-MM-dd HH:mm:ss"));
		}catch(Exception e){}
		
		try {
			if(userThridPartIntegralService.insert(integral)){
				sb.append(",入库成功");
				logger.warn(sb.toString());
			}else{
				sb.append(",入库失败");
				logger.warn(sb.toString());
			}
		} catch (Exception e) {
			sb.append(",入库失败,cause by:").append(e.getMessage());
			logger.error(sb.toString());
		}
		
		json.accumulate("message", "OK");
		json.accumulate("code", 0);
		return json.toString();
	}

	@RequestMapping(value="/wechat_zhuanfa_jifen.3w",produces="text/json; charset=UTF-8")
	@ResponseBody
	public String reviceByWeChatZhuanFa(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String mid = request.getParameter("mid");
		String key = request.getParameter("account");
		String tid = request.getParameter("tid");
		String tname = ServletRequestUtils.getStringParameter(request, "tname","");
		int points = ServletRequestUtils.getIntParameter(request, "integral", 0);
		String ua = request.getParameter("ua");
		String ifa = request.getParameter("ifa");
		String ifv = request.getParameter("ifv");
		String mac = ServletRequestUtils.getStringParameter(request, "mac","");
		String udid = request.getParameter("udid");
		String sign = request.getParameter("sign");
		String order = request.getParameter("order");
		
		String ip = RequestUtil.getIpAddr(request);
		String  queryParameter = RequestUtil.getQueryString(request);
		try {
			queryParameter = URLDecoder.decode(queryParameter, "utf-8");
		} catch (Exception e) {
			 queryParameter = RequestUtil.getQueryString(request);
		}
		String responseData = "{\"res\":%d,\"msg\":\"%s\"}";
		StringBuilder sb = new StringBuilder(640);
		sb.append("微信转发任务:ip=").append(ip);
		sb.append(",query=").append(queryParameter);
		
		if(GlobalConfig.WECHAT_MID.equals(mid) == false
				||StringUtil.isBlank(key)
				||StringUtil.isBlank(tid)
				||points <= 0
				||StringUtil.isBlank(ua)
				||StringUtil.isBlank(sign)
				||StringUtil.isBlank(order)){
			sb.append("微信转发任务参数错误");
			logger.error(sb.toString());
			return String.format(responseData,0, "process error");
		}
//		有的编码是urlencode
		Map<String, String> query = new HashMap<String, String>();
		query.put("mid", mid);
		query.put("account", key);
		query.put("tid", tid);
		query.put("tname", tname);
		query.put("integral", points+"");
		query.put("ua", ua);
		query.put("ifa", ifa);
		query.put("ifv", ifv);
		query.put("mac", mac);
		query.put("udid",udid);
		query.put("order", order);
		//需要加上盐 
		String signKey =GlobalConfig.CALLBACK_KEY_WECHAT_ZHUANFA;
		String  encryptText = RequestSignUtil.getSortedRequestString(query, "", "")+signKey;
		String encryptData = HMACSHA1.HmacSHA1Encrypt(encryptText,signKey);
		
		//检查签名
		if(sign.equals(encryptData) == false){
			sb.append(",签名错误");
			logger.error(sb.toString());
			return String.format(responseData,1, "ok");
		}
		//
		if(userThridPartIntegralService.repeatByOrderId(order, Constants.INTEGAL_SOURCE_WECHAT)){
			sb.append("乙存在的订单");
			logger.error(sb.toString());
			return String.format(responseData,1, "ok");
		}
		
		UserAgentUtil.UserAgent userAgent = new UserAgentUtil.UserAgent(ua);
		int client_type = userAgent.isIPhone()?Constants.platform_ios:Constants.platform_android;
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(key);
		integral.setPoints(points);
		integral.setAd_name(tname);
		integral.setKey(key);
		integral.setAdv_id(tid);
		integral.setClient_type(client_type);
		integral.setUdid(ifa);
		//将udid存放在open_udid 将idfa存放在udid
		integral.setOpen_udid(udid);
		//将ifv 存放在random_code
		integral.setRandom_code(ifv);
		integral.setSource(Constants.INTEGAL_SOURCE_WECHAT);
		integral.setActivate_time(DateUtil.format(GenerateDateUtil.getCurrentDate()));
		integral.setStatus(Constants.ESTATE_Y);
		integral.setOrder_id(order);
		try {
			if(userThridPartIntegralService.insert(integral)){
				sb.append(",入库成功");
				logger.info(sb.toString());
				return String.format(responseData,1, "ok");
			}else{
				sb.append(",入库失败");
				logger.warn(sb.toString());
				return String.format(responseData,0, "fail");
			}
		} catch (Exception e) {
			sb.append(",入库失败,cause by:").append(e.getMessage());
			logger.error(sb.toString());
			return String.format(responseData,0, "error");
		}
	}
	
	@RequestMapping(value="/youmi_ios_callback.3w",produces="text/json; charset=UTF-8")
	@ResponseBody
	public String reviceByYouMiIOS(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//String _fb = request.getParameter("_fb");
		String order = request.getParameter("order");
		String app = request.getParameter("app");
		String ad = request.getParameter("ad");
		int adid = ServletRequestUtils.getIntParameter(request, "adid", 0);
		String key = request.getParameter("user");
		String divice = request.getParameter("device");
		int chn = ServletRequestUtils.getIntParameter(request, "chn", 0);
		float price = ServletRequestUtils.getFloatParameter(request, "price", 0);
		int points = ServletRequestUtils.getIntParameter(request, "points", 0);
		String time = request.getParameter("time");
		String storeid = request.getParameter("storeid");
//		String sig = request.getParameter("sig");
//		String sign = request.getParameter("sign");
		
		String query = RequestUtil.getQueryString(request);
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = RequestUtil.getQueryString(request);
		}
		String ip = RequestUtil.getIpAddr(request);
		StringBuilder sb = new StringBuilder(640);
		sb.append("有米IOS,ip:").append(ip);
		sb.append(",query:").append(query);

		try {
			boolean vildeStatus = true;
			if(adid <= 0
					||points <= 0
					||NumberUtils.isNumber(key) == false
					||StringUtil.isBlank(order)
					||chn != 0
					||GlobalConfig.YM_IOS_APP_ID.equals(app) == false){
					sb.append("相关参数不合法");
					vildeStatus = false;
			}
			
			if(vildeStatus && YoumiSign.checkUrlSignature(query, GlobalConfig.YM_IOS_DEV_SERVER_SECRET) == false){
				sb.append("签名错误");
				vildeStatus = false;
			}
			//检查是不是同一个订单
			if(vildeStatus && this.userThridPartIntegralService.repeatByOrderId(order, Constants.INTEGAL_SOURCE_YOUMI_IOS)){
				sb.append(",重复的订单推送");
				vildeStatus = false;
			}
			
			//TODO 加上每个人只有两次机会
			
			if(vildeStatus == false){
				logger.error(sb.toString());
				response.setStatus(403);
				return "";
			}
			
			Date acDate = new Date(Long.parseLong(time)*1000);
			UserThridPartIntegral integral = new UserThridPartIntegral();
			integral.setActivate_time(time);
			integral.setAd_name(ad);
			integral.setBill(price);
			integral.setPoints(points);
			integral.setAdv_id(adid+"");
			integral.setKey(key);
			integral.setOrder_id(order);
			integral.setIp(ip);
			integral.setClient_type(Constants.platform_ios);
			integral.setSource(Constants.INTEGAL_SOURCE_YOUMI_IOS);
			integral.setItunes_id(storeid);
			integral.setUdid(divice);
			integral.setStatus(Constants.ESTATE_Y);
			integral.setCreatetime(acDate);
			
			if(this.userThridPartIntegralService.insert(integral)){
				sb.append("，入库成功");
				logger.info(sb.toString());
			}else{
				sb.append(",入库失败,没有出现异常");
				logger.warn(sb.toString());
			}
		} catch (Exception e) {
			sb.append(",出现异常:cause by:").append(e);
			logger.error(sb.toString());
		}
		response.setStatus(200);
		return null;
	}
	
	@RequestMapping(value="djoy_lxm_jskux.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByDianJoyIOS(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			 query = request.getQueryString();
		}
		String ip = RequestUtil.getIpAddr(request);
		//60.28.204.228~60.28.204.254
		if(ip.startsWith("60.28.204") == false){
			logger.error("点乐IOS通知：来源ip有误:IP:{},query:{}",ip,query);
			return "403";
		}
		int lastIpNum = Integer.parseInt(ip.substring(ip.lastIndexOf(".")+1));
		if(lastIpNum < 228 || lastIpNum > 254 ){//254 是不用判断的 最大就是255 还是组播地址
			logger.error("点乐IOS通知：末尾ip号有误,IP:{},query:{}",ip,query);
			return "403";
		}
		
		String key = ServletRequestUtils.getStringParameter(request, "snuid", "");
		String device_id = ServletRequestUtils.getStringParameter(request, "device_id", "");
		String app_id = ServletRequestUtils.getStringParameter(request, "app_id", "");
		int currency = ServletRequestUtils.getIntParameter(request, "currency", 0);
		float app_ratio = ServletRequestUtils.getFloatParameter(request, "app_ratio", 0);
		long time_stamp = ServletRequestUtils.getLongParameter(request, "time_stamp", 0);
		String ad_name = ServletRequestUtils.getStringParameter(request, "ad_name","");
		String pack_name = ServletRequestUtils.getStringParameter(request, "pack_name","");
		String token = ServletRequestUtils.getStringParameter(request, "token","");
		String task_id = ServletRequestUtils.getStringParameter(request, "task_id","");
		int trade_type = ServletRequestUtils.getIntParameter(request, "trade_type", 0);
		String ad_id = request.getParameter("ad_id");
		String itunes_id = request.getParameter("itunes_id");
		//device_id=4CC55EFF-8FD3-4B25-A8EF-EEB247DF927B&snuid=15499673&app_id=6ae41269d291f5970ace81f61d883524&currency=800&app_ratio=5&trade_type=1&time_stamp=1455867846&token=d1a581905530b185593e0f634a75ca0b&return_type=simple&ad_name=你我贷&pack_name=com.appstore.nwd&order_id=8607e60c262ed5e4ccc66a216fd26046&ad_id=26271ac66319388c9b892ac47340118a&itunes_id=875761690
		if(StringUtils.isBlank(key)
			||StringUtils.isBlank(device_id)
			||StringUtils.isBlank(app_id)
			||StringUtils.isBlank(ad_name)
			||StringUtils.isBlank(pack_name)
			||StringUtils.isBlank(token)
			||trade_type <= 0
			||GlobalConfig.DiANJOY_APP_ID_IOS.equals(app_id) == false){
			logger.error("点乐IOS通知：关键参数为null,IP:{},query:{}",ip,query);
			return "403";
		}
		
		String secret = MD5Util.getMD5(time_stamp+GlobalConfig.CALLBACK_KEY_IOS_DIANJOY);
		if(secret.equals(token) == false){
			logger.error("点乐IOS通知：加密校验不合法,IP:{},query:{}",ip,query);
			return "403";
		}
		//请以{device_id, pack_name, trade_type，task_id}来进行去重
		//数据库存储的字段是 udid,adv_id,trade_type,open_udid
		if(userThridPartIntegralService.repeatByDianJoy(device_id, pack_name, trade_type, task_id)){
			logger.error("点乐IOS通知：已存在的数据:IP:{},query:{}",ip,query);
			//重复的也算成功的响应
			return "200";
		}
		
		//
		
		Date createtime = new Date(time_stamp*1000);
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(key);
		integral.setPoints(currency);
		integral.setAd_name(ad_name);
		integral.setClient_type(Constants.platform_ios);
		integral.setSource(Constants.INTEGAL_SOURCE_DIANJOY_IOS);
		integral.setIp(ip);
		integral.setTrade_type(trade_type);
		integral.setCreatetime(createtime);
		integral.setStatus(Constants.ESTATE_Y);
		integral.setUdid(device_id);
		integral.setAdv_id(pack_name);
		integral.setOrder_id(ad_id);
		//生成一个订单号 放在没什么用 就不生成啦
		//integral.setOrder_id(String.valueOf(idMakerService.getTimedId()));
		//把倍率放在Random_code
		integral.setRandom_code(String.valueOf(app_ratio));
		//就不去新增字段啦 
		integral.setOpen_udid(task_id);
		integral.setItunes_id(itunes_id);
		
		try {
			if(userThridPartIntegralService.insert(integral)){
				logger.info("点乐IOS通知：入库成功:IP:{},query:{}",ip,query);
				return "200";
			}else{
				logger.error("点乐IOS通知：入库失败:IP:{},query:{}",ip,query);
				return "403";
			}
		} catch (Exception e) {
			logger.error("点乐IOS通知：入库异常:IP:{},query:{},cause by:{}",ip,query,e);
			return "200";
		}
	}
	
	@RequestMapping(value="qm_lwl_ios.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reviceByQuMiIOS(HttpServletRequest request, HttpServletResponse response) throws Exception{
		//这个是不需要的
		JSONObject json = new JSONObject();
		
		String ip = RequestUtil.getIpAddr(request);
		String query = request.getQueryString();
		
		StringBuilder sb = new StringBuilder(200);
		sb.append("趣米IOS,IP:").append(ip);
		try {//方便看
			sb.append(",query:").append(URLDecoder.decode(query, "utf-8"));
		} catch (Exception e) {
			sb.append(",query:").append(query);
		}
		
		//积分订单ID：该值是唯一的，如果开发接收到相同的订单号，那说明该订单已经存在。
		String order=request.getParameter("order");
		//趣米开发者应用ID
		int app=ServletRequestUtils.getIntParameter(request, "app",0);
		//广告名称，如果是应用类型的广告则是应用名，广告名称，如果是应用类型的广告则是应用名，
		String ad=ServletRequestUtils.getStringParameter(request, "ad","");
		//用户ID：开发者设置的积分用户的ID，例如开发者的应用是有登录功能的，则可以使用登录后的用户ID来替代趣米默认的用户标识。
		int user = ServletRequestUtils.getIntParameter(request, "user", 1);
		//设备ID：android是 imei，IOS 7.0以下是macid，IOS 7及以上是 Advertising Identifier (IDFA)。
		String device = request.getParameter("device");
		//此参数只适用于：ios为积分墙。表示微信里面“普通用户的标识”
		//String openid = request.getParameter("openid");
		//用户可以赚取的积分
		int points = ServletRequestUtils.getIntParameter(request, "points", 0);
		//产生效果的时间
		int time = ServletRequestUtils.getIntParameter(request, "time", 0);
		//参数签名
		String sig = request.getParameter("sig");
		//该签名在sig的基础上取消掉广告名称验证（原因：ad名是utf8编码，部分开发者测试教麻烦
		String sig2 = request.getParameter("sig2");
		
		try {
			ad = URLDecoder.decode(ad, "utf-8");
		} catch (Exception e) {
			logger.error("趣米还原ad,cause by:{},src_ad:{},{}",e,ad,sb.toString());
		}
		
		if(StringUtils.isBlank(order)
				||app <= 0
				||StringUtils.isBlank(ad)
				|| user <= 0
				|| StringUtils.isBlank(device)
				||points <= 0){
			logger.error(sb.toString());
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "关键参数为null");
			return json.toString();
		}
		
		StringBuilder sigSb = new StringBuilder(200);
		sigSb.append(GlobalConfig.CALLBACK_KEY_IOS_QUMI);
		sigSb.append("||").append(order);
		sigSb.append("||").append(app);
		sigSb.append("||").append(ad);
		sigSb.append("||").append(user);
		sigSb.append("||").append(device);
		sigSb.append("||").append(points);
		sigSb.append("||").append(time);
		
		String sign = MD5Util.getMD5(sigSb.toString()).substring(8, 24);
		if(sign.equals(sig) == false){
			logger.error("加解密参数不对 自签名1:{},{}",sign,sb.toString());
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "加解密参数不对");
			return json.toString();
		}
		
		sigSb.delete(0, sigSb.length());
		sigSb.append(GlobalConfig.CALLBACK_KEY_IOS_QUMI);
		sigSb.append("||").append(order);
		sigSb.append("||").append(app);
		sigSb.append("||").append(user);
		sigSb.append("||").append(device);
		sigSb.append("||").append(points);
		sigSb.append("||").append(time);
		
		sign = MD5Util.getMD5(sigSb.toString()).substring(8, 24);
		if(sign.equals(sig2) == false){
			logger.error("加解密参数不对 自签名2:{},{}",sign,sb.toString());
			response.setStatus(403);
			json.accumulate("code", -1);
			json.accumulate("message", "加解密参数不对");
			return json.toString();
		}
		logger.info("趣米通过检查，操作开始");
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setKey(user+"");
		integral.setOrder_id(order);
		integral.setPoints(points);
		integral.setAd_name(ad);
		integral.setApp_id(app+"");
		integral.setClient_type(Constants.platform_ios);
		integral.setSource(Constants.INTEGAL_SOURCE_QUMI_IOS);
		integral.setIp(ip);
		integral.setActivate_time(time+"");
		integral.setStatus(Constants.ESTATE_Y);
		integral.setUdid(device);
		
		try {
			if(this.userThridPartIntegralService.insert(integral)){
				logger.info(sb.toString()+",趣米入库成功");
			}else{
				logger.info(sb.toString()+",趣米入库失败");
			}
			
		} catch (Exception e) {
			logger.error("趣米入库失败:{},cause by:{}",query,e);
		}
		
		json.accumulate("code", 0);
		json.accumulate("message", "OK");
		return json.toString();
	}
	
	@RequestMapping(value="dr_jsd_ios.3w",produces="text/json; charset=UTF-8",method=RequestMethod.GET)
	@ResponseBody
	public String reciveByDianRuIOS(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String query = request.getQueryString();
		try {
			query = URLDecoder.decode(query, "utf-8");
		} catch (Exception e) {
			query = request.getQueryString();
		}
		
		String ip = RequestUtil.getIpAddr(request);
		logger.info("点入ios回调:ip:{}",ip);
		
		JSONObject json = new JSONObject();
		//唯一标识 ID 
		String hashid = request.getParameter("hashid");
		//开发者应用 ID 
		String appid = request.getParameter("appid");
		//广告 ID 
		String adid = request.getParameter("adid");
		//广告名称（urlencode
		String adname = request.getParameter("adname");
		try {
			adname = URLDecoder.decode(adname, "utf-8");
		} catch (Exception e) {
			// TODO: handle exception
		}
		//开发者设置的用户 ID（SDK 中设置，可能会urlencode）
		int userid = ServletRequestUtils.getIntParameter(request, "userid",0);
		//mac
		String mac = request.getParameter("mac");
		//设备唯一标识（IMEI） 
		String deviceid = request.getParameter("deviceid");
		//渠道来源 
		String source = request.getParameter("source");
		//积分 
		int point  = ServletRequestUtils.getIntParameter(request, "point", 0);
		//时间戳 
		long time  = ServletRequestUtils.getLongParameter(request, "time", 0);
		//签名结果
		String checksum  = request.getParameter("checksum");
		
		if(StringUtils.isBlank(hashid)
				||StringUtils.isBlank(appid)
				||StringUtils.isBlank(adname)
				||userid <= 0
				||StringUtils.isBlank(deviceid)
				||"dianru".equalsIgnoreCase(source) == false
				||StringUtils.isBlank(checksum)
				||time <= 0){
			json.accumulate("message ", "参数不合法");
			json.accumulate("success", "false");
			logger.error("点入ios传入参数不合法,query:{}",query);
			return json.toString();
		}
		//不想去写一个方法 这样太乱啦 方法参数名太长
		StringBuilder sb = new StringBuilder(200);
		sb.append("?");
		sb.append("hashid=").append(hashid);
		sb.append("&appid=").append(appid);
		sb.append("&adid=").append(adid);
		sb.append("&adname=").append(adname);
		sb.append("&userid=").append(userid);
		sb.append("&deviceid=").append(deviceid);
		sb.append("&source=").append(source);
		sb.append("&point=").append(point);
		sb.append("&time=").append(time);
		sb.append("&appsecret=").append(GlobalConfig.CALLBACK_KEY_IOS_DIANRU);
		
		String md5 = MD5Util.getMD5(sb.toString());
		if(checksum.equals(md5) == false){
			json.accumulate("message ", "加密参数不合法");
			json.accumulate("success", "false");
			logger.error("点入ios传入参数检查加密不合法,query:{}",query);
			return json.toString();
		}
		
		UserThridPartIntegral integral = new UserThridPartIntegral();
		integral.setSource(Constants.INTEGAL_SOURCE_DIANRU_IOS);
		integral.setClient_type(Constants.platform_ios);
		integral.setKey(userid+"");
		integral.setPoints(point);
		integral.setAd_name(adname);
		integral.setAdv_id(adid);
		integral.setUdid(deviceid);
		integral.setIp(ip);
		integral.setOrder_id(hashid);
		//就不去新增字段啦 就把开发者应用ID存在这个字段上 就不存开发者ID啦 浪费数据
		integral.setOpen_udid(mac);
		integral.setActivate_time((time*1000)+"");
		integral.setStatus(Constants.ESTATE_Y);
		try {
			if(this.userThridPartIntegralService.insert(integral)){
				logger.info("点入ios回调 插入数据库成功,query:{}",query);
			}else{
				logger.error("点入ios回调 插入数据库失败，没有出现异常,query:{}",query);
			}
		} catch (Exception e) {
			logger.error("插入数据库失败，cause by:{},query:{}",e,query);
		}
		
		json.accumulate("message ", "OK");
		json.accumulate("success", "true");
		return json.toString();
	}
}
