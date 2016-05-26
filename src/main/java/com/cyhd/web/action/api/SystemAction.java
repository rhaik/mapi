package com.cyhd.web.action.api;

import com.cyhd.common.util.StringUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.AppUpdateIos;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserBanner;
import com.cyhd.service.dao.po.UserHomePageMenu;
import com.cyhd.service.impl.AppUpdateService;
import com.cyhd.service.impl.SystemService;
import com.cyhd.service.impl.UserBannerService;
import com.cyhd.service.impl.UserHomePageMenuService;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.IpAddressUtil;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.service.vo.AppUpdate;
import com.cyhd.web.common.BaseAction;
import com.cyhd.web.common.ClientInfo;
import com.cyhd.web.exception.CommonException;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1")
public class SystemAction extends BaseAction {

	@Resource
	AppUpdateService appUpdateService;
	
	@Resource
	UserBannerService userBannerService;

	@Resource
	private UserHomePageMenuService userHomePageMenuService; 
	
	@Resource
	private SystemService systemService;
	
	private static final String prefix = "/api/v1/sys/";

	@RequestMapping(value = {"/sys/check_update","/af/fa"})
	public ModelAndView checkUpdate(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "update.json.ftl");
		
		int type = ServletRequestUtils.getIntParameter(request, "type", 2);
		
		ClientInfo info = getClientInfo(request);
		
		String v = getClientInfo(request).getAppVer() ;
		
		AppUpdate versions = null;
		AppUpdateIos ios = null;
		if(type == Constants.platform_ios){
			ios = appUpdateService.getAppUpdateIosByBundleId(info.getAppnm());

			boolean entitled = false;
			String entrance = "/web/online/enter.html";

			//检查ios版本是否已审核通过(在后台添加即表示审核通过，不区分有效状态)
			if ( appUpdateService.isAppAudited(info.getAppnm())){
				if (!GlobalConfig.isDeploy){
					entitled = true;
				}else {
					String area = IpAddressUtil.getAddress(info.getIpAddress());
					if (area == null || area.startsWith("中国")) { //必须是中国的ip
						entitled = true;
					}
				}
			}else { //后台未配置其审核通过
				//未审核通过，如果是已有秒赚用户请求，默认显示上线
				String idfa = info.getIdfa();
				if (StringUtil.isNotBlank(idfa)){
					User user = userService.getUserByIdfa(idfa);
					if (user != null && !user.isBlack()){
						entitled = true;
					}
				}
			}

			if (entitled) {
				mv.addObject("entitled", entitled);
				mv.addObject("home_url", entrance);
			}
		}else{
			versions = appUpdateService.getAndroidVersion();
		}
		mv.addObject("update", "false");
		mv.addObject("forceUpdate", "false");
		
		if(versions != null){
			int paramVersion = VersionUtil.getVersionCode(v);  //客户端当前版本
			int setVersion = VersionUtil.getVersionCode(versions.getVersion()); //配置的最新版本
			if(paramVersion == 0 || setVersion == 0){
				logger.error("AppUpdateAction check_update error, param version={} ,set version={}", v, versions.getVersion());
			}
			if(setVersion > paramVersion){
				mv.addObject("update", "true");
				mv.addObject("version", versions);
				if(GlobalConfig.isDeploy){
					if(versions.getVersion().endsWith(".0") || versions.getVersion().indexOf('.') == versions.getVersion().lastIndexOf('.')){
						mv.addObject("forceUpdate", "true");
					}
				}else{
					if(versions.getVersion().equals("1.0.30")){
						mv.addObject("forceUpdate", "true");
					}
				}
			}
		} else if(ios != null) {
			int paramVersion = VersionUtil.getVersionCode(v);  //客户端当前版本
			int setVersion = VersionUtil.getVersionCode(ios.getVersion()); //配置的最新版本
			if(paramVersion == 0 || setVersion == 0){
				logger.error("AppUpdateAction check_update error, param version={} ,set version={}", v, ios.getVersion());
			}
			if(setVersion > paramVersion) {
				mv.addObject("download_url", appUpdateService.getNewestDownloadUrl(ios));
				mv.addObject("update", "true");
				mv.addObject("iosVersion", ios);
				if(GlobalConfig.isDeploy){
					if(ios.getVersion().endsWith(".0") || ios.getVersion().indexOf('.') == ios.getVersion().lastIndexOf('.')){
						mv.addObject("forceUpdate", "true");
					}
				}else{
					if(ios.getVersion().equals("1.0.30")){
						mv.addObject("forceUpdate", "true");
					}
				}
			}
		}
		fillStatus(mv);
		return mv;
	}
	
	@RequestMapping(value = {"/sys/banners","/am/ma"})
	public ModelAndView banner(HttpServletRequest request) throws Exception {

		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix + "banners.json.ftl");

		List<UserBanner> banners = userBannerService.getSystemBanners();
		
		mv.addObject("banners", banners);

		fillStatus(mv);
		return mv;
	}
	
	@RequestMapping(value={"/sys/homePageMenu","/at/ta"},method=RequestMethod.POST)
	public ModelAndView homePageMenus(HttpServletRequest request,HttpServletResponse response) throws Exception{
		ModelAndView mv = new ModelAndView();
		mv.setViewName(prefix+"homePageMenus.json.ftl");
		ClientInfo clientInfo = getClientInfo(request);
		int clientType = clientInfo.isIos()?UserHomePageMenu.IOS:UserHomePageMenu.ANDROID;

		List<UserHomePageMenu> homePageMenus = this.userHomePageMenuService.getHomePageMenus(clientType,clientInfo.getAppVer());
		boolean shouldHide = false;
		if (GlobalConfig.isDeploy && clientInfo.isIos()) {
			String bundle = clientInfo.getAppnm();
			List<AppUpdateIos> appList = appUpdateService.getAllApps();
			long len = appList.stream().filter(appUpdateIos -> bundle != null && bundle.equals(appUpdateIos.getBundle_id())).count();

			//是否要隐藏一些菜单
			if (len == 0 && !"com.lieqicun.bigmoney".equals(bundle)){
				shouldHide = true;
			}
		}

		if (shouldHide) {
			mv.addObject("homePageMenus", homePageMenus.stream().filter(menu -> menu.getId() != 5 && menu.getId() != 7).collect(Collectors.toList()));
		}else{
			mv.addObject("homePageMenus", homePageMenus);
		}
		return mv;
	}
	
	@RequestMapping(value="/integral/conf",produces={"text/json; charset=UTF-8"})
	@ResponseBody
	public String loadIntegralWall(HttpServletRequest request,HttpServletResponse response) throws CommonException{
		ClientInfo clientInfo = getClientInfo(request);
		String data = systemService.getClientIntegralWallConf(clientInfo);
		JSONObject rtv = new JSONObject();
		rtv.accumulate("code", 0);
		rtv.accumulate("message", "ok");
		if(data != null){
			rtv.accumulate("data", JSONObject.fromObject(data));
		}
		return rtv.toString();
	}
}
