package com.cyhd.web.action.web;


import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.cyhd.common.util.JsonUtils;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppTaskChannel;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.dao.po.UserTaskDianru;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.ChannelService;
import com.cyhd.service.impl.UserService;
import com.cyhd.service.impl.UserTaskCalculateService;
import com.cyhd.service.impl.UserTaskReportPersistService;
import com.cyhd.service.impl.UserTaskService;
import com.cyhd.service.impl.UserTaskThirdChannelService;
import com.cyhd.service.util.DianruInterfaceUtil;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.exception.ErrorCode;

@Controller
@RequestMapping("/www/third")
public class ThirdTaskAction extends BaseAction {

	@Resource
	private UserTaskThirdChannelService userTaskThirdChannelService; 
	@Resource
	private UserService userService;
	@Resource
	private ChannelService channelService;
	@Resource
	private UserTaskService userTaskService;
	@Resource
	private UserTaskCalculateService userTaskCalculateService;
	@Resource
	private AppTaskService appTaskService;
	@Resource
	private UserTaskReportPersistService userTaskReportPersistService;

	private static final String prefix = "/www/third/";
 
	private static Logger log = LoggerFactory.getLogger("third");
	
	/**
	 * 点入回调
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/dianru.3w")
	public ModelAndView dianru(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("hashid", ServletRequestUtils.getStringParameter(request, "hashid"));
		map.put("appid", ServletRequestUtils.getStringParameter(request, "appid"));
		map.put("adid", ServletRequestUtils.getStringParameter(request, "adid"));
		map.put("adname", ServletRequestUtils.getStringParameter(request, "adname"));
		map.put("userid", ServletRequestUtils.getStringParameter(request, "userid"));
		map.put("deviceid", ServletRequestUtils.getStringParameter(request, "deviceid"));
		map.put("source", ServletRequestUtils.getStringParameter(request, "source"));
		map.put("point", ServletRequestUtils.getStringParameter(request, "point"));
		map.put("time", ServletRequestUtils.getStringParameter(request, "time"));
		map.put("checksum", ServletRequestUtils.getStringParameter(request, "checksum"));
		Map<String, String[]> params = request.getParameterMap();  
        String queryString = "";  
        for (String key : params.keySet()) {  
            String[] values = params.get(key);  
            for (int i = 0; i < values.length; i++) {  
                String value = values[i];  
                queryString += key + "=" + value + "&";  
            }  
        }  
        // 去掉最后一个空格  
        queryString = queryString.substring(0, queryString.length() - 1);   
		log.info("dianru notify request params " + request.getRequestURL() + "?" + queryString); 
		DianruInterfaceUtil util = new DianruInterfaceUtil();
		
		//验证密钥
		if(util.validateSign(map)) {	
			if(map.get("userid").isEmpty()) {
				log.info("dianru notify request validate userid:{} is empty" , map.get("userid"));
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "userid为空");
				return mv;
			}
			User user = userService.getUserByIdentifyId(Integer.parseInt(map.get("userid")));
			if(user == null) {
				log.info("dianru request validate userid:{} is empty" , map.get("userid"));
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "用户不存在");
				return mv;
			}
			int appId = Integer.parseInt(map.get("appid"));
			int adid = Integer.parseInt(map.get("adid"));
			if(appId <=0 ) {
				log.info("dianru request validate appid:{} is empty" , appId);
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "appId参数不正确");
				return mv;
			}
			if(adid <=0 ) {
				log.info("dianru request validate adid:{} is empty" , adid);
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "adid参数不正确");
				return mv;
			}
			
			AppTaskChannel appTaskChannel = channelService.getAppTaskChannelByAdid(adid);
			if(appTaskChannel ==null) {
				log.info("dianru request validate adid:{} return AppTaskChannel is empty" , adid);
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, map.get("adname")+"任务渠道不存在");
				return mv;
			}
			int taskId = appTaskChannel.getTask_id();
			UserTask userTask = userTaskService.getUserTask(user.getId(), taskId);
			if(userTask == null) {
				log.info("dianru request user userId:{},taskId:{} return userTask is empty" , user.getId(), taskId);
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, map.get("adname")+"任务不存在");
				return mv;
			}
			if(userTask.getConfirm_finish() > 0) {
				log.info("dianru request user userId:{},taskId:{} is finish" , user.getId(), taskId);
				this.fillStatus(mv);
				return mv;
			}
			
			//保存回调信息
			UserTaskDianru dianru = new UserTaskDianru();
			dianru.setUser_task_id(userTask.getId());
			dianru.setHashid(map.get("hashid"));
			dianru.setAppid(appId);
			dianru.setAdid(adid);
			dianru.setAdname(map.get("adname"));
			dianru.setUserid(map.get("userid"));
			dianru.setDeviceid(map.get("deviceid"));
			dianru.setSource(map.get("source"));
			dianru.setPoint(Integer.parseInt(map.get("point")));
			dianru.setTime(new Date(Long.parseLong(map.get("time")) * 1000));
			if(userTaskThirdChannelService.addUserTaskDianru(dianru)) {
				App app = appTaskService.getApp(userTask.getApp_id());
				AppTask appTask = appTaskService.getAppTask(userTask.getTask_id());
				
				userTaskCalculateService.onConfirmFinishTask(user, app, appTask, userTask);
				userTaskReportPersistService.remove(userTask.getId());
				
				this.fillStatus(mv);
			} else {
				log.info("dianru request UserTaskDianru save fail");
				this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "保存失败");
			}
		} else {
			log.info("dianru request validate hashid:{},adname:{} fail:checksum验证失败" , map.get("hashid"), map.get("adname"));
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "checksum验证失败");
		}
		return mv;
	}
	
	/**
	 * 回调处理
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping(value = "/callback.3w")
	public ModelAndView callback(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "result.json.ftl");
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		map.put("key", ServletRequestUtils.getStringParameter(request, "key"));
		map.put("userids", ServletRequestUtils.getStringParameter(request, "userids"));
		map.put("appid", ServletRequestUtils.getStringParameter(request, "appid"));
		map.put("key", ServletRequestUtils.getStringParameter(request, "key"));
		Map<String, String[]> params = request.getParameterMap();  
        String queryString = "";  
        for (String key : params.keySet()) {  
            String[] values = params.get(key);  
            for (int i = 0; i < values.length; i++) {  
                String value = values[i];  
                queryString += key + "=" + value + "&";  
            }  
        }  
        // 去掉最后一个空格  
        queryString = queryString.substring(0, queryString.length() - 1);
		log.info("dianru notify request params " + request.getRequestURL() + "?" + queryString); 
		
		String key =  ServletRequestUtils.getStringParameter(request, "key");
		 
		//验证密钥
		if(key.equals("callback")) {	
			String[] userIds = map.get("userids").split(",");
			int appid = Integer.parseInt(map.get("appid"));
			int count = 0;
			String userFinsh="";
			if(userIds.length > 0) {
				for (String u : userIds) {
				 	User user = userService.getUserById(Integer.parseInt(u));
					if(user == null) {
						log.info("request validate userid:{} is empty" , u);
						continue;
					} 
					if(appid <=0 ) {
						log.info("request validate userid{} appid:{} is empty" , u, appid); 
						continue;
					} 
					 
					UserTask userTask = userTaskService.getUserTaskByAppId(user.getId(), appid);
					if(userTask == null) {
						log.info("request userId:{}, appid:{} return userTask is empty" , u, appid);
						continue;
					}
					if(userTask.isConfirmFinish() || userTask.getReward() > 0) {
						log.info("request user userId:{},taskId:{} is finish" , u, userTask.getId());
						continue;
					}
					 
					App app = appTaskService.getApp(userTask.getApp_id());
					AppTask appTask = appTaskService.getAppTask(userTask.getTask_id());
					
					userTaskCalculateService.onConfirmFinishTask(user, app, appTask, userTask);
					userTaskReportPersistService.remove(userTask.getId());
					
					userFinsh = userFinsh + ","+ u;
					count ++;
		        }  
			}
			log.info("request user finsh userId:{},appid:{} finish count:{}" ,userFinsh, appid,count);
			this.fillStatus(mv);
		} else {
			log.info("验证失败");
			this.fillErrorStatus(mv, ErrorCode.ERROR_CODE_PARAMETER, "验证失败");
		}
		return mv;
	}
	/**
	 * 上级代理的回调
	 * @param request
	 * @param response
	 * @return
	 */
	 @RequestMapping(value = "/callback_pre.3w", produces = "text/json; charset=UTF-8")
	 @ResponseBody
	 public String callbackByDefault(HttpServletRequest request,HttpServletResponse response){
		CallBackBean bean = executeCallBackForPreChannel(request);
		int code = bean.isOK() ? 200 : 400;
		return String.format("{\"code\": %d,\"result\": %s}", code, JsonUtils.jsonQuote(bean.result));
	}
	
	 /**
		 * 上级代理的回调 code为0的
		 * @param request
		 * @param response
		 * @return
		 */
		 @RequestMapping(value = "/callback_zero.3w", produces = "text/json; charset=UTF-8")
		 @ResponseBody
		 public String callbackByZeroCode(HttpServletRequest request,HttpServletResponse response){
			CallBackBean bean = executeCallBackForPreChannel(request);
			int code = bean.isOK() ? 0 : 400;
			return String.format("{\"code\": %d,\"result\": %s}", code, JsonUtils.jsonQuote(bean.result));
		} 
	 /**
	 * 上级代理的回调
	 * @param request
	 * @param response
	 * @return
	 */
	 @RequestMapping(value = "/callback_pre_yq.3w", produces = "text/json; charset=UTF-8")
	 @ResponseBody
	 public String callbackByYouQian(HttpServletRequest request,HttpServletResponse response){
		log.info("上游渠道-友钱回调");
		CallBackBean bean = this.executeCallBackForPreChannel(request);
		int code = bean.isOK() ? 200 : 400;
		String result = JsonUtils.jsonQuote(bean.result);
		return String.format("{\"error\":%s,\"code\": %d,\"result\": %s}", result, code, result);
	}
	 // {"success":true,"message":"ok"} 
	 /***
	  * 上级回调 返回的值 是boolean的
	  * @param request
	  * @param response
	  * @return
	  */
	 @RequestMapping(value = "/callback_bool.3w", produces = "text/json; charset=UTF-8")
	 @ResponseBody
	 public String callbackReturnBool(HttpServletRequest request,HttpServletResponse response){
		 CallBackBean bean = this.executeCallBackForPreChannel(request);
		 //{“message”:”返回结果”,”success”:”false/ture”} 
		return String.format("{\"message\": \"%s\",\"success\": %b}", bean.result, bean.isOK());
	 }
	 
	 //http://third.miaozhuandaqian.com/www/third/callback_pre_jyq.3w
	 @RequestMapping(value = "/callback_pre_jyq.3w", produces = "text/json; charset=UTF-8")
	 @ResponseBody
	 public String callbackByJuYouQian(HttpServletRequest request,HttpServletResponse response){
		log.info("上游渠道-聚有钱回调");
		CallBackBean bean = this.executeCallBackForPreChannel(request);
		int code = bean.isOK() ? 200 : 400;
		//String result = JsonUtils.jsonQuote(bean.result);
		String resp =  String.format("{\"success\":\"%s\",\"message\": \"%s\",\"httpstatus\": \"%d\"}", bean.isOK(), bean.result, code);
		logger.info("上游渠道-聚有钱回调 ,response:{}",resp);
		return resp;
	 }
	 
	private CallBackBean executeCallBackForPreChannel(HttpServletRequest request){
		StringBuilder sb = new StringBuilder(320);
		sb.append("上级渠道回调:ip:").append(RequestUtil.getIpAddr(request)).append(",");
		sb.append(",query:").append(RequestUtil.getQueryString(request));
		String _data = request.getParameter("_data");
		String srcData = channelService.getCallBackData(_data);
		int code = 400;
		String result="";
		if(srcData == null){
			sb.append(",解密出来的数据为null");
			result = "缺少关键数据";
		}else{
			//${渠道Id}|${用户标识}|${app任务的id}|${idfa}
			try{
				String[] datas = srcData.split("\\|");
				int channelId = Integer.parseInt(datas[0]);
				int userIdentify = Integer.parseInt(datas[1]);
				int appId = Integer.parseInt(datas[2]);
				String idfa = datas[3];
				
				sb.append(",channel:").append(channelId);
				sb.append(",userIdentify:").append(userIdentify);
				sb.append(",appId:").append(appId);
				sb.append(",idfa:").append(idfa);
				
				App app = appTaskService.getApp(appId);
				//AppChannel channel = channelService.getAppChannel(channelId);
				User user = userService.getUserByIdentifyId(userIdentify);
				UserTask userTask = userTaskService.getUserTaskByAppId(user.getId(), app.getId());
				AppTask appTask = appTaskService.getAppTask(userTask.getTask_id());
				AppTaskChannelVo vo = channelService.getAppTaskChannel(appTask.getId());
				if(vo == null || vo.getAppChannel().getId() != channelId){
					sb.append(",渠道错误：");
					result = "渠道不匹配";
				}else if(userTask.getIdfa().equals(idfa) == false){
					sb.append(",任务重的idfa不匹配").append("任务中idfa:").append(userTask.getIdfa()).append(",闯过来的idfa:").append(idfa);
					result = "idfa不匹配";
				}else{
					userTaskCalculateService.onConfirmFinishTask(user, app, appTask, userTask);
					code = 0;
					result = "ok";
				}
			}catch(Exception e){
				log.error("渠道回调的任务中出现错误,request:{}:cause by:{}",sb.toString(),e.getMessage());
				result = "不存在的任务";
			}
		}
		CallBackBean bean = new CallBackBean(code, result);
		sb.append(bean.toString());
		log.info(sb.toString());
		return bean;
	}
	private static class CallBackBean{
		public int code;
		public String result;
		public CallBackBean(int code, String result) {
			this.code = code;
			this.result = result;
		}
		public boolean isOK(){
			return code == 0;
		}
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append( ",code=" ).append( code ).append( ", result=" ).append( result);
			return sb.toString();
		}
	}
	public static void main(String[] args) {
		String str = "q|3|s";
		String[] arry = str.split("\\|");
		System.out.println(Arrays.toString(arry));
		System.out.println(String.format("{\"message\": \"%s\",\"success\": %b}","hello",true));
	}
}