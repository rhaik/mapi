package com.cyhd.service.impl;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserHomePageMenuMapper;
import com.cyhd.service.dao.po.UserHomePageMenu;
import com.cyhd.service.util.VersionUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserHomePageMenuService {
	
	@Resource
	private UserHomePageMenuMapper userHomePageMenuMapper;
	
	private LiveAccess<List<UserHomePageMenu>> cachedHomePageMenus = new LiveAccess<List<UserHomePageMenu>>(10, null);

	private final int homePageTTL = 15 * Constants.minutes_millis;


	/**
	 * 缓存所有菜单，不区分类型
	 * @return
	 */
	public List<UserHomePageMenu> getAllHomeMenus(){
		List<UserHomePageMenu> data = cachedHomePageMenus.getElement();
		if(data == null){
			data = userHomePageMenuMapper.getHomePageMenus();
			if (data != null){
				cachedHomePageMenus = new LiveAccess<List<UserHomePageMenu>>(homePageTTL, data);
			}else {
				data = new ArrayList<>();
			}
		}
		return data;
	}

	/**
	 * safari版本首页菜单
	 * @return
	 */
	public  List<UserHomePageMenu> getSafariHomePageMenus(String version){
		List<UserHomePageMenu> data = getAllHomeMenus().stream().filter(menu -> menu.isSafariShow()).collect(Collectors.toList());
		return filterLowestVersion(version, data);
	}

	/**原有的获取ios的首页菜单*/
	public List<UserHomePageMenu> getIosHomePageMenus(String currentVersion){
		List<UserHomePageMenu> data = getAllHomeMenus().stream().filter(menu -> menu.isIosShow()).collect(Collectors.toList());
		return filterLowestVersion(currentVersion, data);
	}
	
	public List<UserHomePageMenu> getHomePageMenus(int clientType,String currentVersion){
		List<UserHomePageMenu> data = null;
		switch (clientType) {
		case UserHomePageMenu.ANDROID:
			data = getAndroidHomePageMenus(currentVersion);
			break;
		case UserHomePageMenu.IOS:
			data = getIosHomePageMenus(currentVersion);
			break;
		case UserHomePageMenu.SAFARI:
			data = getSafariHomePageMenus(currentVersion);
			break;
		default:
			break;
		}
		return data;
	}

	private List<UserHomePageMenu> getAndroidHomePageMenus(String currentVersion) {
		List<UserHomePageMenu> data = getAllHomeMenus().stream().filter(menu -> menu.isAndroidShow()).collect(Collectors.toList());
		return filterLowestVersion(currentVersion, data);
	}
	
	public List<UserHomePageMenu> filterLowestVersion(String currentVersion,List<UserHomePageMenu> menus){
		Calendar cal = GregorianCalendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int currentVersionNum = VersionUtil.getVersionCode(currentVersion);

		return menus.stream().filter(menu -> {
			//7点之前不显示赚金币
			if (hour < 5 && menu.getId() == 7) {
				return false;
			}
			//版本号太小不显示
			if (!StringUtils.isBlank(menu.getVersion()) && currentVersionNum <  VersionUtil.getVersionCode(menu.getVersion())) {
				return false;
			}
			return true;
		}).collect(Collectors.toList());
	}
}
