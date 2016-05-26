package com.cyhd.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.cyhd.service.dao.po.*;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.job.AsyncJob;
import com.cyhd.common.util.job.JobHandler;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.AppChannelMapper;
import com.cyhd.service.dao.db.mapper.AppTaskChannelMapper;
import com.cyhd.service.dao.db.mapper.UserTaskChannelMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.util.DianruInterfaceUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.MD5;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.common.util.AESCoder;


@Service
public class ChannelService extends BaseService {
	private static Logger servicelog = LoggerFactory.getLogger("third");
	//缓存应用渠道信息
	private CacheLRULiveAccessDaoImpl<AppChannel> cacheAppChannel = new CacheLRULiveAccessDaoImpl<AppChannel>(Constants.minutes_millis * 5, 100);
	//缓存第三方渠道任务信息
	private CacheLRULiveAccessDaoImpl<AppTaskChannelVo> cacheAppTaskChannel = new CacheLRULiveAccessDaoImpl<AppTaskChannelVo>(Constants.hour_millis * 1, 100);
	
	@Resource
	private AppChannelMapper appChannelMapper;
	@Resource
	private AppTaskChannelMapper appTaskChannelMapper;
	@Resource
	private UserTaskChannelMapper userTaskChannelMapper;
	@Resource
	private AppTaskService appTaskService;
	@Resource
	private MeituTaskService meituTaskService;
//	@Resource
//	private UserInstalledAppService userInstalledAppService;
	
	@Resource
	private UserTaskService userTaskService;
	
	private final String CALLBACL_AES_KEY = "Wi7hb892KhbFTXh6";
	
	public AppChannel getAppChannel(int id) {
		String cacheKey = String.valueOf(id);
		AppChannel channel = cacheAppChannel.get(cacheKey);
		if(channel == null ) {
//			List<AppChannel> appChannel = appChannelMapper.getAppChannel();
//			if (appChannel != null && appChannel.size() > 0) {
//				appChannel.stream().forEach(ch -> cacheAppChannel.set(String.valueOf(ch.getId()), ch) );
//				channel = cacheAppChannel.get(cacheKey);
//			}
			//修改为要谁就去load谁
			channel = appChannelMapper.getAppChannelById(id);
			cacheAppChannel.set(String.valueOf(cacheKey), channel);
		}
		return channel;
	}
	/**
	 * 获取渠道任务信息
	 * @param taskId
	 * @return
	 */
	public AppTaskChannelVo getAppTaskChannel(int taskId) {
		String cacheKey = String.valueOf(taskId);
		AppTaskChannelVo rs = cacheAppTaskChannel.get(cacheKey);
		if(rs == null) {
			AppTaskChannel appTaskChannel = appTaskChannelMapper.getAppTaskChannelByTaskId(taskId);
			AppTaskChannelVo vo = new AppTaskChannelVo();
			if(appTaskChannel != null) {
				AppChannel appChannel = getAppChannel(appTaskChannel.getApp_channel_id());
				vo.setAppChannel(appChannel);
				vo.setAppTaskChannel(appTaskChannel); 
			}  
			cacheAppTaskChannel.set(cacheKey, vo);
			return vo;
		}
		return rs;
	}
	
	public void addAppTaskChannel(AppTaskChannel channel){
		appTaskChannelMapper.addAppTaskChannel(channel);
	}
	
	/**
	 * 根据开发者应⽤用ID和广告ID获取任务
	 * 
	 * @param adid
	 * @return
	 */
	public AppTaskChannel getAppTaskChannelByAdid(int adid) {
		return appTaskChannelMapper.getAppTaskChannelByAdid(adid);
	}


	public boolean isAllowReceiveTask(User user, AppTask appTask, ClientInfo clientInfo){
		String appStoreId = "";
		AppTaskChannelVo channelVo = getAppTaskChannel(appTask.getId());
		App app = appTaskService.getApp(appTask.getApp_id());
		boolean canRevice = false;
		switch(channelVo.getAppChannel().getId()) {
			case AppChannel.CHANNEL_JUYOUQIAN :	//聚有钱
				//canRevice = true;
				appStoreId = app.getAppstore_id();
				canRevice = isAllowReviceJYQTask(appTask.getId(), clientInfo.getIdfa(), appStoreId, channelVo);
				break;
			case AppChannel.CHANNEL_QUMI : //趣米
				canRevice = isAllowReviceQMTask(appTask.getId(), clientInfo.getIdfa(), appTask.getApp_id(), channelVo, clientInfo.getIpAddress());
				break;
			case AppChannel.CHANNEL_DIANRU : //点入
				DianruInterfaceUtil util = new DianruInterfaceUtil(channelVo.getAppChannel().getClick_url(), channelVo.getAppChannel().getChannel_id());
				canRevice = util.onClick(channelVo.getAppTaskChannel().getThird_id(), channelVo.getAppTaskChannel().getAdid(), user.getUser_identity(), clientInfo);
				break;
			case AppChannel.CHANNEL_MEITU: //美图
				canRevice = meituTaskService.isAllowReviceTask(user, appTask.getId(), appTask.getApp_id(), channelVo, clientInfo);
				break;
			case AppChannel.CHANNEL_ZHANGSHANG : //掌上
				canRevice = isAllowReviceZSTask(appTask, clientInfo, channelVo,user.getId(),appTaskService.getApp(appTask.getApp_id()));
				break;
			case AppChannel.CHANNEL_YOUMI : //有米
				canRevice = isAllowYoumiReviceTask(appTask, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_YOUMI_DISTINCT : //有米(排重)
				appStoreId = appTaskService.getApp(appTask.getApp_id()).getAppstore_id();
				canRevice = isAllowYoumiDistinctReviceTask(appStoreId, appTask, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_ADSAGE :	//艾德思奇
				canRevice = isAllowAdSageReviceTask(appTask, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_ZHIMENG :	//指盟
				canRevice = isAllowMobsmarReviceTask(appTask, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_DIANRU_DISTINCT: //点入排重
				canRevice = isAllowDianruReviceTask(appTask, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_51HB: //51红包
				canRevice = isAllow51HBReviceTask(appTask, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_QUMI_DISTINCT: //趣米带排重接口
				canRevice = isAllowReceiveQMDistinctTask(appTask, clientInfo, channelVo);
			    break;
			case AppChannel.CHANNEL_MOPAN:
				canRevice = isAllowMoPanReviceTaskNotCallBack(appTask, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_MOPAN_CALLBACK:
				canRevice = isAllowMoPanReviceTaskByCallBack(appTask, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_DIAOQIANYANER_NOT_CALLBACK:
				canRevice = isAllowDiaoqianyanReviceTaskByNotCallBack(appTask, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_XINZHETIANXIA_CALLBACK:
				canRevice = isAllowXingzheReviceTaskByCallBack(appTask, user, clientInfo, channelVo,true);
				break;
			case AppChannel.CHANNEL_YOUQIAN_CALLBACK:
				canRevice = isAllowYouQianReviceTaskByCallBack(appTask, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_51HONGBAO_CALLBACK:
				canRevice = isAllow51HBReviceTaskByCallBack(appTask, clientInfo, channelVo, user);
				break;
			case AppChannel.CHANNEL_DUOWAN_CALLBACK:
				canRevice = isAllowDuowanReviceTaskByCallBack(appTask, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_AIPUYOUBANG_CALLBACK:
				canRevice = isAllowAiPuYouBangReviceTaskByCallBack(appTask, user, clientInfo, channelVo,true);
				break;
			case AppChannel.CHANNEL_MIDI_CALLBACK:
				canRevice = isAllowMIDEReviceTaskCallbask(appTask, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_TONGBANQIANG_NOT_DISTINCT:
				canRevice = isAllowTongBanQiangReviceTaskCallbask(appTask,app, user, clientInfo, channelVo);
				break;
			case AppChannel.CHANNEL_JUYOUQIAN_CALLBACK:
				canRevice = isAllowJuYouQianReviceTask(appTask, clientInfo, channelVo,user,app);
				break;
			case AppChannel.CHANNEL_AIPUYOUBANG_NEW_NOT_CALLBACK:
				canRevice = isAllowAiPuYouBnagNewReviceTaskByDistinct(clientInfo, channelVo, app)
						&& isAllowAiPuYouBangNewReviceTask(appTask, clientInfo, channelVo,user,app,false);
				break;
			case AppChannel.CHANNEL_LANMAO_DISTINCT:
				canRevice = isAllowLanMaoReviceTaskByDistinct(appTask, clientInfo, channelVo, user);
				break;
			case AppChannel.CHANNEL_AIPUYOUBANG_NEW_CALLBACK:
				//先走爱普友邦的排重接口 
				canRevice = isAllowAiPuYouBnagNewReviceTaskByDistinct(clientInfo, channelVo, app);
				if(canRevice){
					canRevice = isAllowAiPuYouBangNewReviceTask(appTask, clientInfo, channelVo,user,app,true);
				}
				break;
			case AppChannel.CHANNEL_YOUQIAN_QZPP_NOT_CALLBACK:
				canRevice = isAllowLanYouQinaQZPPByDistinct(appTask, app, clientInfo, channelVo, user);
				break;
			case AppChannel.CHANNEL_MIAOLE_DISTINCT:
				canRevice = isAllowMiaoLeByDistinct(appTask, app, clientInfo, channelVo, user);
				break;
			case AppChannel.CHANNEL_WUXIFEIMENG_NOTCALLBACK:
				canRevice = isAllowWuXiFeiMengByDistinct(appTask, app, clientInfo, channelVo, user);
				break;
			case AppChannel.CHANNEL_JUYOUQIAN_REAL_TIME_DISTINCT:
				canRevice = isAllowJuYouQianRealTimeDistinctReviceTask(appTask, clientInfo, channelVo,user,app);
				break;
			case AppChannel.CHANNEL_ANN9_CALLBACK:
				canRevice = isAllowAnn9ReviceTask(appTask, clientInfo, channelVo,user,app,true);
				break;
			case AppChannel.CHANNEL_ANN9_NOT_CALLBACK:
				canRevice = isAllowAnn9ReviceTask(appTask, clientInfo, channelVo,user,app,false);
				break;
			case AppChannel.CHANNEL_REFANQIE_NOT_CALLBACK:
				canRevice = isAllowReFanQieReviceTask(appTask, clientInfo, channelVo,user,app,false);
				break;
			case AppChannel.CHANNEL_KEJIN_NOT_CALLBACK:
				canRevice = isAllowKeJinReviceTask(appTask, clientInfo, channelVo,user,app,false);
				break;
			case AppChannel.CHANNEL_KEJIN_CALLBACK:
				canRevice = isAllowKeJinReviceTask(appTask, clientInfo, channelVo,user,app,true);
				break;
			case AppChannel.CHANNEL_SHIKE_DISCINCT:
				canRevice = isAllowShiKeReviceTaskByDiscinct(appTask,clientInfo,channelVo,app);
				break;
			case AppChannel.CHANNEL_SHIKE_CALLBACK:
				canRevice = isAllowShiKeReviceTaskByCallBack(appTask,clientInfo,channelVo,app,user);
				break;
		}
		return canRevice;
	}
	
	
	private boolean isAllowShiKeReviceTaskByCallBack(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo channelVo,
			App app,User user) {
		if(!isAllowShiKeReviceTaskByDiscinct(appTask, clientInfo, channelVo, app)){
			return false;
		}
		
		try {
		
			StringBuilder parameters = new StringBuilder(320);
			parameters.append(channelVo.getAppChannel().getClick_url());
			parameters.append("?channel=").append(channelVo.getAppTaskChannel().getThird_id());
			parameters.append("&idfa=").append(clientInfo.getIdfa());
			parameters.append("&ip=").append(clientInfo.getIpAddress());
			parameters.append("&callback=").append(genCallbackUrlData(channelVo, user, appTask, clientInfo.getIdfa()));
			
			String url = parameters.toString();
			servicelog.info("请求试客点击开始,channel:{},idfa:{}",channelVo.getAppTaskChannel().getThird_id(),clientInfo.getIdfa());
			String response = HttpUtil.get(url, null,6000);
			servicelog.info("请求试客点击结束,channel:{},idfa:{},response:{}",channelVo.getAppTaskChannel().getThird_id(),clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			return json != null && json.optInt("status",2) == 1;
		} catch (Exception e) {
			servicelog.info("请求试客点击异常,channel:{},idfa:{},cause:",channelVo.getAppTaskChannel().getThird_id(),clientInfo.getIdfa(),e);
		}
		return false;
	}
	private boolean isAllowShiKeReviceTaskByDiscinct(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo channelVo,
			App app) {
		StringBuilder parameters = new StringBuilder(320);
		parameters.append(channelVo.getAppChannel().getQuery_url());
		parameters.append("?channel=").append(channelVo.getAppTaskChannel().getThird_id());
		parameters.append("&idfa=").append(clientInfo.getIdfa());
		parameters.append("&ip=").append(clientInfo.getIpAddress());
		
		String url = parameters.toString();
		int success = 2;
		try {
			servicelog.info("请求试客排重开始,channel:{},idfa:{}",channelVo.getAppTaskChannel().getThird_id(),clientInfo.getIdfa());
			String response = HttpUtil.get(url, null,6000);
			servicelog.info("请求试客排重结束,channel:{},idfa:{},response:{}",channelVo.getAppTaskChannel().getThird_id(),clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				success = json.optInt(clientInfo.getIdfa(), 2);
				if(success == 1){
					try {
						//userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
					} catch (Exception e) {}
				}
			}
		} catch (Exception e) {
			servicelog.info("请求试客排重异常,channel:{},idfa:{},cause:",channelVo.getAppTaskChannel().getThird_id(),clientInfo.getIdfa(),e);
		}
		return success == 0;
	}
	private boolean isAllowKeJinReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo channelVo,
			User user, App app, boolean callback) {
		
		try{
			String code = null;
			String pcid = null;

			//一个参数的时候为pcid，如果要排重，需要填成 code&pcid 的形式
			String[] third_id = channelVo.getAppTaskChannel().getThird_id().split("&");
			if (third_id.length == 2){
				code = third_id[0];
				pcid = third_id[1];
			}else {
				pcid = third_id[0];
			}

			//code不为空的时候才走排重接口
			if (StringUtil.isNotBlank(code)) {
				StringBuilder distinctParameters = new StringBuilder(128);
				distinctParameters.append(channelVo.getAppChannel().getQuery_url());
				distinctParameters.append("?code=").append(code);
				distinctParameters.append("&did=").append(clientInfo.getIdfa());

				String requestURl = distinctParameters.toString();
				servicelog.info("请求应用Codrim排重开始:request:{}", requestURl);
				String response = HttpUtil.get(requestURl, null, 10000);
				servicelog.info("请求应用Codrim排重结束:request:{},response:{}", requestURl, response);
				JSONObject json = new JSONObject(response);
				int exist = json.optInt(clientInfo.getIdfa(), 2);
				if (exist == 1) {
					try {
						//userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
					} catch (Exception e) {
					}
				}

				if (exist != 0) {
					return false;
				}
			}else {
				logger.warn("isAllowKeJinReviceTask, 未走排重，user:{}, idfa={}", user.getId(), clientInfo.getIdfa());
			}
			
			StringBuilder parames = new StringBuilder(640);
			parames.append(channelVo.getAppChannel().getClick_url());
			parames.append("?did=").append(clientInfo.getIdfa());
			if(callback){
				parames.append("&callback=").append(genCallbackUrlData(channelVo, user, appTask, clientInfo.getIdfa()));
			}
			parames.append("&pcid=").append(pcid);
			parames.append("&ip=").append(clientInfo.getIpAddress());
			
			String requestURl = parames.toString();
			servicelog.info("请求应用Codrim点击开始:request:{}", requestURl);
			String response = HttpUtil.get(requestURl, null,10000);
			servicelog.info("请求Codrim点击结束:idfa:{},appid:{},response:{}",clientInfo.getIdfa(),app.getAppstore_id(),response);
			JSONObject json = new JSONObject(response);
			return json != null && "1000".equals(json.optString("code", ""));
		}catch(Exception e){
			servicelog.info("请求Codrim点击异常:idfa:{},appid:{},cause:",clientInfo.getIdfa(),app.getAppstore_id(),e);
		}
		return false;
	}
	private boolean isAllowReFanQieReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo channelVo,
			User user, App app, boolean b) {
		StringBuilder sb = new StringBuilder();
		sb.append(channelVo.getAppChannel().getQuery_url());
		sb.append("?appid=").append(app.getAppstore_id());
		sb.append("&idfa=").append(clientInfo.getIdfa());
		sb.append("&channel=").append(channelVo.getAppChannel().getChannel_id());
		
		String queryURL = sb.toString();
		try {
			servicelog.info("热番茄排重开始,request:{}",queryURL);
			String response = HttpUtil.get(queryURL, null,10000);
			servicelog.info("热番茄排重结束,idfa:{},app:{},response:{}",clientInfo.getIdfa(),app.getName(),response);
			JSONObject json = new JSONObject(response);
			int exist = 2;
			if(json != null){
				exist = json.optInt(clientInfo.getIdfa(), 2);
				if(exist == 1){
					try {
						//userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
					} catch (Exception e) {}
				}
			}
			if(exist == 0){
				return clickTaskByReFanQie(channelVo, app, appTask, clientInfo);
			}
		} catch (Exception e) {
			servicelog.info("热番茄排重异常,idfa:{},app:{},cause ",clientInfo.getIdfa(),app.getName(),e);
		}
		return false;
	}
	private boolean isAllowAnn9ReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo channelVo, User user,
			App app,boolean callback) {
		if(!ann9Distinct(clientInfo, app, channelVo)){
			return false;
		}
		try{
			StringBuilder parames = new StringBuilder(640);
			parames.append(channelVo.getAppChannel().getClick_url());
			parames.append("?udid=").append(clientInfo.getIdfa());
			if(callback){
				parames.append("&multipleurl=").append(genCallbackUrlData(channelVo, user, appTask, clientInfo.getIdfa()));
			}
			parames.append("&appid=").append(app.getAppstore_id());
			parames.append("&source=").append(channelVo.getAppChannel().getChannel_id());
			parames.append("&returnFormat=1");
			
			String requestURl = parames.toString();
			servicelog.info("请求应用雷达点击开始:request:{}",requestURl);
			String response = HttpUtil.get(requestURl, null,1000);
			servicelog.info("请求应用雷达点击结束:idfa:{},appid:{}",clientInfo.getIdfa(),app.getAppstore_id());
			JSONObject json = new JSONObject(response);
			return json != null && json.optBoolean("success", false);
		}catch(Exception e){
			servicelog.info("请求应用雷达点击结束:idfa:{},appid:{},cause:",clientInfo.getIdfa(),app.getAppstore_id(),e);
		}
		return false;
	}
	
	
	private boolean ann9Distinct(ClientInfo clientInfo,App app,AppTaskChannelVo vo){
		try{
			Map<String, String> params = new HashMap<>(2);
			//加密参数
			params.put("appid",app.getAppstore_id());
			params.put("idfa",clientInfo.getIdfa());
			String url = vo.getAppChannel().getQuery_url();
			servicelog.info("请求应用雷达排重接口开始:idfa:{},appId:{}",clientInfo.getIdfa(),app.getAppstore_id());
			String response = HttpUtil.postByForm(url, params);
			servicelog.info("请求应用雷达排重接口结束:idfa:{},appId:{},response:{}",clientInfo.getIdfa(),app.getAppstore_id(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				int value = json.optInt(clientInfo.getIdfa(), 2);
				if(value == 1){
					try {
						//userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
					} catch (Exception e) {}
				}
				return value == 0;
			}
		}catch(Exception e){
			servicelog.info("请求应用雷达排重接口结束:idfa:{},appId:{},cause:",clientInfo.getIdfa(),app.getAppstore_id(),e);
		}
		return false;
	}
	private boolean isAllowTongBanQiangReviceTaskCallbask(AppTask appTask,App app, User user, ClientInfo clientInfo,
			AppTaskChannelVo vo) {
		String url = null;
		StringBuilder sb = new StringBuilder();
		String appid = vo.getAppTaskChannel().getThird_id();
		if(StringUtil.isBlank(appid)){
			appid = app.getAppstore_id();
		}
		//排重开始
		//http://api.miidi.net/cas/exist.bin?source=miaozhuan&appid=413993350CP&idfa=
		sb.append(vo.getAppChannel().getQuery_url());
		sb.append("?channelid=").append(vo.getAppChannel().getChannel_id());
		sb.append("&appid=").append(appid);
		sb.append("&idfa=").append(clientInfo.getIdfa());
		sb.append("&ip=").append(clientInfo.getIpAddress());
		//排重结束
		String response = null;
		try {
			url = sb.toString();
			servicelog.info("调用铜板墙排重开始,user:{},request:{}",user,url);
			response = HttpUtil.get(url, null);
			servicelog.info("调用铜板墙排重结束,user:{},response:{}",user,response);
			JSONObject json = new JSONObject(response);
			return json != null && json.optInt("code",1) == 0;
		}catch(Exception e){
			servicelog.info("调用铜板墙排重异常,user:{},url:{},cause by{}",user,url,e);
		}
		return false;
	}
	/**
	 * 调用趣米的排重接口，点击之前先排重
	 * @param appTask
	 * @param clientInfo
	 * @param channelVo
	 * @return
	 */
	private boolean isAllowReceiveQMDistinctTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo channelVo) {
		Map<String, String> params = new HashMap<>();
		params.put("idfa", clientInfo.getIdfa());
		params.put("type", "query");
		params.put("ad_id", channelVo.getAppTaskChannel().getThird_id());

		try {
			String result = HttpUtil.get("http://new.wall.qumi.com/Api/Opendata/mqlc", params);
			servicelog.info("qumi distinct, appTask:{}, idfa:{}, result:{}", appTask, clientInfo.getIdfa(), result);

			JSONObject json = new JSONObject(result);
			if (json != null && json.optInt("status") == 0){

				//再次调用趣米的点击接口
				return isAllowReviceQMTask(appTask.getId(), clientInfo.getIdfa(), appTask.getApp_id(), channelVo, clientInfo.getIpAddress());
			}
		}catch (Exception exp){
			servicelog.error("qumi distinct, appTask:{}, idfa:{}, error:{}", appTask, clientInfo.getIdfa(), exp);
		}
		return false;
	}

	/**
	 * 聚有钱
	 * 
	 * @param taskId  
	 * @return
	 * @throws Exception 
	 */
	protected boolean isAllowReviceJYQTask(int taskId, String idfa, String appId, AppTaskChannelVo vo) {
		//idfa = "EBE31E56-F7FB-4181-8281-F92B46E69D83";
		String url = vo.getAppChannel().getQuery_url();
		HashMap<String, String> params = new HashMap<String, String>(); 
		params.put("idfa", idfa);
		params.put("appId", appId);
		params.put("channel", vo.getAppChannel().getChannel_id()+""); 
	
		String rs ="";
		try{
			servicelog.info("appid={},check {} AllowReviceTask {} request url:{},params:{}",appId, idfa, taskId, url, params.toString());
			rs = HttpUtil.postByForm(url, params);
			servicelog.info("appid={},check {} AllowReviceTask {} retrun: {}",appId, idfa, taskId, rs);
			JSONObject json = new JSONObject(rs);  
			return json.get(idfa).equals("0");
		} catch (Exception e) {
			servicelog.error("appid="+appId+",check "+idfa+" AllowReviceTask is error:" + e.getMessage());
			return false;
		}
	}
	/**
	 * 趣米验证
	 * 
	 * @param taskId
	 * @return
	 * @throws UnsupportedEncodingException 
	 * @throws Exception 
	 */
	protected boolean isAllowReviceQMTask(int taskId, String idfa, int appId, AppTaskChannelVo vo, String ip) {
		String url = vo.getAppChannel().getClick_url();
		HashMap<String, String> map = new HashMap<String, String>();
		String app_secret = "debf1f3a6f96ef212bf4d5fcb162ffd0";
		map.put("appkey", "1836054339dfe5d3de672e56fdd1c60a");
		map.put("adid", appId * 11 + 997 + "");
		map.put("action", 	"activate");
		map.put("device_id", idfa);
		long currentTime = new Date().getTime() / 1000;
		map.put("timestamp", currentTime+"");
		
		
		String callbackParams = RequestSignUtil.getSortedRequestString(map);
		map.put("sign", MD5.getMD5((callbackParams + app_secret).getBytes()));
		map.put("expire", "false");
		String callback = vo.getAppChannel().getCallback_url() + "?" + RequestSignUtil.getSortedRequestString(map);
		
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("idfa", idfa);
		params.put("ad", vo.getAppTaskChannel().getThird_id() + "");
		params.put("app", vo.getAppChannel().getChannel_id()+""); 
		params.put("clientip", ip); 
		params.put("callback", callback);
		String rs ="";
		try{
			servicelog.info("appid={},check {} AllowReviceTask {} request url:{},params:{}",appId, idfa, taskId, url, params.toString());
			rs = HttpUtil.postByForm(url, params);
			servicelog.info("appid={},check {} AllowReviceTask {} retrun: {}",appId, idfa, taskId, rs);
////			if(!rs.isEmpty()) {
////				JSONObject json = new JSONObject(rs);
////				return json.get("state").equals("error") ?  false : true;
////			}
//			return true;
//			//趣米的点击接口 返回格式是{"21EA85E6-9AB2-41E7-B9E3-46F75A0C03EE":1}
			JSONObject json = new JSONObject(rs);
			return json.optInt(idfa, 1) == 0;
		} catch (Exception e) {
			servicelog.error("appid="+appId+",check "+idfa+" AllowReviceTask is error:" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * 掌上互动
	 * 
	 * @return
	 * @throws Exception 
	 */
	protected boolean isAllowReviceZSTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo,int userId,App app) {
		
		//排重
		//http://api.v3.9080app.com/RemoveEcho.ashx?adid=2616&idfa=23567&btype=1
		
		StringBuilder sb = new StringBuilder(320);
		sb.append("http://api.v3.9080app.com/RemoveEcho.ashx");
		sb.append("?adid=").append(vo.getAppTaskChannel().getAdid());
		sb.append("&idfa=").append(clientInfo.getIdfa());
		sb.append("&btype=").append("1");
		
		String repeatQuery = sb.toString();
		try {
			logger.info("请求掌上互动排重接口,query:{}",repeatQuery);
			String rev = HttpUtil.get(repeatQuery, null);
			logger.info("请求掌上互动排重接口,返回,query:{},response:{}",repeatQuery,rev);
			JSONObject json = new JSONObject(rev);
			if(json.has(clientInfo.getIdfa()) == false || json.getInt(clientInfo.getIdfa())==1){
				try {
					//去掉已安装入库 这样用户换设备还可以下载
					//userInstalledAppService.insert(userId, app.getId(), clientInfo.getDid(), app.getAgreement());
				} catch (Exception e) {}
				return false;
			}
			
		} catch (Exception e) {
			logger.info("请求掌上互动排重接口,异常,query:{},cause by:{}",repeatQuery,e.getMessage());
			return false;
		}
		
		String url = vo.getAppChannel().getClick_url();
		HashMap<String, String> map = new HashMap<String, String>();
		String app_secret = "a7a3b69cddbeb9e6a9ef42c843a15b6a";
		map.put("appkey", "20d7373975f3a67263ac97ca993ed5bc");
		map.put("adid", appTask.getApp_id() * 11 + 997 + "");
		map.put("action", 	"activate");
		map.put("device_id", clientInfo.getIdfa());
		long currentTime = new Date().getTime() / 1000;
		map.put("timestamp", currentTime+"");
		
		String callbackParams = RequestSignUtil.getSortedRequestString(map);
		map.put("sign", MD5.getMD5((callbackParams + app_secret).getBytes()));
		map.put("expire", "false");
		String callback = vo.getAppChannel().getCallback_url() + "?" + RequestSignUtil.getSortedRequestString(map);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("idfa", clientInfo.getIdfa());
		params.put("appid", vo.getAppTaskChannel().getThird_id() + "");
		params.put("adid", vo.getAppTaskChannel().getAdid()+""); 
		params.put("ip", clientInfo.getIpAddress()); 
		params.put("mac", "02:00:00:00:00:00");
	
		String rs ="";
		try{
			params.put("callback", URLEncoder.encode(callback, "utf-8"));
			servicelog.info("appid={},check {} AllowReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), url, params.toString());
			rs = HttpUtil.get(url, params);
			servicelog.info("appid={},check {} AllowReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);  
				return json.has("success") ? json.getBoolean("success") : false;
			}
			return false;
		} catch(Exception e) {
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" AllowReviceTask is error:" + e.getMessage());
			return false;
		}
	} 
	
	/**
	 * 有米
	 *  
	 * @return
	 * @throws Exception 
	 */
	protected boolean isAllowYoumiReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo) {
		//首先排重
		String distinctUrl = "http://cp.api.youmi.net/midiapi/querya/";
		String appStoreId = appTaskService.getApp(appTask.getApp_id()).getAppstore_id();
		if (!isDistinctSuccessByYoumi(distinctUrl, appStoreId, appTask, clientInfo)){
			return false;
		}

		String url = vo.getAppChannel().getClick_url();
	 
		HashMap<String, String> map = new HashMap<String, String>();
		String app_secret = "fb778759380e2501e84a6d2da3bf44d7";
		map.put("appkey", "1308acefb6535b53f645e724d7d9a003");
		map.put("adid", appTask.getApp_id() * 11 + 997 + "");
		map.put("action", 	"activate");
		map.put("device_id", clientInfo.getIdfa());
		long currentTime = new Date().getTime() / 1000;
		map.put("timestamp", currentTime+"");
		
		String callbackParams = RequestSignUtil.getSortedRequestString(map);
		map.put("sign", MD5.getMD5((callbackParams + app_secret).getBytes()));
		map.put("expire", "false");
		String callback = RequestSignUtil.getSortedRequestString(map);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("s", vo.getAppTaskChannel().getThird_id()); 
		params.put("ifa", clientInfo.getIdfa());
		params.put("ip", clientInfo.getIpAddress()); 
		params.put("mac", "");
	
		String rs = "";
		try{ 
			params.put("clickid", URLEncoder.encode(callback, "utf-8"));
			servicelog.info("appid={},check {} isAllowYoumiReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), url, params.toString());
			rs = HttpUtil.get(url, params);
			servicelog.info("appid={},check {} isAllowYoumiReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);  
				if(json.optInt("c") != 0) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
//			if(e.getClass().getName().equals("org.json.JSONException")) {
//				return true;
//			}
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" AllowReviceTask is error:" + e.getMessage());
			return false;
		}
	} 
	
	/**
	 * 有米
	 *  
	 * @return
	 * @throws Exception 
	 */
	protected boolean isAllowYoumiDistinctReviceTask(String appStoreId, AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo) {
		String url = vo.getAppChannel().getClick_url();
		return isDistinctSuccessByYoumi(url, appStoreId, appTask, clientInfo);
	}


	/**
	 * 有米排重
	 * @param distinctUrl
	 * @param appStoreId
	 * @param appTask
	 * @param clientInfo
	 * @return
	 */
	protected boolean isDistinctSuccessByYoumi(String distinctUrl, String appStoreId, AppTask appTask, ClientInfo clientInfo) {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("idfa", clientInfo.getIdfa());
		params.put("appid", appStoreId);

		String rs = "";
		try{
			servicelog.info("appid={},check {} isAllowYoumiDistinctReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), distinctUrl, params.toString());
			rs = HttpUtil.postByForm(distinctUrl, params);
			servicelog.info("appid={},check {} isAllowYoumiDistinctReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);
				if(json!= null && json.has(clientInfo.getIdfa())) {
					return "1".equals(json.get(clientInfo.getIdfa()).toString()) ? false : true;
				}
				return false;
			} else {
				return false;
			}
		} catch(Exception e) {
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" isAllowYoumiDistinctReviceTask is error:" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * adSage
	 *  
	 * @return
	 * @throws Exception 
	 */
	protected boolean isAllowAdSageReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo) {
		String url = vo.getAppChannel().getClick_url();
		 
		HashMap<String, String> map = new HashMap<String, String>();
		String app_secret = "36059498c465c9d743736ecc3ac3020d";
		map.put("appkey", "52bac2cb344a7c69849dc47f8c975239");
		map.put("adid", appTask.getApp_id() * 11 + 997 + "");
		map.put("action", 	"activate");
		map.put("device_id", clientInfo.getIdfa());
		long currentTime = new Date().getTime() / 1000;
		map.put("timestamp", currentTime+"");
		
		String callbackParams = RequestSignUtil.getSortedRequestString(map);
		map.put("sign", MD5.getMD5((callbackParams + app_secret).getBytes()));
		map.put("expire", "false");
		String callback = RequestSignUtil.getSortedRequestString(map);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("ver", "T01"); 
		params.put("slpg", "11"); 
		params.put("trctype", "1"); 
		params.put("orderid", "378"); 
		params.put("ordertype", "outside"); 
		params.put("channel", "378"); 
		params.put("apptype", "1"); 
		
		params.put("idfa", clientInfo.getIdfa());
		params.put("ip", clientInfo.getIpAddress()); 
		params.put("sysversion", clientInfo.getAppVer());
		params.put("From", "client");
		params.put("mac", ""); 
	
		String rs = "";
		try{ 
			params.put("callback", URLEncoder.encode(callback, "utf-8"));
			servicelog.info("appid={},check {} isAllowAdSageReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), url, params.toString());
			rs = HttpUtil.get(url, params);
			servicelog.info("appid={},check {} isAllowAdSageReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);  
				if(json.has("c")) {
					return false;
				}
				return true;
			} else {
				return false;
			}
		} catch(Exception e) {
			if(e.getClass().getName().equals("org.json.JSONException")) {
				return true;
			}
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" isAllowAdSageReviceTask is error:" + e.getMessage());
			return false;
		}
	}
	
	/**
	 * 指盟
	 *  
	 * @return
	 * @throws Exception 
	 */
	protected boolean isAllowMobsmarReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo) {
		String url = vo.getAppChannel().getClick_url();
	 
		HashMap<String, String> map = new HashMap<String, String>();
		String app_secret = "abd65e1543c4b2a61da6d3bbdd1bd27c";
		map.put("appkey", "cbe2b0eeac958e4392e4b8d18aa19514");
		map.put("adid", appTask.getApp_id() * 11 + 997 + "");
		map.put("action", 	"activate");
		map.put("device_id", clientInfo.getIdfa());
		long currentTime = new Date().getTime() / 1000;
		map.put("timestamp", currentTime+"");
		
		String callbackParams = RequestSignUtil.getSortedRequestString(map);
		map.put("sign", MD5.getMD5((callbackParams + app_secret).getBytes()));
		map.put("expire", "false");
		String callback = vo.getAppChannel().getCallback_url() + "?" + RequestSignUtil.getSortedRequestString(map);
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("adid", vo.getAppTaskChannel().getThird_id()); 
		params.put("cid", vo.getAppChannel().getChannel_id());
		params.put("idfa", clientInfo.getIdfa());
		params.put("ip", clientInfo.getIpAddress()); 
		params.put("mac", "");
		params.put("jump", "0");
		
		String rs = "";
		try{ 
			params.put("callbackurl", URLEncoder.encode(callback, "utf-8"));
			servicelog.info("appid={},check {} isAllowMobsmarReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), url, params.toString());
			rs = HttpUtil.get(url, params);
			servicelog.info("appid={},check {} isAllowMobsmarReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				int index = rs.indexOf("\"success\":\"true\"");
				if(index>=0) {
					return true;
				}
			}
		} catch(Exception e) {
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" isAllowMobsmarReviceTask is error:" + e.getMessage());
		}
		return false;
	} 
	/**
	 * 点入排重
	 * 
	 * @param appTask
	 * @param clientInfo
	 * @param vo
	 * @return
	 */
	protected boolean isAllowDianruReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo) {
		String url = vo.getAppChannel().getClick_url();
		HashMap<String, String> params = new HashMap<String, String>(); 
		params.put("idfa_list", clientInfo.getIdfa());
		params.put("dr_adid", vo.getAppTaskChannel().getThird_id()); 

		if (url.indexOf('?') > 0){
			url = url + "&dr_adid=" + vo.getAppTaskChannel().getThird_id();
		}else {
			url = url + "?dr_adid=" + vo.getAppTaskChannel().getThird_id();
		}

		String rs = "";
		try{ 
			servicelog.info("appid={},check {} isAllowDianruReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), url, params.toString());
			rs = HttpUtil.postByForm(url, params);
			servicelog.info("appid={},check {} isAllowDianruReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);  
				if(json!= null && json.has(clientInfo.getIdfa())) {
					return "0".equals(json.optString(clientInfo.getIdfa()));
				}
			}  
		} catch(Exception e) {
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" isAllowDianruReviceTask is error:" + e.getMessage());
		}
		return false;
	}
	
	/**
	 * 51红包 排重
	 * 
	 * @param appTask
	 * @param clientInfo
	 * @param vo
	 * @return
	 */
	protected boolean isAllow51HBReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo) {
		String url = vo.getAppChannel().getClick_url();
		HashMap<String, String> params = new HashMap<String, String>(); 
		params.put("idfa", clientInfo.getIdfa());
		params.put("bid", vo.getAppTaskChannel().getThird_id()); 
		params.put("source", vo.getAppChannel().getChannel_id()); 
		params.put("ip", clientInfo.getIpAddress());
		String rs = "";
		try{ 
			servicelog.info("appid={},check {} isAllow51HBReviceTask {} request url:{},params:{}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), url, params.toString());
			rs = HttpUtil.get(url, params);
			servicelog.info("appid={},check {} isAllow51HBReviceTask {} retrun: {}",appTask.getApp_id(), clientInfo.getIdfa(), appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);  
				if(json!= null && json.has(clientInfo.getIdfa()) && "0".equals(json.optString(clientInfo.getIdfa()))) {
					return true;
				}
			}  
		} catch(Exception e) {
			servicelog.info("appid="+appTask.getApp_id()+",check "+clientInfo.getIdfa()+" isAllow51HBReviceTask is error:" + e.getMessage());
		}
		return false;
	}
	
	/***
	 * 51红包回调任务
	 * @param appTask
	 * @param clientInfo
	 * @param vo
	 * @param user
	 * @return
	 */
	protected boolean isAllow51HBReviceTaskByCallBack(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo,User user) {
		logger.info("51红包回调任务");
		if(isAllow51HBReviceTask(appTask, clientInfo, vo) ){
			return executeBy51HBCallBack(vo, appTask, clientInfo, user,"http://www.51hb.me/Hongbao/Report", true);
		}
		return false;
	}
	/**
	 * 磨盘不回调
	 * @param appTask
	 * @param clientInfo
	 * @param vo
	 * @return
	 */
	protected boolean isAllowMoPanReviceTaskNotCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo) {
		return isAllowMoPanReviceTask(appTask, user, clientInfo, vo, false);
	}
	/***
	 * 磨盘任务重回调的任务
	 * @param appTask
	 * @param user
	 * @param clientInfo
	 * @param vo
	 * @return
	 */
	protected boolean isAllowMoPanReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo) {
		return isAllowMoPanReviceTask(appTask, user, clientInfo, vo, true);
	}
	private boolean isAllowMoPanReviceTask(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isCallback) {
		int userid = user.getId();
		App app = appTaskService.getApp(appTask.getApp_id());
		//排重
		AppChannel channel = vo.getAppChannel();
		StringBuilder sb = new StringBuilder(320);
		sb.append(channel.getQuery_url());
		sb.append("?appid=").append(vo.getAppTaskChannel().getThird_id());
		sb.append("&idfa=").append(clientInfo.getIdfa());
		sb.append("&source=").append(vo.getAppChannel().getChannel_id());
		String url = sb.toString();
		String response = null;
		try{
			servicelog.info("磨盘排重开始：idfa:{},user:{},appName:{},appid:{},request:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),url);
			response = HttpUtil.get(url, null);
			servicelog.info("磨盘排重结束：idfa:{},user:{},appName:{},appid:{},response:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),response);
			JSONObject  json = new JSONObject(response);
			if(json != null){
				if (json.has(clientInfo.getIdfa()) == false) {
					return false;
				}
				if(json.getInt(clientInfo.getIdfa()) == 1){
					try {
						//userInstalledAppService.insert(userid, app.getId(), clientInfo.getDid(), app.getAgreement());
					} catch (Exception e) {}
					return false;
				}
			}
		}catch(Exception e){
			servicelog.info("磨盘排重异常：idfa:{},user:{},appName:{},appid:{},cause by:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),e.getMessage());
			return false;
		}
		//排重结束
		sb.delete(0, sb.length());
		try {
			sb.append(channel.getClick_url());
			sb.append("?source=").append(vo.getAppChannel().getChannel_id());
			sb.append("&appid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&clientIp=").append(clientInfo.getIpAddress());
			//回调地址
			if(isCallback){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			//TODO 添加请求
			url = sb.toString();
			servicelog.info("请求磨盘的点击接口:user:{},idfa:{},request:{}",userid,clientInfo.getIdfa(),url);
			//磨盘成功就跳到appStore 下载
			Map<String, String> status = HttpUtil.getHttpStatus(url, null);
			servicelog.info("磨盘请求成功：user:{},idfa:{},response code:{}",userid,clientInfo.getIdfa(),status);
			String redirect = status.get("redirect");
			if(StringUtil.isNotBlank(redirect)){
				if(redirect.startsWith("https://itunes.apple.com")){
					return true;
				}
			}
		} catch (Exception e) {
			servicelog.info("磨盘请求异常：user:{},idfa:{},response cause by:{}",userid,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	/***
	 *掉钱眼不回调 排重 ＋ 用户完成上报  
	 */
	protected boolean isAllowDiaoqianyanReviceTaskByNotCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo){
		return isAllowDiaoqianyanReviceTaskByCallBack(appTask, user, clientInfo, vo, false);
	}
	//http://api.miidi.net/cas/j.bin?source=miaozhuan&appid=413993350CP&mac=&idfa=&clientIp=&callback=
	protected boolean isAllowMIDEReviceTaskCallbask(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo){
		String url = null;
		StringBuilder sb = new StringBuilder();
		
		//排重开始
		//http://api.miidi.net/cas/exist.bin?source=miaozhuan&appid=413993350CP&idfa=
		sb.append(vo.getAppChannel().getQuery_url());
		sb.append("?source=").append(vo.getAppChannel().getChannel_id());
		sb.append("&appid=").append(vo.getAppTaskChannel().getThird_id());
		sb.append("&idfa=").append(clientInfo.getIdfa());
		//排重结束
		String response = null;
		try {
			url = sb.toString();
			servicelog.info("调用米迪排重开始,user:{},request:{}",user,url);
			response = HttpUtil.get(url, null);
			servicelog.info("调用米迪排重结束,user:{},response:{}",user,response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				int code = json.getInt(clientInfo.getIdfa());
				if(code == 1){
					App app = appTaskService.getApp(appTask.getApp_id());
					try{
						//userInstalledAppService.insert(user.getId(), appTask.getApp_id(), clientInfo.getDid(), app.getAgreement());
					}catch(Exception e){}
					return false;
				}
			}
			sb.delete(0, sb.length());
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?source=").append(vo.getAppChannel().getChannel_id());
			sb.append("&appid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&ip=").append(clientInfo.getIpAddress());
			sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			url = sb.toString();
			servicelog.info("请求米迪的点击接口开始,user:{},requet:{}",user.getId(),url);
			Map<String, String> responseMap = HttpUtil.getHttpStatus(url, null);
			servicelog.info("请求米迪的点击接口结束,user:{},response:{}",user.getId(),responseMap);
			String code = responseMap.get("code");
			if("302".equals(code)||"301".equals(code)){
				String redirect = responseMap.get("redirect");
				if(StringUtil.isNotBlank(redirect)){
					return (redirect.startsWith("https://itunes.apple.com") || redirect.startsWith("http://itunes.apple.com"));
				}
			}
		} catch (Exception e) {
			servicelog.error("上游渠道米迪,user:{},appTask:{},cause by:{}",user,appTask.getId(),e);
		}
		return false;
	}

	private boolean isAllowDiaoqianyanReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isCallback) {
		App app = appTaskService.getApp(appTask.getApp_id());
		StringBuilder sb = new StringBuilder(320);
		sb.append(vo.getAppChannel().getQuery_url());
		sb.append("?appid=").append(app.getAppstore_id());
		sb.append("&source=").append(vo.getAppChannel().getChannel_id());
		sb.append("&connect=").append("0");
		sb.append("&idfa=").append(clientInfo.getIdfa());
		
		int userid = user.getId();
		
		String url = sb.toString();
		String response = null;
		JSONObject  json = null;
		try{
			servicelog.info("掉钱眼排重开始：idfa:{},user:{},appName:{},appid:{},request:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),url);
			response = HttpUtil.get(url, null);
			servicelog.info("掉钱眼排重结束：idfa:{},user:{},appName:{},appid:{},response:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),response);
			json = new JSONObject(response);
			if(json != null){
				if (json.has(clientInfo.getIdfa()) == false) {
					return false;
				}
				if(json.getInt(clientInfo.getIdfa()) == 1){
					try {
						//userInstalledAppService.insert(userid, app.getId(), clientInfo.getDid(), app.getAgreement());
					} catch (Exception e) {}
					return false;
				}
			}
		}catch(Exception e){
			servicelog.info("掉钱眼排重异常：idfa:{},user:{},appName:{},appid:{},cause by:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),e.getMessage());
			return false;
		}
		
		//是回调任务 就不去点击
		if(isCallback){
			return diaoqianyanClick(appTask, user, app, clientInfo, vo,isCallback);
		}
		//返回排重通过的结果
		return true;
	}
	/***
	 * 掉钱眼的点击 <br/>
	 * 掉钱眼有的任务是先排重,任务完成后点击上报
	 * @param appTask
	 * @param user
	 * @param app
	 * @param clientInfo
	 * @param vo
	 * @return
	 */
	public boolean diaoqianyanClick(AppTask appTask,User user,App app, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isClick){
		StringBuilder sb = new StringBuilder(640);
		
		try {
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?source=").append(vo.getAppChannel().getChannel_id());
			sb.append("&appid=").append(app.getAppstore_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&ip=").append(clientInfo.getIpAddress());
			//回调地址 只有点击的时候才有
			if(isClick){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			String url = sb.toString();
			servicelog.info("请求掉钱眼的点击接口:user:{},idfa:{},request:{}",user,clientInfo.getIdfa(),url);
			//磨盘成功就跳到appStore 下载
			String response = HttpUtil.get(url, null);
			servicelog.info("请求掉钱眼请求成功：user:{},idfa:{},response code:{}",user,clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optBoolean("success",false);
			}
		} catch (Exception e) {
			servicelog.info("掉钱眼请求异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	
	private boolean isAllowXingzheReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isCallback) {
		StringBuilder sb = new StringBuilder(640);
		try {
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?source=").append(vo.getAppChannel().getChannel_id());
			sb.append("&appid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&IDFA=").append(clientInfo.getIdfa());
			sb.append("&client_ip=").append(clientInfo.getIpAddress());
			//回调地址 只有点击的时候才有
			if(isCallback){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			String url = sb.toString();
			servicelog.info("请求行者天下的点击接口:user:{},idfa:{},request:{}",user,clientInfo.getIdfa(),url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求行者天下请求成功：user:{},idfa:{},response code:{}",user,clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optBoolean("success",false);
			}
		} catch (Exception e) {
			servicelog.info("行者天下请求异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	protected boolean isAllowYouQianReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo) {
		return isAllowYouQianReviceTaskByCallBack(appTask, user, clientInfo, vo, true);
	}
	
	/***
<<<<<<< .working
	 * 友钱
	 * partnerid：是固定的取上游渠道的标识
	 * taskid：跟任务相关 去任务中的第三方id
	 * @param appTask
	 * @param user
	 * @param clientInfo
	 * @param vo
	 * @param isCallback
	 * @return
	 */
	private boolean isAllowYouQianReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isCallback) {
		App app = appTaskService.getApp(appTask.getApp_id());
		StringBuilder sb = new StringBuilder(320);
		sb.append(vo.getAppChannel().getQuery_url());
		sb.append("?taskid=").append(vo.getAppTaskChannel().getThird_id());
		sb.append("&partnerid=").append(vo.getAppChannel().getChannel_id());
		sb.append("&connect=").append("0");
		sb.append("&idfas=").append(clientInfo.getIdfa());
		//sb.append("&ip=").append(clientInfo.getIpAddress());
		int userid = user.getId();
		
		String url = sb.toString();
		String response = null;
		JSONObject  json = null;
		try{
			servicelog.info("友钱排重开始：idfa:{},user:{},appName:{},appid:{},request:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),url);
			response = HttpUtil.get(url, null);
			servicelog.info("友钱排重结束：idfa:{},user:{},appName:{},appid:{},response:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),response);
			json = new JSONObject(response);
			json = json.getJSONObject("result");
			if(json != null){
				if (json.has(clientInfo.getIdfa()) == false) {
					return false;
				}
				if(json.getInt(clientInfo.getIdfa()) == 1){
					try {
						//userInstalledAppService.insert(userid, app.getId(), clientInfo.getDid(), app.getAgreement());
					} catch (Exception e) {}
					return false;
				}
			}
		}catch(Exception e){
			servicelog.info("友钱排重异常：idfa:{},user:{},appName:{},appid:{},cause by:{}",clientInfo.getIdfa(),userid,app.getName(),app.getId(),e.getMessage());
			return false;
		}
		//排重结束
		sb.delete(0, sb.length());
		try {
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?partnerid=").append(vo.getAppChannel().getChannel_id());
			sb.append("&taskid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&appid=").append(app.getAppstore_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&mac=").append("");
			sb.append("&ip=").append(clientInfo.getIpAddress());
			//回调地址
			if(isCallback){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			url = sb.toString();
			servicelog.info("请求友钱的点击接口:user:{},idfa:{},request:{}",userid,clientInfo.getIdfa(),url);
			response = HttpUtil.get(url, null);
			servicelog.info("请求友钱请求成功：user:{},idfa:{},response code:{}",userid,clientInfo.getIdfa(),response);
			json = new JSONObject(response);
			if(json != null){
				return json.getInt("code") == 200;
			}
		} catch (Exception e) {
			servicelog.info("友钱请求异常：user:{},idfa:{},response cause by:{}",userid,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	
	public boolean isAllowDuowanReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo){
		return isAllowDuoWanReviceTaskByCallBack(appTask, user, clientInfo, vo, true);
	}
	
	private boolean isAllowDuoWanReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isCallback) {
		String userid = user == null?"分发下游渠道":String.valueOf(user.getId());
		
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?ptid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&mac=").append("02:00:00:00:00:00");
			sb.append("&ip=").append(clientInfo.getIpAddress());
			//回调地址
			if(isCallback){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			String	url = sb.toString();
			servicelog.info("请求多顽的点击接口:user:{},idfa:{},request:{}",userid,clientInfo.getIdfa(),url);
			String	response = HttpUtil.get(url, null);
			servicelog.info("请求多顽请求成功：user:{},idfa:{},response code:{}",userid,clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optBoolean("success", false);
			}
		} catch (Exception e) {
			servicelog.info("友钱多顽异常：user:{},idfa:{},response cause by:{}",userid,clientInfo.getIdfa(),e);
		}
		return false;
	}
	/***
	 * 爱普优帮
	 * @param appTask
	 * @param user
	 * @param clientInfo
	 * @param vo
	 * @param isCallback
	 * @return
	 */
	private boolean isAllowAiPuYouBangReviceTaskByCallBack(AppTask appTask,User user, ClientInfo clientInfo, AppTaskChannelVo vo,boolean isCallback) {
		StringBuilder sb = new StringBuilder(640);
		try {
			//加密参数
			sb.append("&reqtype=").append("0");
			sb.append("&appid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&ip=").append(clientInfo.getIpAddress());
			sb.append("&isbreak=").append("0");
			sb.append("&device=").append(clientInfo.getModel());
			sb.append("&os=").append(clientInfo.getOSVersion());
			//os 是多少 device是多少
			//回调地址 只有点击的时候才有
			if(isCallback){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			//390770b3f76ae639
			String encode_data = AESCoder.encrryptAndEncodeByYouMi(sb.toString(), "390770b3f76ae639");
			sb.delete(0, sb.length());
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?srcid=").append(vo.getAppChannel().getChannel_id());
			sb.append("&ENCODE_DATA=").append(URLEncoder.encode(encode_data, "utf-8"));
			String url = sb.toString();
			servicelog.info("请求爱普优帮的点击接口:user:{},idfa:{},request:{}",user,clientInfo.getIdfa(),url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求爱普优帮请求成功：user:{},idfa:{},response code:{}",user,clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optInt("resultCode", 1) == 0;
			}
		} catch (Exception e) {
			servicelog.info("爱普优帮请求异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	
	private boolean isAllowJuYouQianReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo,User user,App app) {
		if(isAllowReviceJYQTask(appTask.getId(), clientInfo.getIdfa(), app.getAppstore_id(), vo) == false){
			return false;
		}
		
		String userid = user == null?"分发下游渠道":String.valueOf(user.getId());
		
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?Grant_type=").append("default");
			sb.append("&appid=").append(app.getAppstore_id());
			sb.append("&IDFA=").append(clientInfo.getIdfa());
			sb.append("&taskid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&IP=").append(clientInfo.getIpAddress());
			sb.append("&channelid=").append("74");
			sb.append("&Mac=").append("");
			//回调地址
			sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			String	url = sb.toString();
			servicelog.info("请求聚有钱回调的点击接口:user:{},idfa:{},request:{}",userid,clientInfo.getIdfa(),url);
			String	response = HttpUtil.get(url, null);
			servicelog.info("请求聚有钱回调的点击请求成功：user:{},idfa:{},response code:{}",userid,clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optBoolean("success", false);
			}
		} catch (Exception e) {
			servicelog.error("请求聚有钱回调的点击异常：user:{},idfa:{},response cause by:{}",userid,clientInfo.getIdfa(),e);
		}
		return false;
	}
	
	//http://asoapi.aiyingli.com/api/aso_source/cpid/517/
	private boolean isAllowAiPuYouBangNewReviceTask(AppTask appTask, ClientInfo clientInfo, AppTaskChannelVo vo,
			User user, App app,boolean isCallback) {
		
		StringBuilder sb = new StringBuilder(640);
		long timestamp = System.currentTimeMillis()/1000;
		try {
			sb.append(vo.getAppChannel().getClick_url());
			//加密参数
			sb.append("?reqtype=").append("0");
			sb.append("&appid=").append(app.getAppstore_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&ip=").append(clientInfo.getIpAddress());
			sb.append("&isbreak=").append("0");
			sb.append("&device=").append(clientInfo.getModel());
			sb.append("&os=").append(clientInfo.getOSVersion());
			sb.append("&timestamp=").append(timestamp);
			//os 是多少 device是多少
			//回调地址 只有点击的时候才有
			if(isCallback){
				sb.append("&callback=").append(genCallbackUrlData(vo, user, appTask, clientInfo.getIdfa()));
			}
			sb.append("&sign=").append(MD5Util.getMD5(timestamp+"356058973e054935fc94dfdb37714c03"));
			//390770b3f76ae639
			String url = sb.toString();
			servicelog.info("请求爱普优帮新的点击接口:user:{},idfa:{},request:{}",user,clientInfo.getIdfa(),url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求爱普优帮新的点击请求成功：user:{},idfa:{},response code:{}",user,clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			return json != null && json.optInt("code",1) == 0;
		} catch (Exception e) {
			servicelog.info("爱普优帮新的点击请求异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	
	private boolean isAllowLanMaoReviceTaskByDistinct(AppTask appTask, ClientInfo clientInfo,
			AppTaskChannelVo vo, User user) {
		StringBuilder sb = new StringBuilder(640);
		boolean  flag = false;
		try {
			sb.append(vo.getAppChannel().getQuery_url());
			//加密参数
			sb.append("?source=").append(vo.getAppChannel().getChannel_id());
			sb.append("&appiosid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			//390770b3f76ae639
			String url = sb.toString();
			servicelog.info("请求懒猫排重接口开始:user:{},request:{}",user,url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求懒猫排重接口结束：user:{},response code:{}",user,response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				flag = json.optInt(clientInfo.getIdfa(), 1) == 0;
				if(flag == false){
					//userInstalledAppService.addPreFilteredIDFA(appTask.getApp_id(), clientInfo.getIdfa());
				}
			}
		} catch (Exception e) {
			servicelog.info("请求懒猫排重接口异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		
		if(flag){
			flag = clickTaskByLanMao(vo, appTask, clientInfo);
		}
		
		return flag;
	}
	private boolean clickTaskByLanMao(AppTaskChannelVo vo, AppTask appTask, ClientInfo clientInfo) {
		boolean flag = false;
		String url="http://qc.cattry.com/Home/Union/click.html";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?source=").append(vo.getAppChannel().getChannel_id());
		sBuilder.append("&appiosid=").append(vo.getAppTaskChannel().getThird_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		sBuilder.append("&mac=02:00:00:00:00:00");
		sBuilder.append("&os=").append(clientInfo.getOSVersion());
		url = sBuilder.toString();
		try {
			servicelog.info("请求懒猫点击任务开始，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("请求懒猫点击任务响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			flag = jsonObject!= null && jsonObject.optInt("status",0) == 1;
			
		} catch (Exception e) {
			servicelog.error("请求懒猫点击任务异常，request:{},cause by:{}",url,e);
			flag = false;
		}
		return flag;
	}
	
	private boolean isAllowAiPuYouBnagNewReviceTaskByDistinct( ClientInfo clientInfo,
			AppTaskChannelVo vo, App app) {
		StringBuilder sb = new StringBuilder(640);
		try {
			//http://asoapi.aiyingli.com/api/aso_IdfaRepeat/cpid/517/
			sb.append(vo.getAppChannel().getQuery_url());
			//加密参数
			sb.append("?appid=").append(app.getAppstore_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			//390770b3f76ae639
			String url = sb.toString();
			servicelog.info("请求爱普友邦排重接口开始:request:{}",url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求爱普友邦排重接口结束：response code:{}",response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				//0:该 IDFA 已下载过不可做任务 1:该 IDFA 没下载过可以做任务
				return json.optInt(clientInfo.getIdfa(), 0) == 1;
			}
		} catch (Exception e) {
			servicelog.info("请求爱普友邦排重接口异常：idfa:{},response cause by:{}",clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	/***
	 * 友钱 泉州澎湃
	 * @param appTask
	 * @param clientInfo
	 * @param vo
	 * @param user
	 * @return
	 */
	private boolean isAllowLanYouQinaQZPPByDistinct(AppTask appTask,App app, ClientInfo clientInfo,
			AppTaskChannelVo vo, User user) {
		StringBuilder sb = new StringBuilder(640);
		try {
			sb.append(vo.getAppChannel().getQuery_url());
			//加密参数
			sb.append("?bid=").append(app.getBundle_id());
			sb.append("&source=").append(vo.getAppChannel().getChannel_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			//390770b3f76ae639
			String url = sb.toString();
			servicelog.info("请求友钱泉州澎湃排重接口开始:user:{},request:{}",user,url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求友钱泉州澎湃排重接口结束：user:{},response code:{}",user,response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optInt(clientInfo.getIdfa(), 1) == 0;
			}
		} catch (Exception e) {
			servicelog.info("请求友钱泉州澎湃排重接口异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
}
	
	private boolean isAllowMiaoLeByDistinct(AppTask appTask, App app, ClientInfo clientInfo, AppTaskChannelVo channelVo,
			User user) {
		StringBuilder sb = new StringBuilder(640);
		try {
			sb.append(channelVo.getAppChannel().getQuery_url());
			//加密参数
			sb.append("?bid=").append(app.getBundle_id());
			sb.append("&source=").append(channelVo.getAppChannel().getChannel_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&ip=").append(clientInfo.getIpAddress());
			//390770b3f76ae639
			String url = sb.toString();
			servicelog.info("请求秒乐排重接口开始:user:{},request:{}",user,url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求秒乐排重接口结束：user:{},response code:{}",user,response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				if(json.has(clientInfo.getIdfa()) ){
					boolean flag =  json.optInt(clientInfo.getIdfa(), 1) == 0;
					if(flag == false){
						try{
							//userInstalledAppService.addPreFilteredIDFA(app.getId(), clientInfo.getIdfa());
						}catch(Exception e){}
					}
					return flag;
				}
			}
		} catch (Exception e) {
			servicelog.info("请求秒乐排重接口异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	
	private boolean isAllowWuXiFeiMengByDistinct(AppTask appTask, App app, ClientInfo clientInfo,
			AppTaskChannelVo channelVo, User user) {
		StringBuilder sb = new StringBuilder(640);
		try {
			sb.append(channelVo.getAppChannel().getQuery_url());
			//加密参数
			sb.append("?appid=").append(channelVo.getAppTaskChannel().getThird_id());
			sb.append("&source=").append(channelVo.getAppChannel().getChannel_id());
			sb.append("&idfa=").append(clientInfo.getIdfa());
			sb.append("&ip=").append(clientInfo.getIpAddress());
			//390770b3f76ae639
			String url = sb.toString();
			servicelog.info("请求无锡飞梦排重接口开始:user:{},request:{}",user,url);
			String response = HttpUtil.get(url, null);
			servicelog.info("请求无锡飞梦排重接口结束：user:{},response code:{}",user,response);
			JSONObject json = new JSONObject(response);
			if(json != null){
				return json.optInt(clientInfo.getIdfa(), 1) == 0;
			}
		} catch (Exception e) {
			servicelog.info("请求无锡飞梦排重接口异常：user:{},idfa:{},response cause by:{}",user,clientInfo.getIdfa(),e.getMessage());
		}
		return false;
	}
	
	private boolean isAllowJuYouQianRealTimeDistinctReviceTask(AppTask appTask, ClientInfo clientInfo,
			AppTaskChannelVo vo, User user, App app) {

		StringBuilder sb = new StringBuilder();
		try {
			sb.append(vo.getAppChannel().getClick_url());
			sb.append("?Grant_type=").append("default");
			sb.append("&appid=").append(app.getAppstore_id());
			sb.append("&IDFA=").append(clientInfo.getIdfa());
			sb.append("&isFliterIDFA=1");
			sb.append("&taskid=").append(vo.getAppTaskChannel().getThird_id());
			sb.append("&IP=").append(clientInfo.getIpAddress());
			sb.append("&channelid=").append("74");
			String	url = sb.toString();
			servicelog.info("请求聚有钱实时排重接口:user:{},idfa:{},request:{}",user.getId(),clientInfo.getIdfa(),url);
			String	response = HttpUtil.get(url, null);
			servicelog.info("请求聚有钱实时排重接口请求成功：user:{},idfa:{},response code:{}",user.getId(),clientInfo.getIdfa(),response);
			JSONObject json = new JSONObject(response);
			return json != null &&  json.optInt(clientInfo.getIdfa(), 1) == 0;
		} catch (Exception e) {
			servicelog.error("请求聚有钱实时排重接口异常：user:{},idfa:{},response cause by:{}",user.getId(),clientInfo.getIdfa(),e);
		}
		return false;
	}
	
	/***
	 * 新增上级渠道任务回调地址<br/>
	 * _data制作简单的加密:<br/>
	 * src = ${渠道Id}|${用户标识}|${app的id}|${idfa}<br/>
	 * _data=aes(src,key);<br/>
	 * 解密：src={@link #getCallBackData(String _data)}
	 * @param vo
	 * @param user
	 * @param task
	 * @param app
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String genCallbackUrlData(AppTaskChannelVo vo,User user, AppTask appTask, String idfa) throws UnsupportedEncodingException{
		String callback = vo.getAppChannel().getCallback_url();
		if(StringUtil.isBlank(callback)){
			if(GlobalConfig.isDeploy){
				callback = "http://third.miaozhuandaqian.com/www/third/callback_pre.3w";
			}else{
				callback = "http://www.mapi.lieqicun.com/www/third/callback_pre.3w";
			}
		}
		StringBuilder sb = new StringBuilder(320);
		sb.append(vo.getAppChannel().getId()).append("|");
		sb.append(user.getUser_identity()).append("|");
		sb.append(appTask.getApp_id()).append("|");
		sb.append(idfa);
		String _data =  AesCryptUtil.encryptWithCaseInsensitive(sb.toString(), CALLBACL_AES_KEY);
		sb.delete(0, sb.length());
		sb.append(callback).append("?_data=").append(_data);
		return URLEncoder.encode(sb.toString(), "utf-8");
	}
	
	/**
	 * src = ${渠道Id}|${用户标识}|${app的id}|${idfa}<br/>
	 * _data=aes(src,key);<br/>
	 * {@link #genCallbackUrlData(AppTaskChannelVo, User, AppTask, String)}
	 * @param srcCallBackData
	 * @return
	 */
	public String getCallBackData(String srcCallBackData){
		return AesCryptUtil.decryptWithCaseInsensitive(srcCallBackData, CALLBACL_AES_KEY);
	}


	/**
	 * 向渠道上报激活信息
	 * @param appTask
	 * @param ut
	 * @param clientInfo
	 * @return
	 */
	public boolean reportTaskToChannel(AppTask appTask, UserTask ut, ClientInfo clientInfo){
		boolean status = false;
		if(appTask.getIschannel() > 0) {
			//检查是否有回调
			AppTaskChannelVo appTaskChannel = getAppTaskChannel(appTask.getId());
			//boolean isCallBack = appTaskChannel.getAppChannel().isCallBack();
			//该任务是不是要上报
			if(appTaskChannel.getAppTaskChannel().isNeedReport()){
				int i = 1;
				servicelog.info("上报任务开始,userId:{},idfa:{},taskId:{},usertask:{}",ut.getUser_id(), ut.getIdfa(),appTask.getId(),ut.getId());
				status = doReportChannel(appTask,appTaskChannel,clientInfo);
				servicelog.info("上报任务结束,userId:{},idfa:{},taskId:{},usertask:{},上报任务:{}", ut.getUser_id(), ut.getIdfa(),appTask.getId(),ut.getId(), status);
				//重试次数三次
				//TODO 如果都还失败 那就交给JOB去执行 修改标识位 
				int times = 3;
				while(i < times && status == false){
					i++;
					if(status == false){
						status = doReportChannel(appTask,appTaskChannel,clientInfo);
						servicelog.info("第 {} 次上报任务结束,userId:{},idfa:{},taskId:{},usertask:{},上报任务:{}",i, ut.getUser_id(), ut.getIdfa(),appTask.getId(),ut.getId(), status);
					}
				}
				if(status){
					userTaskService.updateReportStatus(ut.getUser_id(), ut.getId());
				}
			}
		}
		return status;
	}
	
	public boolean doReportChannel(AppTask appTask, AppTaskChannelVo vo,ClientInfo clientInfo){
		boolean rtv = false;
		App app = appTaskService.getApp(appTask.getApp_id());
		switch(vo.getAppChannel().getId()) {
			case AppChannel.CHANNEL_51HB:
				rtv = reportFinshTaskBy51HB(vo, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_DIAOQIANYANER_NOT_CALLBACK:
				rtv = diaoqianyanClick(appTask, null, app, clientInfo, vo,false);
				break;
			case AppChannel.CHANNEL_QUMI:
				rtv = reportFinshTaskByQuMi(vo, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_QUMI_DISTINCT:
				rtv = reportFinshTaskByQuMi(vo, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_TONGBANQIANG_NOT_DISTINCT:
				rtv = reportFinshTaskByTongBanQiang(vo,app, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_LANMAO_DISTINCT:
				rtv = reportFinshTaskByLanMao(vo, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_AIPUYOUBANG_NEW_NOT_CALLBACK:
				rtv = reportFinshTaskByAiPuYouBang(vo, appTask, app, clientInfo);
				break;
			case AppChannel.CHANNEL_YOUQIAN_QZPP_NOT_CALLBACK:
				rtv = reportFinshTaskByYouQianQZPP(vo, app, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_MIAOLE_DISTINCT:
				rtv = reportFinshTaskByMiaoLe(vo, app, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_WUXIFEIMENG_NOTCALLBACK:
				rtv = reportFinshTaskByWuXiFeiMeng(vo, app, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_REFANQIE_NOT_CALLBACK:
				rtv = reportFinshTaskByReFanQie(vo, app, appTask, clientInfo);
				break;
			case AppChannel.CHANNEL_SHIKE_DISCINCT:
				rtv = reportFinshTaskByShiKe(vo, app, appTask, clientInfo);
				break;
		}
		return rtv;
	}
	
	private boolean reportFinshTaskByShiKe(AppTaskChannelVo channel, App app, AppTask appTask, ClientInfo clientInfo) {
		String url="http://ad.pher156.com/checkbill";
		StringBuilder sBuilder = new StringBuilder(320);
		sBuilder.append(url);
		sBuilder.append("?channel=").append(channel.getAppTaskChannel().getThird_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报试客任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报试客响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optInt(clientInfo.getIdfa(),2) == 1;
			
		} catch (Exception e) {
			servicelog.error("上报试客任务异常，request:{},cause by:{}",url,e);
		}
	
		return rtv;
	}
	private  boolean executeBy51HBCallBack(AppTaskChannelVo channel,AppTask appTask,ClientInfo clientInfo,User user,String url1,boolean callback){
		StringBuilder sb = new StringBuilder();
		//上报地址在程序中写死
		String idfa = clientInfo.getIdfa();
		sb.append(url1);
		sb.append("?idfa=").append(idfa);
		sb.append("&bid=").append(channel.getAppTaskChannel().getThird_id());
		sb.append("&source=").append(channel.getAppChannel().getChannel_id());
		sb.append("&ip=").append(clientInfo.getIpAddress());
		try{ 
			if(callback && user != null){
				sb.append("&callbackurl=").append(genCallbackUrlData(channel, user, appTask, idfa));
			}
			String rs = "";
			String url = sb.toString();
			servicelog.info("51红包上报 appid={},check {} callbackChannelBy51HB {} request url:{}",appTask.getApp_id(), idfa, appTask.getId(),url);
			rs = HttpUtil.get(url, null);
			servicelog.info("51红包上报 appid={},check {} callbackChannelBy51HB {} retrun: {}",appTask.getApp_id(), idfa, appTask.getId(), rs);
			if(rs!= null && !rs.isEmpty()) {
				JSONObject json = new JSONObject(rs);  
				if(json!= null && json.has("success") && json.getBoolean("success")) {
					logger.info("51红包上报成功-idfa:{},taskId:{}",idfa,appTask.getId());
					return true;
				}else{
					logger.info("51红包上报失败-idfa:{},taskId:{}",idfa,appTask.getId());
				}
			}  
		} catch(Exception e) {
			servicelog.info("51红包上报失败,appid="+appTask.getApp_id()+",check "+idfa+" callbackChannelBy51HB is error:" + e.getMessage());
		}
		return false;
	}
	protected boolean reportFinshTaskBy51HB(AppTaskChannelVo channel,AppTask appTask,ClientInfo clientInfo){
		//"http://www.51hb.me/Hongbao/Submit"
		return executeBy51HBCallBack(channel, appTask, clientInfo, null,"http://www.51hb.me/Hongbao/Submit", false);
	} 
	
	protected boolean reportFinshTaskByQuMi(AppTaskChannelVo channel,AppTask appTask,ClientInfo clientInfo){
		String url="http://new.wall.qumi.com/api/opendata/idfasubmit";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?app=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&adid=").append(channel.getAppTaskChannel().getThird_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报趣米任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报趣米响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optInt("code",1) == 0;
			
		} catch (Exception e) {
			servicelog.error("上报趣米任务异常，request:{},cause by:{}",url,e);
		}
	
		return rtv;
	}
	
	private boolean reportFinshTaskByTongBanQiang(AppTaskChannelVo channel,App app, AppTask appTask, ClientInfo clientInfo) {
		String url="http://121.40.57.89:8004/vendor/iosnotification";
		if (GlobalConfig.isDeploy) {
			url = "http://open.tongbanqiang.com/vendor/iosnotification";
		}
		StringBuilder sBuilder = new StringBuilder(640);
		String appid =channel.getAppTaskChannel().getThird_id();
		if(StringUtil.isBlank(appid)){
			appid = app.getAppstore_id();
		}
		sBuilder.append(url);
		sBuilder.append("?channelid=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&appid=").append(appid);
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报铜板墙任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报铜板墙响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optInt("code",1) == 0;
			
		} catch (Exception e) {
			servicelog.error("上报铜板墙任务异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	
	private boolean reportFinshTaskByLanMao(AppTaskChannelVo channel, AppTask appTask, ClientInfo clientInfo) {
		String url="http://qc.cattry.com/Home/Union/active.html";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?source=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&appiosid=").append(channel.getAppTaskChannel().getThird_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报懒猫任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报懒猫任务响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optInt("status",0) == 1;
			
		} catch (Exception e) {
			servicelog.error("上报懒猫任务异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	
	private boolean reportFinshTaskByAiPuYouBang(AppTaskChannelVo channel, AppTask appTask,App app, ClientInfo clientInfo) {
		String url="http://asoapi.aiyingli.com/api/aso_Submit/cpid/517/";
		long timestamp = System.currentTimeMillis()/1000;
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?appid=").append(app.getAppstore_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		sBuilder.append("&timestamp=").append(timestamp);
		sBuilder.append("&sign=").append(MD5Util.getMD5(timestamp+"356058973e054935fc94dfdb37714c03"));
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报爱普友邦开始，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报爱普友邦响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optInt("code",1) == 0;
			
		} catch (Exception e) {
			servicelog.error("上报爱普友邦任务异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	
	protected boolean reportFinshTaskByYouQianQZPP(AppTaskChannelVo channel,App app,AppTask appTask,ClientInfo clientInfo){
		String url="http://120.24.163.58/chuangli/callback_commit.php";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?bid=").append(app.getBundle_id());
		sBuilder.append("&source=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报友钱泉州澎湃任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报友钱泉州澎湃响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && "success".equals(jsonObject.getString(clientInfo.getIdfa()));
			
		} catch (Exception e) {
			servicelog.error("上报友钱泉州澎湃任务异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	protected boolean reportFinshTaskByMiaoLe(AppTaskChannelVo channel,App app,AppTask appTask,ClientInfo clientInfo){
		String url="http://121.201.28.156/miaole/task/commit.php";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?bid=").append(app.getBundle_id());
		sBuilder.append("&source=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报秒乐任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报秒乐响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && "1".equals(jsonObject.getString("result"));
			
		} catch (Exception e) {
			servicelog.error("上报秒乐任务异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	protected boolean reportFinshTaskByWuXiFeiMeng(AppTaskChannelVo channel,App app,AppTask appTask,ClientInfo clientInfo){
		String url="http://www.apptyk.com/data/report";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?appid=").append(channel.getAppTaskChannel().getThird_id());
		sBuilder.append("&source=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报无锡飞梦任务，request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报无锡飞梦响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optBoolean("success",false);
			
		} catch (Exception e) {
			servicelog.error("上报无锡飞梦任务异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	
	private boolean clickTaskByReFanQie(AppTaskChannelVo channel,App app,AppTask appTask,ClientInfo clientInfo){
		String url="http://api.refanqie.com/1/hlw-coreapi/channel/reportClick.json";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?appid=").append(app.getAppstore_id());
		sBuilder.append("&channel=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		sBuilder.append("&ip=").append(clientInfo.getIpAddress());
		sBuilder.append("&version=").append(clientInfo.getOSVersion());
		sBuilder.append("&model=").append(clientInfo.getModel());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("热番茄点击开始,request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("热番茄点击响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optBoolean("success",false);
			
		} catch (Exception e) {
			servicelog.error("热番茄点击异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	private boolean reportFinshTaskByReFanQie(AppTaskChannelVo channel,App app,AppTask appTask,ClientInfo clientInfo){
		String url="http://api.refanqie.com/1/hlw-coreapi/channel/submitIdfa.json";
		StringBuilder sBuilder = new StringBuilder(640);
		sBuilder.append(url);
		sBuilder.append("?appid=").append(app.getAppstore_id());
		sBuilder.append("&channel=").append(channel.getAppChannel().getChannel_id());
		sBuilder.append("&idfa=").append(clientInfo.getIdfa());
		url = sBuilder.toString();
		boolean rtv = false;
		try {
			servicelog.info("上报热番茄开始,request:{}",url);
			String response = HttpUtil.get(url, null,10000);
			servicelog.info("上报热番茄响应:request:{},response:{}",url,response);
			JSONObject jsonObject = new JSONObject(response);
			rtv = jsonObject!= null && jsonObject.optBoolean("success",false);
			
		} catch (Exception e) {
			servicelog.error("上报热番茄异常，request:{},cause by:{}",url,e);
		}
		return rtv;
	}
	private static Logger log = LoggerFactory.getLogger(AsyncJob.class);
	private AsyncJob<ThirdModel> reviceTask = new AsyncJob<ThirdModel>("第三方广告点击", new JobHandler<ThirdModel>() {
		@Override
		public boolean handle(ThirdModel t) {
			return reviceTaskAsyncJob(t.getIdfa(), t.getAppId(), t.getVo(), t.getIp());
		}
	}, 20);
	/**
	 * 
	 * @param idfa
	 * @param appId
	 * @param vo
	 * @param ip
	 * @return
	 */
	public boolean reviceTask(String idfa, String appId, AppTaskChannelVo vo, String ip) {
		ThirdModel model = new ThirdModel();
		model.setIdfa(idfa);
		model.setAppId(appId);
		model.setVo(vo);
		model.setIp(ip); 
	    return reviceTask.offer(model);
	}
	/**
	 * 广告点击
	 * @param idfa
	 * @param appId
	 * @param vo
	 * @param ip
	 * @return
	 * @throws Exception
	 */
	protected boolean reviceTaskAsyncJob(String idfa, String appId, AppTaskChannelVo vo, String ip) {
		String url = vo.getAppChannel().getClick_url();
		if(url == "") return true;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("idfa", idfa);
		params.put("appId", appId);
		params.put("channelid", vo.getAppChannel().getChannel());
		params.put("taskid", vo.getAppTaskChannel().getThird_id()+"");
		params.put("ip", ip);
		params.put("callback", vo.getAppChannel().getCallback_url());
		try{
			String rs = HttpUtil.postByForm(url, params);
			JSONObject json = new JSONObject(rs); 
			return (Boolean) json.get("success");
		} catch(Exception e) {
			log.error("error when reviceTaskAsyncJob exec", e);
			return false;
		}
	}
	static class ThirdModel{ 
		private String idfa;
		private String appId;
		private AppTaskChannelVo vo;
		private String ip;
		
		public String getIdfa() {
			return idfa;
		}
		public void setIdfa(String idfa) {
			this.idfa = idfa;
		}
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		public AppTaskChannelVo getVo() {
			return vo;
		}
		public void setVo(AppTaskChannelVo vo) {
			this.vo = vo;
		}
		public String getIp() {
			return ip;
		}
		public void setIp(String ip) {
			this.ip = ip;
		}
	}
	
}
