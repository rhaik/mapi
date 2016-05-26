//package com.cyhd.service.channelQuickTask;
//
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.ThreadFactory;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.Resource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import com.cyhd.common.util.DateUtil;
//import com.cyhd.common.util.HttpUtil;
//import com.cyhd.common.util.ItunesUtil;
//import com.cyhd.common.util.StringUtil;
//import com.cyhd.service.constants.Constants;
//import com.cyhd.service.dao.po.App;
//import com.cyhd.service.dao.po.AppChannel;
//import com.cyhd.service.dao.po.AppTask;
//import com.cyhd.service.dao.po.User;
//import com.cyhd.service.dao.po.UserTask;
//import com.cyhd.service.impl.UserInstalledAppService;
//import com.cyhd.service.vo.AppTaskChannelVo;
//import com.cyhd.service.vo.UserTaskVo;
//import com.cyhd.web.common.ClientInfo;
//
//import net.sf.json.JSONArray;
//import net.sf.json.JSONObject;
//
////@Service("dianJoyQuickTaskService")
//public class DianJoyQuickTaskService extends IQuickTaskService{
//	
//	private String app_id = null;
//	
//	private ExecutorService assemblyES = null;
//	
//	private static Logger servicelog = LoggerFactory.getLogger("third");
//	
//	@Resource
//	private UserInstalledAppService userInstalledAppService;
//	
//	@Override
//	public int getAppChannelID() {
//		return AppChannel.CHANNEL_QUICK_DIANJOY;
//	}
//	
//	@PostConstruct
//	private void initTheadPool(){
//		ThreadFactory factory = new ThreadFactory() {
//			@Override
//			public Thread newThread(Runnable r) {
//				Thread t  = new Thread(r);
//				t.setName("assemply_dianjoy_app.thread");
//				return t;
//			}
//		};
//		assemblyES = Executors.newFixedThreadPool(4, factory);
//	}
//	
//	
//	@Override
//	public void filterAllTaskApp(List<UserTaskVo> data,List<App> filter){
//		if(data == null || data.isEmpty()
//				||filter == null || filter.isEmpty()){
//			return ;
//		}
//		
//		Iterator<UserTaskVo> it = data.iterator();
//		while (it.hasNext()) {
//			if(filter.contains(it.next().getApp())){
//				it.remove();
//			}
//		}
//	}
//	
//	@Override
//	public List<UserTaskVo> getAllTaskApp(ClientInfo clientInfo,User user,Map<String, String> parame){
//		Map<String, String> parametres = new HashMap<String, String>();
//		parametres.put("key", parame.get("key"));
//		parametres.put("data", parame.get("data"));
//		parametres.put("e_version", "1");
//		
//		String queryURI= "http://dd.dianjoy.com/dev/api/chicken/adlist.php?user_ip="+clientInfo.getIpAddress()+"&ua="+parame.get("ua");
//		String response = null;
//		try {
//			servicelog.info("请求点乐的开墙接口,request:{}",queryURI);
//			//response = HttpUtil.postByForm(queryURI, parametres);
//			//TODO change
//			response = GetDataByFile.readFile();
//			servicelog.info("请求点乐开墙响应,response:{}",response);
//			return assemblyApps(response,user);
//		} catch (Exception e) {
//			servicelog.error("请求点乐开墙异常:request:{},cause by:{}",queryURI,e);
//		}
//		return new ArrayList<>(0);
//	}
//	
//	@Override
//	public boolean click(ClientInfo clientInfo,User user,App app,AppTask appTask,AppTaskChannelVo vo,Map<String, String> parame){
//		String requestURL = null;
//		try {
//			StringBuilder sb = new StringBuilder(640);
//			sb.append("dd.dianjoy.com/dev/api/chicken/down.php");
//			sb.append("?device_id=").append(clientInfo.getIdfa());
//			//sb.append("&ad_id=").append(appTask.getOuter_id());
//			sb.append("&idfa=").append(clientInfo.getIdfa());
//			sb.append("&device_name=").append(URLEncoder.encode(clientInfo.getModel(), "utf-8"));
//			sb.append("&os_type=").append("ios");
//			sb.append("&os_version=").append(clientInfo.getOSVersion());
//			//TODO 修改
//			sb.append("&app_id=").append(app_id);
//			sb.append("&snuid=").append(user.getUser_identity());
//			sb.append("&user_ip=").append(clientInfo.getIpAddress());
//			sb.append("&ua=").append(parame.get("ua"));
//			
//			requestURL = sb.toString();
//			servicelog.info("请求点乐_快速点击接口,request:{}",requestURL);
//			Map<String,String> response = HttpUtil.getHttpStatus(requestURL, null);
//			servicelog.info("请求点乐_快速点击接口响应,response:{}",response);
//			String redirect = response.get("redirect");
//			if(StringUtil.isNotBlank(redirect)){
//				if(redirect.startsWith("https://itunes.apple.com")||redirect.startsWith("http://itunes.apple.com")){
//					return true;
//				}
//			}
//		} catch (Exception e) {
//			servicelog.error("请求点乐_快速,request:{},cause by:{}",requestURL,e);
//		}
//		return false;
//	}
//	
//	private List<UserTaskVo> assemblyApps(String data,User user){
//		if(StringUtil.isBlank(data)){
//			return new ArrayList<>(0);
//		}
//		
//		JSONObject json = JSONObject.fromObject(data);
//		if(json == null){
//			return new ArrayList<>(0);
//		}
//		int total = json.optInt("total");
//		List<UserTaskVo> rtv = new ArrayList<UserTaskVo>(total);
//		JSONArray offers = json.getJSONArray("offers");
//		
//		List<Callable<Boolean>> taskList = new ArrayList<>(total);
//		for(int i = 0; i < offers.size(); i++){
//			final int j = i;
//			taskList.add(new Callable<Boolean>() {
//				UserTaskVo tmp = null;
//				@Override
//				public Boolean call() {
//					try{
//						tmp = assemblyApp(offers.getJSONObject(j),user);
//						if(tmp != null){
//							rtv.add(tmp);
//						}
//					}catch(Exception e){
//						e.printStackTrace();
//					}
//					return true;
//				}
//			});
//		}
//		try {
//			List<Future<Boolean>> futures= assemblyES.invokeAll(taskList);
//			for(Future<Boolean> future:futures){
//				future.isDone();//阻塞 等待任务完成 
//			}
//		} catch (InterruptedException e) {
//			servicelog.error("装配点乐App中出错:cause by:{}",e);
//		}
//		
//		return rtv;
//	}
//	/***
//	 * <p>
//	 * 1）如果用户已完成的:还是显示吧<br/>
//	 * 2）如果是我们系统已经存在的app,如果outer_id是空白的<br/>
//	 * 3) 如果外部id和已有的app的外部ID不同 不予显示:免得麻烦 <br/>
//	 *      这样有点麻烦，如果是我们已有的app要想跑 就手动改一下app的outer_id 即可
//	 * 都不给予显示 
//	 * </p>
//	 * @param object
//	 * @return
//	 */
//	private UserTaskVo assemblyApp(JSONObject object,User user){
//		String bundleID =  object.getString("pack_name");
//		String ad_id = object.getString("ad_id");
//		
//		if(StringUtil.isBlank(bundleID)){
//			return null;
//		}
//		App app  = appTaskService.getAppByBundleID(bundleID);
//		boolean isNewApp = false;
//		UserTask userTask = null;
//		
//		//如果是存在的app 并且没有外部ID 过滤
//		if(app != null ){
////			//没有外部ID 或者是空白的 
////			if(StringUtil.isBlank(app.getOuter_id())){
////				return null;
////			}
////			//如果不同于系统中的外部id那就不予显示 
////			if(app.getOuter_id().equals(ad_id) == false){
////				return null;
////			}
////			//不是一家的app
////			if("dianJoyQuickTaskService".equals(app.getQuick_service()) == false){
////				return null;
////			}
//			//安装过的不予显示 这个应该不用管  他们返回的数据应该就是用户可以做的 
//			//增加判断是不是做过的任务 
//			//TODO add to cache 
//			//用户做过的任务 那就是必须在我们系统录过的app 积分墙除外
//			userTask = userTaskService.getUserTaskByAppId(user.getId(), app.getId());
//			//得过奖的 就不显示啦
//			if(userTask.isConfirmFinish() && userTask.getReward() == 1){
//				return null;
//			}
//			
//		}else{
//			isNewApp = true;
//			app = new App();
//			app.setAppstore_id(String.valueOf(ItunesUtil.parseItunesURL(object.getString("ad_url"))));
//		}
//		
//		UserTaskVo vo = new UserTaskVo();
//		AppTask apptask = new AppTask();
//		//URLscheme: url_schema    processname:  process_name  bundleidentifier: pack_name
//		vo.setUserTask(userTask);
//		
//		app.setName(object.getString("title"));
//		app.setDownload_size(object.getString("size"));
//		app.setIcon(object.getString("icon"));
//		app.setUrl(object.getString("ad_url"));
//		app.setBundle_id(bundleID);
//		app.setDescription(object.getString("text"));
//		app.setAgreement(object.optString("url_schema"));
//		app.setProcess_name(object.optString("process_name"));
//		app.setIs_promotion(1);
//		
//		apptask.setDuration(object.optInt("runtime", 1800));
//		apptask.setKeywords(object.getString("keywords"));
//		apptask.setAmount(calcuateAmount(object.optInt("price",100),false));
//		apptask.setStart_time(DateUtil.getTodayStartDate());
//		apptask.setEnd_time(DateUtil.getTodayEndDate());
//		apptask.setShow_end_time(apptask.getEnd_time());
//		apptask.setFriends_amount(10);
//		apptask.setState(Constants.ESTATE_Y);
//		apptask.setDescription(object.getString("description"));
//		apptask.setCurrent_task(100);
//		
//		if(isNewApp){
//			//app = appTaskService.addApp(app);
//			if(app == null){
//				//return null;
//			}
//		}
//		//外部ID不同 
//		//现在是更新 如果以后是多家都有
////		if(ad_id.equals(app.getOuter_id()) == false){
//////			if(this.appTaskService.updateOuterId(app.getId(), ad_id) == false){
//////				return null;
//////			}
////		}
//		vo.setAppTask(apptask);
//		vo.setApp(app);
//		return vo;
//	}
//	//计算出给用户多少钱
//	/**
//	 * 如果是付费的 
//	 * 不是付费的 
//	 * 管他的 显示的钱是给用户的
//	 * @param srcAmount
//	 * @param isPayTask
//	 * @return
//	 */
//	private int calcuateAmount(int srcAmount,boolean isPayTask){
//		return srcAmount;
//	}
//
//	@Override
//	public boolean reportTaskFinsh(ClientInfo clientInfo, User user, AppTask appTask, App app,AppTaskChannelVo vo,
//			Map<String, String> extraParams) {
//		return false;
//	}
//
//	@Override
//	public void assembly(UserTaskVo vo, JSONObject object) {
//		
//	}
//
//}
