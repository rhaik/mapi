package com.cyhd.service.channelQuickTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyhd.common.util.GenerateDateUtil;
import com.cyhd.common.util.LiveAccess;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.App;
import com.cyhd.service.dao.po.AppChannel;
import com.cyhd.service.dao.po.AppTask;
import com.cyhd.service.dao.po.AppTaskChannel;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserInstalledApp;
import com.cyhd.service.dao.po.UserTask;
import com.cyhd.service.impl.AppTaskService;
import com.cyhd.service.impl.ChannelService;
import com.cyhd.service.impl.PropertiesService;
import com.cyhd.service.impl.UserInstalledAppService;
import com.cyhd.service.impl.UserTaskService;
import com.cyhd.service.util.CollectionUtil;
import com.cyhd.service.vo.AppTaskChannelVo;
import com.cyhd.service.vo.UserTaskVo;
import com.cyhd.web.common.ClientInfo;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public abstract class IQuickTaskService {
	@Resource
	AppTaskService appTaskService;
	
	@Resource
	UserTaskService userTaskService;
	
	@Resource
	ChannelService channelService;
	
	ConcurrentHashMap<Integer, App> cachedApps = new ConcurrentHashMap<Integer, App>(); 
	
	static Logger logger = LoggerFactory.getLogger("quicktask"); 
	
	@Resource
	private PropertiesService propertiesService;
	
	private LiveAccess<List<String>> quickFilterCache = new LiveAccess<List<String>>(Constants.minutes_millis*5, null);
	
	@Resource
	UserInstalledAppService userInstalledAppService;
	
	/****
	 * 得到该服务的渠道 在组装apptaskchannel的时候要用<br/>
	 * 为了简单 使用的时候 将ID值配置在{@link AppChannel}中
	 * @return
	 */
	abstract int getAppChannelID();
	abstract String getAdidKey();
	
	protected AppChannel getAppChannel(){
		return channelService.getAppChannel(getAppChannelID());
	}
	/***
	 * 数据库配置的过滤列表
	 * @return
	 */
	protected List<String> getFilterAppIdList(){
		List<String> filterList = quickFilterCache.getElement();
		if(filterList == null){
			String filterData = propertiesService.getQuickTaskFiterList();
			if(StringUtil.isNotBlank(filterData)){
				filterList = CollectionUtil.fromStringToStringList(filterData);
				quickFilterCache = new LiveAccess<List<String>>(Constants.minutes_millis*5, filterList);
			}
		}
		return filterList;
	}
	/***
	 * 获取开墙数据
	 * @param clientInfo
	 * @param extraParams 额外的参数
	 * @return
	 */
	public abstract List<UserTaskVo> getAllTaskApp( ClientInfo clientInfo,User user, Map<String, String > extraParams);
	
//	/***
//	 * 请求开墙数据 得到里面的app任务列表
//	 * @param clientInfo
//	 * @param user
//	 * @param extraParams
//	 * @return
//	 */
//	public abstract JSONArray getAppTasks(ClientInfo clientInfo,User user, Map<String, String > extraParams);

	/**
	 * 排重接口 没有的话 就返回true
	 * @param clientInfo
	 * @param app
	 * @param vo
	 * @return
	 */
	 protected abstract boolean distinct(ClientInfo clientInfo,User user,App app,AppTaskChannelVo vo);
	/***
	 * 实际的点击接口
	 * @param clientInfo
	 * @param user
	 * @param app
	 * @param appTask
	 * @param vo
	 * @param extraParams
	 * @return
	 */
	 protected abstract  boolean doClick(ClientInfo clientInfo,User user, App app,AppTask appTask,AppTaskChannelVo vo,Map<String, String > extraParams);
	
	/**
	 * 点击接口
	 * @param clientInfo
	 * @param user
	 * @param ad_id
	 * @param extraParams 额外的参数
	 */
	public  boolean click(ClientInfo clientInfo,User user, App app,AppTask appTask,AppTaskChannelVo vo,Map<String, String > extraParams){
		if(this.distinct(clientInfo, user, app, vo) == false){
			return false;
		}
		return this.doClick(clientInfo, user, app, appTask, vo, extraParams);
	}
	/***
	 * 有的任务是上报的
	 * @param clientInfo
	 * @param user
	 * @param appTask
	 * @param app
	 * @param extraParams
	 * @return
	 */
	public abstract boolean reportTaskFinsh(ClientInfo clientInfo,User user,AppTask appTask, App app,AppTaskChannelVo vo); 
//
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
//	
//	/**组装app 从原始的数据中组装出一个app 不做任何操作 */
//	abstract App assemblyApp(JSONObject object);
//	/**
//	 * 组装appTask 从原始的数据中组装出一个appTask 不做任何操作
//	 * @param app 必须是有id 也就是存到我们数据库中去了的app
//	 * @param object
//	 * @return
//	 */
//	abstract AppTask assemblyAppTask(App app,JSONObject object);
	
	public void createChannel(String ad_id,int task_id,int nedReport){
		AppTaskChannel channel = new AppTaskChannel();
		channel.setThird_id(ad_id);
		channel.setApp_channel_id(getAppChannelID());
		channel.setTask_id(task_id);
		//任务不是直接奖励的而都要上报 
		channel.setNeedReport(nedReport);
		channelService.addAppTaskChannel(channel);
	}
}
