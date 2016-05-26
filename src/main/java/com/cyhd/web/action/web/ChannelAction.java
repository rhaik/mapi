package com.cyhd.web.action.web;

import com.cyhd.common.util.*;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppChannelDistribution;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.UserTaskNotify;
import com.cyhd.service.impl.*;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.vo.AppTaskVo;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.Map.Entry;

/**
 * 应用渠道
 * Created by hy on 9/15/15.
 */
@Controller
@RequestMapping("/www/channel")
public class ChannelAction {

    private final static Logger logger = LoggerFactory.getLogger("channel");

    @Resource
    private AppVendorService appVendorService;
    
    @Resource
    private AppTaskService appTaskService;
    
    @Resource
    private UserTaskService userTaskService;
    
    @Resource
    private UserTaskNotifyService userTaskNotifyService;
    
    @Resource
    DistributeChannelService distributeChannelService;
    
    @Resource
    private ChannelService channelService;
    
    @Resource
    private UserService userService;
    
    @Resource
    private UserTaskCalculateService userTaskCalculateService;


	/**
	 * 渠道获取所有的分发任务列表，类似点入快速任务
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/tasks.3w", produces = Constants.APPLICATION_JSON_VALUE)
	@ResponseBody
	public String getAllQuickTasks(HttpServletRequest request, HttpServletResponse response){
		String idfa = request.getParameter("idfa");
		int channelId = ServletRequestUtils.getIntParameter(request, "channel", 0);
		String sign = request.getParameter("sign");

		String result = null;
		if (StringUtil.isBlank(idfa)){
			result = "idfa is required";
		}else if (channelId <= 0) {
			result = "channel is required";
		}else if (StringUtil.isBlank(sign)){
			result = "sign is required";
		}else {
			AppChannelDistribution appChannel = distributeChannelService.getAppChannelDistribution(channelId);
			if (appChannel == null){
				result = "channel does not exist";
			}else if ( !check(idfa, 0, channelId, appChannel.getApp_secret(), sign)){
				result = "invalid sign";
			}else {
				//parameter pass
				List<AppTask> channelTasks = appTaskService.getChannelTasks(channelId);
				List<AppTaskVo> validTasks = new ArrayList<>(channelTasks.size());
				for (AppTask task : channelTasks){
					if (task.getLeftTasks() <= 0){ //任务无剩余，直接pass
						continue;
					}

					App app = appTaskService.getApp(task.getApp_id());

					boolean isReceived = false; //true表示排重未通过
					//如果App有厂商，先走厂商批量排重
					if(app.getVendor_id() > 0){
						Map<String, Integer> idfaResult = appVendorService.onDisctinctNew(app, idfa);
						if (idfaResult != null && (idfaResult.get(idfa) == null || idfaResult.get(idfa) == 1) ){
							isReceived = true;
						}
					}

					//再次调用本系统排重
					if (!isReceived){
						App twinApp = appTaskService.getTwinApp(app.getId());
						int twinAppId = twinApp == null ? 0 : twinApp.getId();
						isReceived = userTaskNotifyService.isReceivedByIdfa(idfa, task.getApp_id(), twinAppId, channelId);

						//排重通过
						if (!isReceived){
							AppTaskVo appTaskVo = new AppTaskVo();
							appTaskVo.setApp(app);
							appTaskVo.setAppTask(task);
							validTasks.add(appTaskVo);
						}
					}
				}


				//组装任务数据为json
				JSONArray array = new JSONArray();
				validTasks.forEach(appTaskVo -> array.add(JSONObject.fromObject(appTaskVo.getChannelTaskInfo())));

				String json = array.toString();
				logger.info("getAllQuickTasks success, idfa:{}, channel:{}, tasks:{}", idfa, channelId, json);
				return json;
			}
		}

		logger.error("getAllQuickTasks error, idfa:{}, channel:{}, error:{}", idfa, channelId, result);
		response.setStatus(400);
		response.setHeader("Error-Message", result);
		return "[]";
	}


    /**
     * 第三方渠道点击
     * @return
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/click.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String onClick(HttpServletRequest request) {
    	return executeClick(request);
    }
    
    /**
     * 排重接口
     * @return
     * @throws UnsupportedEncodingException 
     */
    @RequestMapping(value = "/disctinct.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String disctinct(HttpServletRequest request) {
    	logger.info("disctinct request " + request.getRequestURL() + "?" + RequestUtil.getQueryString(request)); 
		int code = 400;
		String result = null;
		String idfa = request.getParameter("idfa");
		int adid = NumberUtil.safeParseInt(request.getParameter("adid"));
		int channelId = ServletRequestUtils.getIntParameter(request, "channel", 0);
		String serct = request.getParameter("sign");
		if(StringUtils.isBlank(idfa)) {
			result = "idfa is required";
		} else if(adid <=0) {
			result = "adid is required";
		}
		
		if(channelId == 0 || isExceptedChannel(channelId)){
			//不校验channel
		}else{
			AppChannelDistribution appChannel = distributeChannelService.getAppChannelDistribution(channelId);
	        if(appChannel == null) {
	        	 result = "channel not exist";
	        }else{
	        	//add check key idfa 不加入
	        	if(check(null, adid, channelId, appChannel.getApp_secret(), serct) == false){
	        		result="source is wrong";
	        	}
	        }
		}
		int appid = (adid - 997) / 11;
		App app = appTaskService.getApp(appid);
		if(app == null) {
			result = "adid is error";
		}
		if (result == null){

			//如果App有厂商，先走厂商批量排重
			if(app.getVendor_id() > 0){
				result = this.appVendorService.onDisctinct(app, idfa);
			}

			//厂商批量排重失败，继续在本系统排重
			App twinApp = appTaskService.getTwinApp(app.getId());
			int twinAppId = twinApp == null ? 0 : twinApp.getId();
			if (result == null){

				String[] idfaArray = idfa.split(",");
		    	if(idfaArray.length <= 500) {
		    		StringBuilder str = new StringBuilder();
		        	str.append("{");
	//	        	for (int i = 0; i < idfaArray.length; i++) {
	//	        		boolean isReviced = userTaskService.isRevicedByIdfa(idfaArray[i], appid);
	//	        		str.append("\""+idfaArray[i]+ "\":"+isReviced+",");
	//				}
	//	        	str.setLength(str.toString().length() - 1);
		        	str.append(userTaskNotifyService.isRevicedByIdfaArray(idfaArray, appid, twinAppId, channelId));
		        	str.append("}");
		        	result = str.toString();
		        	code = 0;
		    	} else {
		    		result = "idfa too long";
		    	}
			}else { //厂商排重之后，再在本系统排一次
				List<String> idfaList = new ArrayList(Arrays.asList(idfa.split(",")));
				JSONObject json = JSONObject.fromObject(result);
				for (Object key : json.keySet()){
					//已经接过，不需要再次排重
					if(json.getBoolean(String.valueOf(key)) == true){
						idfaList.remove(key);
					}
				}

				StringBuilder str = new StringBuilder(320);
				if(idfaList.size() > 0){
					str.append("{");
					str.append(userTaskNotifyService.isRevicedByIdfaArray(idfaList.toArray(new String[0]), appid, twinAppId, channelId));
					str.append("}");
					
					JSONObject tmp = JSONObject.fromObject(str.toString());
					json.putAll(tmp);
					result = json.toString();
				}
				//厂商排重成功
				code = 0;
			}
		 }

        String response = String.format("{\"code\": %d,\"result\": %s}", code, result.charAt(0) == '{' ? result : JsonUtils.jsonQuote(result));
        logger.info("disctinct:result:{}", response);

        return response;
    }

    /**
     * 保存任务
     * @param request
     * @param appId
     * @param appTask
     * @param idfa
     * @param channelId
     * @return
     */
    private boolean saveTask(HttpServletRequest request, int appId, AppTask appTask, String idfa, int channelId) {
    	String callbackurl = request.getParameter("callbackurl");
    	if(callbackurl == null){
    		callbackurl = request.getParameter("callback");
    	}
		try{
			callbackurl = URLDecoder.decode(callbackurl, "utf-8");
		}catch(Exception e) {}

		//首先看看有无历史任务
		UserTaskNotify utn = userTaskNotifyService.getByIdfaAndAppId(idfa, appId);
		if (utn != null){
			if (utn.getStatus() == 1 && utn.isVilid()){ //进行中的任务
				if (channelId == utn.getChannel()){
					logger.info("channel reclick task, channel:{}, idfa:{}, appid:{}, task:{}", channelId, idfa, appId, appTask.getId());
					return true;
				}else {
					logger.error("channel click doing task, not the same channel. channel:{}, idfa:{}, appid:{}, task:{}", channelId, idfa, appId, appTask.getId());
					return false;
				}
			}else if (utn.getStatus() != 1){
				logger.error("channel click finished task. channel:{}, idfa:{}, appid:{}, task:{}", channelId, idfa, appId, appTask.getId());
				return false;
			}else {
				//点击已经过期的任务，OK
				logger.info("channel click expired task. channel:{}, idfa:{}, appid:{}, task:{}. former utn:{} ", channelId, idfa, appId, appTask.getId(), utn);
			}
		}else {
			utn = new UserTaskNotify();
		}

		//保存点击数据
		utn.setAction("activate");
		utn.setApp_id(appId);
		utn.setTask_id(appTask.getId());

		//开始时间是当前时间
		utn.setStarttime(GenerateDateUtil.getCurrentDate());

		//结束时间是任务结束时间后的一个小时，结束时间之后，就算厂商回调或者渠道上报了，也认为无效
		utn.setExpiretime(DateUtil.getAddDate(appTask.getEnd_time(), Calendar.HOUR, 1));

		utn.setIdfa(idfa);
		utn.setChannel(channelId);
		utn.setIp(request.getParameter("ip"));
		utn.setMac(request.getParameter("mac"));
		utn.setCallbackurl(callbackurl);
		int type = (appTask.isVendorTask() && appTask.isDirectReward() == false) ? 2 : 1;
		utn.setType(type);

		if (utn.getId() > 0){ //restart old task
			logger.info("restart channel task:{}", utn);
			return userTaskNotifyService.restartTask(utn);
		}else { //add new task
			return userTaskNotifyService.addTask(utn) && appTaskService.onUserReceiveTask(appTask.getId());
		}
    }
    /**
     * 检查请求参数是否正确
     * @param request
     * @return
     */
    private String checkParameters(String idfa,int adid,int channelId,String ip,String secret){
        String result = null;
        if(adid <= 0){
            result = "adid is required";
        }else if (StringUtils.isBlank(idfa)){
            result = "idfa is required";
        }else if (StringUtils.isBlank(ip)){
            result = "ip is required";
        }

        if (result == null){
        	if(idfa.length() != 36) {
        		result = "idfa is error";
        	}
        	AppChannelDistribution appChannel = distributeChannelService.getAppChannelDistribution(channelId);
            if(appChannel == null) {
            	 result = "channel not exist";
            }
            if(result == null){
            	if(check(null, adid, channelId, appChannel.getApp_secret(), secret) == false){
            		result = "sign is wrong";
            	}
            }
            if(!Validator.isRealIpByV4(ip)) {
            	logger.error("渠道点击ip有问题:channel:{},ip:{},idfa:{}",channelId,ip,idfa);
            	result = "ip format error";
            }
        }
        return result;
    }

    @RequestMapping(value = "/report_hsk.3w", produces = "text/json; charset=UTF-8")
    @ResponseBody
    public String reportTask(HttpServletRequest request,HttpServletResponse response) {
    	//bid,idfa,source,adid
    	String ip = RequestUtil.getIpAddr(request);
    	String query = RequestUtil.getQueryString(request);
    	StringBuilder sb = new StringBuilder(200);
    	sb.append("上报接口,ip:").append(ip);
    	sb.append(",query:").append(query);
    	
    	String idfa = request.getParameter("idfa");
        int channelId = ServletRequestUtils.getIntParameter(request, "channel", 0);
        int adid = ServletRequestUtils.getIntParameter(request, "adid", 0);
    	String serct = request.getParameter("sign");
        String formatResp = "{\"code\":%s,\"result\":%s} ";
        if(StringUtils.isBlank(idfa) 
        		||channelId < 1
        		||adid < 997){
        	logger.warn(sb.toString()+"参数为null");
        	return String.format(formatResp,400, "\"关键参数为null\"");
        }
        //TODO add token 
        AppChannelDistribution appChannel = distributeChannelService.getAppChannelDistribution(channelId);
        if(appChannel == null){
        	sb.append(",没有找到该渠道");
			logger.warn(sb.toString());
			return String.format(formatResp,400,"\"来源不对\"");
        }
        //TODO  check
        if(check(null, adid, channelId, appChannel.getApp_secret(), serct) == false){
        	sb.append(",加解密不对:").append(channelId);
			logger.warn(sb.toString());
			return String.format(formatResp,400,"\"来源不对\"");
        }
        
        int appid = (adid - 997) / 11;
		App app = appTaskService.getApp(appid);
		if(app == null){
			sb.append(",没有找到响应的app");
			logger.warn(sb.toString());
			return String.format(formatResp,400,"\"没有找到相应的任务\"");
		}
    	
		AppTask appTask = appTaskService.getTaskByAppIdAndChannelId(appid, channelId);
		if(appTask == null){
			sb.append("获取到的appTask为null");
			logger.error(sb.toString());
			return String.format(formatResp,400,"\"没有找到相应的任务\"");	
		}
		String[] idfaArr = idfa.split(",");
		if(idfaArr.length > 100){
			//TODO 超出最大值 
			sb.append(",IDFA的长度太长,超出最大长度100");
			logger.error(sb.toString());
			return String.format(formatResp,400,"\"idfa数据太多,最多100条\"");
		}
		//TODO add to job-thread
		String resp =  userTaskNotifyService.executorThreeRepotTask(idfaArr, appTask, channelId, ip, null, app);
		sb.append(",数据处理,处理后的:{}").append(resp);
		logger.info(sb.toString());
		resp = "{"+resp+"}";
		return String.format(formatResp,0,resp);
    }
    
    @RequestMapping(value = "/click_kd.3w")
	@ResponseBody
	public String onClickByKouDaiATM(HttpServletRequest request) {
    	logger.info("点击请求来着口袋ATM");
		String executeData = executeClick(request);
		JSONObject json = JSONObject.fromObject(executeData);
		int code = json.optInt("code",400);
		return code == 0?"200":"400";
	}
    

    @RequestMapping(value = "/disctinct_new.3w", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String disctinctNew(HttpServletRequest request,HttpServletResponse response) {
    	String queryString = RequestUtil.getQueryString(request);
    	logger.info("disctinct_new request " + request.getRequestURL() + "?" + queryString); 
		String result = null;
		String idfa = request.getParameter("idfa");
		int adid = NumberUtil.safeParseInt(request.getParameter("adid"));
		int channelId = ServletRequestUtils.getIntParameter(request, "channel", 0);
		String serct = request.getParameter("sign");
		if(StringUtils.isBlank(idfa)) {
			result = "idfa is required";
		} else if(adid <=0) {
			result = "adid is required";
		}
		
		if(isExceptedChannel(channelId)){
			//不校验channel
		}else{
			AppChannelDistribution appChannel = distributeChannelService.getAppChannelDistribution(channelId);
	        if(appChannel == null) {
	        	 result = "channel not exist";
	        }else{
	        	//add check key idfa 不加入
	        	if(check(null, adid, channelId, appChannel.getApp_secret(), serct) == false){
	        		result="source is wrong";
	        	}
	        }
		}
		int appid = (adid - 997) / 11;
		App app = appTaskService.getApp(appid);
		if(app == null) {
			result = "adid is error";
		}else{
			//判断任务是不是有效
			AppTask appTask = appTaskService.getTaskByAppIdAndChannelId(appid, channelId);
			if(appTask == null){
				result = "task is not found";
			}else if(appTask.getReceived_task() >= (appTask.getCurrent_task() * 2 )){ //最多不超过两倍
				result = "task is not valid";
			}
		}
		
		if (result == null){
			Map<String, Integer> idfaResult = null;
			//如果App有厂商，先走厂商批量排重
			if(app.getVendor_id() > 0){
				idfaResult = this.appVendorService.onDisctinctNew(app, idfa);
			}

			//厂商批量排重失败，继续在本系统排重
			App twinApp = appTaskService.getTwinApp(app.getId());
			int twinAppId = twinApp == null ? 0 : twinApp.getId();
			List<String> idfaList = new ArrayList<String>();
			if (idfaResult == null || idfaResult.isEmpty()){
				if(idfaResult == null){
					idfaResult = new HashMap<>();
				}
				idfaList.addAll(Arrays.asList(idfa.split(",")));
			}else { //厂商排重之后，再在本系统排一次
				Set<Entry<String, Integer>> entrys = idfaResult.entrySet();
				Iterator<Entry<String, Integer>> iterator = entrys.iterator();
				Entry<String, Integer> entry = null;
				while(iterator.hasNext()){
					entry = iterator.next();
					//通过的需要再一次系统排重
					if(entry.getValue() == 0){
						idfaList.add(entry.getKey());
					}
				}
			}
			
			if(idfaList.size() > 0){
				try {
					idfaResult.putAll(userTaskNotifyService.isRevicedByIdfa(idfaList.toArray(new String[0]), appid, twinAppId, channelId));
				} catch (Exception e) {
					logger.error("新排重接口处理系统中的idfa出现异常：,request:{},{}",queryString,e);
				}
			}
			result = JSONObject.fromObject(idfaResult).toString();
		 }else{
			//404处理
			response.setStatus(400);
			response.setHeader("Error-Message", result);
			logger.info("新排重接口处理未通过：reques：{}，result:{}",queryString,result);

			if (StringUtil.isNotBlank(idfa)) {
				//返回未通过的响应
				String[] idfaArray = idfa.split(",");
				Map<String, Integer> idfaResult = new LinkedHashMap<>(idfaArray.length);
				for (String fa : idfaArray) {
					idfaResult.put(fa, 1);
				}

				result = JSONObject.fromObject(idfaResult).toString();
			}
		 }

       // String response = String.format("{\"code\": %d,\"result\": %s}", code, result.charAt(0) == '{' ? result : JsonUtils.jsonQuote(result));
        logger.info("disctinct_new:result:{}", result);

        return result;
    }
    

    /**
     * 校验第三方传过来的 
     * @param idfa
     * @param adid
     * @param channel
     * @return
     */
    private final boolean check(String idfa,int adid,int channel,String key,String serct){
    	if(isExceptedChannel(channel)){
    		return true;
    	}
    	if(StringUtils.isBlank(serct)){
    		return false;
    	}
    	if(channel <= 0){
    		return false;
    	}

		//唱吧钱包不带分隔符
		String separator = "|";
		if (channel == 48703){
			separator = "";
		}

    	StringBuilder sb = new StringBuilder(100);
    	if(idfa != null){
    		sb.append(idfa).append(separator);
    	}
		if (adid > 0){
			sb.append(adid).append(separator);
		}
    	sb.append(channel).append(separator).append(key);
    	String md5 = MD5Util.getMD5(sb.toString());
    	return (md5.equals(serct));
    }


	/**
	 * 不需要验证签名的渠道
	 * @param channel
	 * @return
	 */
	private boolean isExceptedChannel(int channel){
		return channel == 11029 || channel==22658 || channel==68725 || channel==67170 ||channel == 79817;
	}
	
	private String executeClick(HttpServletRequest request){
		String query = RequestUtil.getQueryString(request);
    	String channelIp = RequestUtil.getIpAddr(request);
		logger.info("onClick request " + request.getRequestURL() + "?" + query); 
		String idfa = request.getParameter("idfa");
        int adid = ServletRequestUtils.getIntParameter(request, "adid", 0);
        int channelId = ServletRequestUtils.getIntParameter(request,"channel",0);
        String secret = request.getParameter("sign");
        String ip = request.getParameter("ip");
        String os = request.getParameter("os");
        String result = checkParameters(idfa,adid,channelId,ip,secret);
        int code = 0;
        int appId = (adid - 997) / 11;
        
        if (result == null){
            App app = appTaskService.getApp(appId);
            if(app == null) {
            	 result = "app not exist";
            } else {
				App twinApp = appTaskService.getTwinApp(app.getId());
				AppTask appTask = appTaskService.getTaskByAppIdAndChannelId(appId, channelId);
				
				int twinAppId = twinApp == null? 0 : twinApp.getId();
            	boolean isReviced = userTaskNotifyService.isReceivedByIdfa(idfa, appId, twinAppId, channelId);
            	if(isReviced) { //接过任务
            		result = "idfa is received";

					//判断是否由本渠道接过，如果是的话，一段时间内可以重复点击
					UserTaskNotify taskNotify = userTaskNotifyService.getByIdfaAndAppId(idfa, appId);
					if (taskNotify != null && taskNotify.getChannel() == channelId && taskNotify.getStatus() == 1 && taskNotify.getReward() == 0 ){
						logger.warn("executeClick, channel reclick task, utn:{}", taskNotify);
						if(appTask != null && appTask.isValid() && taskNotify.isVilid()){
							result = null;
						}
					}
					
            	} else {
            		if(appTask ==  null) {
            			result = "app task not exist";
            		}
					else if(appTask.getReceived_task() >= (appTask.getCurrent_task() * 2 )) { //由第三方控制数量,最多不能超2倍
            			result = "app task exceed the max number";
            		}
            		//这里需要修改 去哪儿攻略比较特殊 有厂商 但是不回调
            		else if(appTask.isVendorTask()) {	//有厂商
                		ClientInfo clientInfo = new ClientInfo();
                		clientInfo.setIdfa(idfa);
                		clientInfo.setIpAddress(ip);
                		clientInfo.setOs(os);
                		if(appVendorService.onClick(app, appTask, clientInfo) && saveTask(request, appId, appTask, idfa, channelId) ) {
                		} else {
                			result = "idfa is received";
                		}
    	            } else if(!saveTask(request, appId, appTask, idfa, channelId)) {
    	            	result = "app task click error";
    	            }
            	}
            }
        }

        if (result == null){
            code = 0;
            result = "ok";
        }else {
            code = 400;
        }

        String response = String.format("{\"code\": %d,\"result\": %s}", code, JsonUtils.jsonQuote(result));
        logger.info("点击ip:{},query:{},onClick:result:{}",channelIp,query,response);
        return response;
	}
}
