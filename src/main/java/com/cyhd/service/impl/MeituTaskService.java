package com.cyhd.service.impl;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IdEncoder;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.util.RequestSignUtil;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.web.common.ClientInfo;

/**
 * 美图任务服务<br/>
 * 调用接任务时，点击美图的点击接口，校验是否可以接任务<br/>
 * 启动独立线程，定时（每分钟）从美图后台拉取数据，获取用户任务完成情况
 * @author hy
 *
 */
@Service
public class MeituTaskService {
	
	private static Logger servicelog = LoggerFactory.getLogger("third");


	//默认的AppSecret
    private static final String TEST_APP_SECRET = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAI0iL6WVMz+VIZfgYLKOUPFZwOiVCZ+Oxc6PjgWz2k4REAFNqylL/+7SnJYt0MF2s8XL7t+W7/qKhFH3zAIL0szwdXvEDB04/AVU6y2X6qx2IZ2J4C4t6n8b5N3g8W5/JNtmm6zHh6kw/YM5RR9vR3SBZV5r4ggbhRugaTJYA34BAgMBAAECgYAvkzKUkrLv4Amu9Mgj6K2IbkUFIhhYXPx5IRMzAOm6Hy5SAiiMhz4C96QpS9BvJuB68L/ZRzwmLMNmDi4LEolAYrgikCFON364/kZZF1BnLnktagMd/3mT23kaXeC52pnXFx5dDrVu8Bok3AyFlf1UBVo1T4Lb2tjxdnmIP6eUAQJBAN12u3URfDHYY+bd0zKiQeD04RZKsiq/nA38koPSB0EIEjR7FRJtX/4cQXwQfAZ+zMoQ3zkw7CsMaE19eMYoRTECQQCjJINOhFvcq5MAwz6E4JssF2prJEsskH9W/iaqQyS8YLfBqVPtzuYshGdCLCMUNGY8kK1AaPJvU52XfIrJhtHRAkBdshEHK3me4Q0LLMhgwLMciJ3+P2X3ng9Y/4XBTYeSJOcG2xgELtARA0VVRugiG11rFA5M9PzGDb7HIhGJzJnRAkBaMrPOU2ueo9XQ1CHawXvJcuDJf/V4HCPravThqeHDrQ2rqvzWPFASSNn2QgTbBOWJksvXEq8HUgmNWbQ6G6ohAkEAxHU3QIcAkCK74LBT7nr4fawJn6T7bFgc02zXEfX3GiU8d7t6XKUpEyRqSi4jN8gGcXhzyLoFIhvJTybg+0XP9Q==";

    private static final String ONLINE_APP_SECRET = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJ1UsZOzxmmSG8mu9huPa7KJdfpJUeTEfqfkTqKIdre1d8XbiaGgu0vDJWfZAzb38luB1QKn2w17GFYsGTB2KJedJc5DmUycB23eIsbq0bwF4lFNHWnHY+kx9ElfEHVBhUuBB02cDv8ugF2Owcj01q0lKXWALuQ4tosSPTVzV4tJAgMBAAECgYEAlSKSTatM+geo1Y5G9isGcK/CqKTVvx/P24AcVg87UbrvtRr8pLxCrndmPsFEPdfc4Yb5jtHkYCv+Drkwi3KF2GgE669ODeKsdmIuP8IgndXFJ0HsHvfo4WX4r3QREMo9sJT+wr3OoGh7oUAUbaZNKv9kJzEtxWtycZaTp04OIoECQQDtyDPDJsh6cJ/ouUjCF+ixIhyTdLFZif7MyuU9ZOkn7a2xY8HH4lD7QBLHA3AC9crsqWaU5isSO3jiNeqewoeRAkEAqWKKoaNF+SuXikJKlcMgXm9TZoxNrqs3fzvnhqyeiRFUsBJZUS7vBmMgduCOSD5+9qPbnf5RlpGREIkwwTucOQJBAN6MFDZAZD6EjoXodHqEmhq/THOOMjcVes79zxRpD/d48qomLcYAwb6GN4zgYMPEfIqH+iS+T+2ekANYZyDz/KECQDckTEs4kvP92/R3hj5g6m2zwivVKwjc1lFGWCYAlg+7I526K3eBVvD2XkA09DzSk5SJXVp+y2K/+sCuWL/fPwkCQA0gFuYeP3VlmTYbL8hOjWVv1Xfh231gwYeACQDnMNeq6BvbfFKc4hY8oDn95tO7NExtyyWugDWgkOvpM3IkRRw=";


    //最长的空闲时间，超过这个时间则停止后台取数据
	private static final long MAX_IDLE_TIME = 10 * 60 * 1000;
	
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
	private UserInstalledAppService userInstalledAppService;

	//获取美图数据的后台线程
	private ExecutorService executor;
	
	//上次获取数据的时间
	private volatile long lastTimestamp;

	//上次获取美图任务的时间
	private volatile long lastTaskCheckTime;



	//美图的所有app任务，理论上只有一个，可以支持多个
	private Map<Integer,AppTaskChannelVo> meituTaskMap;


	@PostConstruct
	public void startMeituJob(){
		//如果是允许Job的服务器，才允许启动任务
		if (GlobalConfig.runJob){
			checkMeituTasks();

			//无论接任务是否成功，都启动取数据的任务
			startFetchData();
		}
	}

	/**
	 * 美图点击请求，检查用户是否能够接任务
	 * @param user
	 * @param taskId
	 * @param appId
	 * @param vo
	 * @param clientInfo
	 * @return
	 */
	public boolean isAllowReviceTask(User user, int taskId, int appId,
			AppTaskChannelVo vo, ClientInfo clientInfo) {
		String url = vo.getAppChannel().getClick_url();
		String idfa = clientInfo.getIdfa();
		String ip = clientInfo.getIpAddress();

		String appkey = getMeituAppKey(vo);
		String appSecret = getMeituSecret(vo);


		//custom info
		HashMap<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("idfa", idfa);
		infoMap.put("client_ip", ip);
		infoMap.put("user_id", "" + user.getUser_identity());
		infoMap.put("task_id", "" + IdEncoder.encode(taskId));
		infoMap.put("app_id", appId+"");
		
		String customeInfo = JSONObject.fromObject(infoMap).toString();
		
		//请求参数
		Map<String, String> params = new HashMap<String,String>();
		params.put("appkey", appkey);
		params.put("channel", "ASO");
		params.put("click_id", "");
		params.put("page_id", "");
		params.put("pos_id", "");
		params.put("page_mobile_info", "");
		params.put("custom_info", customeInfo);
		
		String sign = RequestSignUtil.signRequestUsingRSA(params, appSecret);
		params.put("sig", sign);

		boolean isAllowd = false;
		try {
			String response = HttpUtil.postByForm(url, params);

			servicelog.debug("meitu click request: params:{}, response:{}", params, response);

			JSONObject json = JSONObject.fromObject(response);
			if (json != null) {
				if (json.optInt("status_code") == 200) {
					servicelog.info("Meitu task click ok, user={}, taskId={}", user.getUser_identity(), taskId);

					isAllowd = true;
				}else{
					App app = appTaskService.getApp(appId);
					if (app != null) {
						userInstalledAppService.insert(user.getId(), appId, clientInfo.getDid(), app.getAgreement());
					}
				}
			} else {
				servicelog.warn("Meitu Task service return:{}, for user:{}, taskId:{}, appId:{}", response, user.getId(), taskId, appId);
			}

		} catch (Exception e) {
			servicelog.error("Meitu click error", e);
		}
		
		return isAllowd;
	}

	/**
	 * 根据当前所有运行的任务获取美图的app channel
	 */
	protected  void checkMeituTasks(){
		//获取当前的美图任务
		Map<Integer, AppTaskChannelVo> taskMap = new HashMap<>();
		List<AppTask> taskList = appTaskService.getValidTasks();

		taskList.stream().filter(task -> task.getIschannel() != 0).forEach(task -> {
			AppTaskChannelVo channelVo = channelService.getAppTaskChannel(task.getId());
			if (channelVo != null && channelVo.getAppChannel() != null && channelVo.getAppChannel().getId() == AppChannel.CHANNEL_MEITU) {
				taskMap.put(task.getId(), channelVo);
			}
		});

		meituTaskMap = taskMap;
		lastTaskCheckTime = System.currentTimeMillis();

		servicelog.info("current meitu task map:{}", meituTaskMap);
	}

	/**
	 * 获取当前所有进行中的美图数据
	 * @param startTime
	 * @param endTime
	 */
	public void getMeituData(long startTime, long endTime){
		//所有进行中的美图task
		if(meituTaskMap != null && meituTaskMap.size() > 0) {
			servicelog.info("get meitu data, tasks:{}, startTime:{}, endTime:{}", meituTaskMap.keySet(), startTime, endTime);
			for (AppTaskChannelVo channelVo : meituTaskMap.values()) {
				getMeituAppData(channelVo, startTime, endTime);
			}
		}else {
			servicelog.info("get meitu data, task map is empty");

			//如果为空，则再取一次任务数据
			checkMeituTasks();
		}
	}
	
	
	/**
	 * 从美图服务器获取用户数据<br/>
	 * 由于可能有多个appkey，所以需要多次请求
	 * @param startTime
	 * @param endTime
	 */
	public void getMeituAppData(AppTaskChannelVo channelVo, long startTime, long endTime){
		String appkey = getMeituAppKey(channelVo);
		String appSecret = getMeituSecret(channelVo);
		String queryUrl = channelVo.getAppChannel().getQuery_url();
		
		Map<String, String> params = new HashMap<String, String>();
		params.put("appkey", appkey);
		params.put("channel", "ASO");
		params.put("start_time", "" + startTime);
		params.put("end_time", "" + endTime);
		params.put("timestamp", "" + System.currentTimeMillis() );

		String sign = RequestSignUtil.signRequestUsingRSA(params, appSecret);
		params.put("sig", sign);
		
		try {
			String response = HttpUtil.postByForm(queryUrl, params);

			servicelog.debug("meitu data request: params:{}, response:{}", params, response);

			JSONObject result = JSONObject.fromObject(response);
			
			//获取到正确的返回数据
			if(result != null && appkey.equals(result.getString("appkey"))){
				JSONArray dataList = result.getJSONArray("data");
				dealMeituData(dataList);
			}else{
				servicelog.warn("invalid meitu server data:{}", response);
			}
			
		} catch (Exception e) {
			servicelog.error("get meitu data error", e);
		}
	}
	
	
	/**
	 * 处理美拍服务器列表数据
	 * @param dataList
	 */
	protected void dealMeituData(JSONArray dataList){
		if(dataList != null && dataList.size() > 0){
            servicelog.info("deal Meitu server data, total size:{}", dataList.size());
			
			for(int i = 0, len = dataList.size(); i < len; ++ i){
				JSONObject data = dataList.getJSONObject(i);
				String customInfo = data.getString("custom_info");
				JSONObject customData = JSONObject.fromObject(customInfo);
				dealMeituCustomData(customData);
			}

		}else{
			servicelog.warn("empty meitu server data");
		}
	}
	
	/**
	 * 处理美图服务器数据中的custom info
	 * @param customData
	 */
	protected void dealMeituCustomData(JSONObject customData){
		int uid = NumberUtils.toInt(customData.getString("user_id"), 0);
		String taskIdStr = customData.getString("task_id");

        Integer taskIdInteger = IdEncoder.decode(taskIdStr);
        int taskId = taskIdInteger == null? 0 : taskIdInteger.intValue();
		
		//合法的请求
		if(uid > 0 && taskId > 0){
			//处理用户的任务状态
			UserTask ut = null;

			User user = userService.getUserByIdentifyId(uid);
			if (user != null) {
				ut = userTaskService.getUserTask(user.getId(), taskId);
			}

			if (ut != null && ut.getConfirm_finish() == 0) {
				//设置第三方已确认用户任务完成
				AppTask appTask = appTaskService.getAppTask(taskId);
				App app = appTaskService.getApp(ut.getApp_id());

				userTaskCalculateService.onConfirmFinishTask(user, app, appTask, ut);
				servicelog.info("Meitu custom data OK, uid={}, taskId={}", uid, taskId);
			}else{
				servicelog.warn("Meitu custom data, user task is not valid or confirmed, uid={}, taskId={}, data={}", uid, taskId, customData.toString());
			}
		}else{
			servicelog.warn("invalid Meitu task data, uid={}, taskId=({}, {}), data:{}", uid, taskIdStr, taskId, customData.toString());
		}
	}
	
	
	
	/**
	 * 启动获取后台数据的任务<br/>
	 * 有isAllowReviceTask任务调用的时候，则开启取数据的任务<br/>
	 * 如果超过MAX_IDLE_TIME时间未取到过用户的任务数据，则停止取数据的线程，只到再次调用isAllowReviceTask启动任务<br/>
	 * 
	 */
	protected void startFetchData(){
		//如果当前没有任务在运行
		if(executor == null ){
			//新开始任务，只取当前这段时间的数据
			long meituStartTime = NumberUtil.safeParseLong(System.getProperty("meitu_start_time"));
			lastTimestamp = meituStartTime > 0 ? meituStartTime : System.currentTimeMillis();

			//执行任务
			executor = Executors.newSingleThreadExecutor();
			executor.execute(fetchDataJob);
		}
	}

	/**
	 * 获取美图的app key
	 * @param vo
	 * @return
	 */
	protected String getMeituAppKey(AppTaskChannelVo vo){
		String appkey = vo.getAppChannel().getChannel_id();
		if (vo.getAppTaskChannel().getThird_app_key() != null){
			appkey = vo.getAppTaskChannel().getThird_app_key();
		}
		return appkey;
	}


    /**
     * 获取美图app secret
     * @return
     */
    protected String getMeituSecret(AppTaskChannelVo channelVo){
        String appSecret = TEST_APP_SECRET;
		if (GlobalConfig.isDeploy){
            appSecret = ONLINE_APP_SECRET;
        }

		if (channelVo.getAppTaskChannel().getThird_app_secret() != null){
			appSecret = channelVo.getAppTaskChannel().getThird_app_secret();
		}
		return appSecret;
    }
	
	/**
	 * 获取美图数据的任务
	 */
	private Runnable fetchDataJob = new Runnable(){

		@Override
		public void run() {

			//如果在超时时间之内，则继续循环，否则循环退出，任务结束
			while(true){
				//每分钟取一次数据
				try {
					TimeUnit.MINUTES.sleep(1);

					//设置取数据的时间间隔
					long currentTimestamp = System.currentTimeMillis();
					getMeituData(lastTimestamp, currentTimestamp);
					lastTimestamp = currentTimestamp;

					//超过间隔之后，重新获取美图任务信息
					if (currentTimestamp - lastTaskCheckTime > MAX_IDLE_TIME){
						checkMeituTasks();
					}

				} catch (Exception e) {
					servicelog.error("meitu executor thread exception", e);
				}
			}
		}
	};
}
