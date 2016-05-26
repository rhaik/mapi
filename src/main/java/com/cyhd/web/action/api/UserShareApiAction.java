package com.cyhd.web.action.api;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.po.AppUpdateIos;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.AppUpdateService;
import com.cyhd.service.impl.QiniuService;
import com.cyhd.service.impl.UserShareService;
import com.cyhd.service.util.CookieUtil;
import com.cyhd.service.util.MD5;
import com.cyhd.service.util.RequestUtil;
import com.cyhd.web.common.BaseAction;
import com.google.gson.JsonObject;

@Controller
@RequestMapping("/api/v1")
public class UserShareApiAction extends BaseAction {

	@Resource
	private AppUpdateService appUpdateService;
	
	@Resource
	private UserShareService userShareService;
	
	@Resource
	private QiniuService qiniuService;
	
	/**
	 * 获取App最新的下载地址
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/share/app/url")
	@ResponseBody
	public String showUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String ip = RequestUtil.getIpAddr(request);
		String downloadId = CookieUtil.getCookieValue("sharedownload", request);
		AppUpdateIos ios = appUpdateService.getAppUpdateIos(downloadId);
		if(downloadId.isEmpty() || ios == null) {
			ios = appUpdateService.selectAppUpdateIos(ip);

			downloadId = MD5.getMD5(ios.getId() + AppUpdateService.ID_HASH_SALT);
			logger.info("user download ios:{}, new cookie:{}", ios, downloadId);
		}else {
			logger.info("user download ios:{} from cookie id:{}", ios, downloadId);
		}

		CookieUtil.setNewCookie("sharedownload", downloadId, 365 * 24 * 60 * 60, null, response);

		return appUpdateService.getNewestDownloadUrl(ios);
	}
	
	/**
	 * 获取用户分享的URL
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/share/share/url")
	@ResponseBody
	public String shareUrl(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User u = getUser(request);
		return userShareService.getUserShareUrl(u);
	}
	
	@RequestMapping(value={"/share/share/config","/ar/ra"},method=RequestMethod.POST,produces="text/json; charset=UTF-8")
	@ResponseBody
	public String getShareConfig(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		User u = getUser(request);
		JsonObject json = new JsonObject();
		
		JsonObject wx = new JsonObject();
		wx.addProperty("share_logo_url", Constants.share_logo_url);
		wx.addProperty("share_title", Constants.share_wx_title);
		wx.addProperty("share_wx_content", Constants.share_wx_content);
		wx.addProperty("share_wx_url", Constants.share_wx_pre_link+u.getInvite_code());
		json.add("share_wx", wx);
		json.addProperty("code", 0);
		return json.toString();
	}
	
	@RequestMapping(value={"/share/pic_url/{id:\\w+}","/as/sa/{id:\\w+}"},method=RequestMethod.POST,produces="text/json; charset=UTF-8")
	@ResponseBody
	public String getSharePicURL(@PathVariable("id")String unionIdMd5,HttpServletRequest request,HttpServletResponse response){
		
		JsonObject json = new JsonObject();
		
		User user = userService.getUserByInviteCode(unionIdMd5);
		String[] pics = null;
	
		long start = System.currentTimeMillis();
		pics = userShareService.getUserShareURLByQiNiuPic(user);
		long end = System.currentTimeMillis();
			
		if(logger.isDebugEnabled()){
			logger.debug("times:{}",end-start);
		}
		
		String loadFailPath = qiniuService.getResourceURLByFileName("load_fail.png");
		StringBuilder  arr = new StringBuilder(100);
		
		for(int i = 1 ; i < pics.length+1 ; i++){
			if(StringUtils.isBlank(pics[i-1])){
				pics[i-1] = loadFailPath;
			}
			arr.append(pics[i-1]).append(",");
		}
		arr.delete(arr.lastIndexOf(","),arr.length());
		
		json.addProperty("pics", arr.toString());
		json.addProperty("code", 0);
		return json.toString();
	}
}
