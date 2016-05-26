package com.cyhd.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.cyhd.service.dao.po.User;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.LiveAccess;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.UserBannerMapper;
import com.cyhd.service.dao.po.UserBanner;

/**
 * 
 * 用户轮播图 service
 *
 */
@Service
public class UserBannerService {

	public static final int CATEGORY_SYSTEM = 1;
	public static final int CATEGORY_DUOBAO = 2;


	@Resource
	private UserBannerMapper userBannerMapper ;
	
	@Resource
	private PropertiesService propertiesService ;
	
	
	private LiveAccess<List<UserBanner>> cachedBanners = new LiveAccess<List<UserBanner>>(Constants.minutes_millis * 5, null);
	
	
	public List<UserBanner> getAllBanners() {
		List<UserBanner> banners = cachedBanners.getElement();
		if(banners == null) {
			banners = userBannerMapper.getBanners() ;
			if(banners != null){
				cachedBanners = new LiveAccess<List<UserBanner>>(10 * Constants.minutes_millis, banners);
			}
		}

		if (banners == null){
			banners = Collections.EMPTY_LIST;
		}
		return banners ;
	}

	/**
	 * 获取系统的Banner
	 * @return
	 */
	public List<UserBanner> getSystemBanners(){
		return getBanners(CATEGORY_SYSTEM);
	}
	
	public List<UserBanner> getBanners(int category){
		return getAllBanners().stream().filter( userBanner -> userBanner.getCategory() == category).collect(Collectors.toList());
	}

}
