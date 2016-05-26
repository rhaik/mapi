package com.cyhd.service.impl;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.AppVendorMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.*;
import com.cyhd.service.util.AppContext;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.vendor.IVendorClickService;
import com.cyhd.web.common.ClientInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

import javax.annotation.Resource;

/**
 * Created by hy on 9/15/15.
 */
@Service
public class AppVendorService {

    private final static Logger logger = LoggerFactory.getLogger("vendor");

    private static final int CACHE_TTL = Constants.minutes_millis * 60;

    CacheLRULiveAccessDaoImpl<AppVendor> appVendorCache = new CacheLRULiveAccessDaoImpl<AppVendor>(CACHE_TTL);


    @Resource
    UserService userService;

    @Resource
    AppVendorMapper appVendorMapper;

    @Resource
    AppTaskService appTaskService;

    @Resource
    UserTaskService userTaskService;

    @Resource
    UserTaskCalculateService userTaskCalculateService;
    
    @Resource
    UserTaskNotifyService userTaskNotifyService;

    /**
     * 获取app vendor
     * @param id
     * @return
     */
    public AppVendor getAppVendor(int id){
        String cacheKey = "" + id;
        AppVendor vendor = appVendorCache.get(cacheKey);
        if (vendor == null){
            vendor = appVendorMapper.getAppVendor(id);
            if (vendor != null){
                appVendorCache.set(cacheKey, vendor);
                appVendorCache.set(vendor.getApp_key(), vendor);
            }
        }
        return vendor;
    }


    /**
     * 根据appkey获取AppVendor
     * @param appkey
     * @return
     */
    public AppVendor getAppVendorByAppKey(String appkey){
        AppVendor vendor = appVendorCache.get(appkey);
        if (vendor == null){
            vendor = appVendorMapper.getAppVendorByAppKey(appkey);
            if (vendor != null){
                appVendorCache.set("" + vendor.getId(), vendor);
                appVendorCache.set(vendor.getApp_key(), vendor);
            }
        }
        return vendor;
    }


    /**
     * 处理厂商回调
     * @param vendor
     * @param adid  对应本系统中UserTask的id
     * @param idfa  用户的idfa
     * @return
     */
    public String onVendorCallback(AppVendor vendor, String adid, String idfa){
        String result = null; //null表示执行成功

        int aidNum = NumberUtil.safeParseInt(adid);
        if (aidNum <= 0){
            result =  "adid is invalid";
        }else {
            int appId = (aidNum - 997) / 11;
            App app = appTaskService.getApp(appId);

            //检查vendor与task之间的对应关系
            if (app == null || app.getVendor_id() != vendor.getId()) {
                result = "appkey and adid not match";
            }else {
                //根据idfa获取用户任务信息
                UserTask userTask = userTaskService.getUserTaskByIdfaAndAppId(idfa, appId);
                UserTaskNotify utn = null;
                if (userTask != null && userTask.getConfirm_finish() == 0){
                    logger.info("app vendor callback, confirm user task finished, userTask:{}, vendor:{}", userTask, vendor);

                    User user = userService.getUserById(userTask.getUser_id());
                    AppTask appTask = appTaskService.getAppTask(userTask.getTask_id());

                    userTaskCalculateService.onConfirmFinishTask(user, app, appTask, userTask);
                }else if((utn = userTaskNotifyService.getByIdfaAndAppId(idfa, appId))!=null){	//渠道回调
                	userTaskNotifyService.onVendorCallback(utn);
                }else{
                    //userTask不存在，或者状态不对，也认为调用成功
                    logger.warn("app vendor callback, user task not exists or confirmed, vendor:{}, adid:{}, idfa:{}", vendor, adid, idfa);
                }
            }
        }

        logger.info("app vendor callback, vendor:{}, adid:{}, idfa:{}, result:{}", vendor, adid, idfa, result);
        return result;
    }


    /**
     * 向厂商发起点击请求
     * @param user
     * @param appTask
     * @param clientInfo
     * @return true表示可以接任务，否则不能接任务
     */
    public boolean onClick(User user, AppTask appTask, ClientInfo clientInfo) {
        App app = appTaskService.getApp(appTask.getApp_id());
        AppVendor vendor = getAppVendor(app.getVendor_id());

        //需要click
        boolean isAllowed = false;
        if (StringUtils.isNotBlank(vendor.getClick_url())){
            //动态获取Spring服务
            String serviceName = vendor.getService_name();
            if (StringUtils.isNotBlank(serviceName)){
                try {
                    IVendorClickService vendorService = AppContext.getAppContext().getBean(serviceName.trim(), IVendorClickService.class);
                    if (vendorService != null) {
                        isAllowed = vendorService.onClickApp(vendor, user, app, appTask, clientInfo);
                    }else{
                        logger.warn("app vendor service not found:{}, appTask:{}", serviceName, appTask);
                    }
                }catch (Exception exp){
                    logger.error("app vendor click error, service:{}, task:{}, error:{}", serviceName, appTask, exp);
                }
            }else {
                logger.warn("app vendor service name empty:{}, appTask:{}", serviceName, appTask);
            }
        }else {
            logger.info("app vendor click, no need click, vendor:{}, task:{}", vendor, appTask);
            isAllowed = true;
        }

        logger.info("app vendor click, appTask:{}, user:{}, result:{}", appTask, user, isAllowed);

        return isAllowed;
    }
    
    /**
     * 向厂商发起点击请求
     * @param app
     * @param appTask
     * @param clientInfo
     * @return true表示可以接任务，否则不能接任务
     */
    public boolean onClick(App app, AppTask appTask, ClientInfo clientInfo) {
        AppVendor vendor = getAppVendor(app.getVendor_id());

        //需要click
        boolean isAllowed = false;
        if (StringUtils.isNotBlank(vendor.getClick_url())){
            //动态获取Spring服务
            String serviceName = vendor.getService_name();
            if (StringUtils.isNotBlank(serviceName)){
                try {
                    IVendorClickService vendorService = AppContext.getAppContext().getBean(serviceName.trim(), IVendorClickService.class);
                    if (vendorService != null) {
                        isAllowed = vendorService.onClickApp(vendor, app, appTask, clientInfo);
                    }else{
                        logger.warn("app vendor service not found:{}, appTask:{}", serviceName, appTask);
                    }
                }catch (Exception exp){
                    logger.error("app vendor click error, service:{}, task:{}, error:{}", serviceName, appTask, exp);
                }
            }else {
                logger.warn("app vendor service name empty:{}, appTask:{}", serviceName, appTask);
            }
        }else {
            logger.info("app vendor click, no need click, vendor:{}, task:{}", vendor, appTask);
            isAllowed = true;
        }
        return isAllowed;
    }
    /**
	 * 讲今日头条的appid转化为我们系统的adid(需要*11+997)<br/>
	 * 头条ad_id＝23 app_id＝13
	段子，ad_id=24  app-id＝7
	 */
	public int convertToDayAppId(int today_app_id){
		if(today_app_id == 13){
			if(GlobalConfig.isDeploy){
				return 104*11+997;
			}
			return 123*11+997;
		}else if(today_app_id == 7){
			if(GlobalConfig.isDeploy){
				return 102*11+997;
			}
			return 125	*11+997;
		}
		return 0;
	}
	
	 public String onDisctinct(App app,  String idfas) {
	        AppVendor vendor = getAppVendor(app.getVendor_id());
	        //需要click
	        String rest = null;
	        //是不是要增加一个 查询idfa接口
	        if (StringUtils.isNotBlank(vendor.getClick_url())){
	            //动态获取Spring服务
	            String serviceName = vendor.getService_name();
	            if (StringUtils.isNotBlank(serviceName)){
	                try {
	                    IVendorClickService vendorService = AppContext.getAppContext().getBean(serviceName.trim(), IVendorClickService.class);
	                    if (vendorService != null) {
	                    	rest = vendorService.disctinct(vendor, app, idfas);
	                    }else{
	                        logger.warn("app vendor service not found:{}, app:{}", serviceName, app);
	                    }
	                }catch (Exception exp){
	                    logger.error("app vendor click error, service:{}, app:{}, error:{}", serviceName, app, exp);
	                }
	            }else {
	                logger.warn("app vendor service name empty:{}, app:{}", serviceName, app);
	            }
	        }else {
	            logger.info("app vendor click, no need click, vendor:{}, app:{}", vendor, app);
	            rest = null;
	        }
	        return rest;
	    }
	 public Map<String, Integer> onDisctinctNew(App app,  String idfas) {
	        AppVendor vendor = getAppVendor(app.getVendor_id());
	        //需要click
	        Map<String, Integer> rest = null;
	        //是不是要增加一个 查询idfa接口
	        if (StringUtils.isNotBlank(vendor.getClick_url())){
	            //动态获取Spring服务
	            String serviceName = vendor.getService_name();
	            if (StringUtils.isNotBlank(serviceName)){
	                try {
	                    IVendorClickService vendorService = AppContext.getAppContext().getBean(serviceName.trim(), IVendorClickService.class);
	                    if (vendorService != null) {
	                    	rest = vendorService.disctinctNew(vendor, app, idfas);
	                    }else{
	                        logger.warn("app vendor service not found:{}, app:{}", serviceName, app);
	                    }
	                }catch (Exception exp){
	                    logger.error("app vendor click error, service:{}, app:{}, error:{}", serviceName, app, exp);
	                }
	            }else {
	                logger.warn("app vendor service name empty:{}, app:{}", serviceName, app);
	            }
	        }else {
	            logger.info("app vendor click, no need click, vendor:{}, app:{}", vendor, app);
	            rest = null;
	        }
	        return rest;
	    }
}
