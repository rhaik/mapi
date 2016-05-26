package com.cyhd.service.impl;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import com.cyhd.common.util.NumberUtil;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.MD5;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.AppUpdateIosMapper;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.AppUpdateIos;
import com.cyhd.service.vo.AppUpdate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppUpdateService extends BaseService {


	public static final String ID_HASH_SALT = "bigmoney2015";

	@Resource
	PropertiesService propertyService;
	@Resource
	AppUpdateIosMapper appUpdateIosMapper;
	
	private AppUpdate iosVersion = new AppUpdate("1.0.0", "");
	private AppUpdate androidVersion = new AppUpdate("1.0.0", "");
	
	private CacheLRULiveAccessDaoImpl<List<AppUpdateIos>> cachedAppUpdate = new CacheLRULiveAccessDaoImpl<List<AppUpdateIos>>(5*Constants.minutes_millis, 100);
	private volatile boolean loading = false;
	
	@PostConstruct
	public void reload(){
		if(loading)
			return;
		try {
			loading = true;
			String androidProp = propertyService.getAndroidVersion();

			if (androidProp != null) {
				AppUpdate temp = parse(androidProp);
				if (temp != null) {
					androidVersion = temp;
				}
			}
			String iosProp = propertyService.getIosVersion();
			if (iosProp != null) {
				AppUpdate temp = parse(iosProp);
				if (temp != null) {
					iosVersion = temp;

					//将后台填写的地址放入origUrl中保存，新下载地址放在url字段中
					String iosUrl = iosVersion.getUrl();
					iosVersion.setOrigUrl(iosUrl);
					iosVersion.setUrl(fixIOSDownnloadUrl(iosUrl));
				}
			}
		} catch (Exception e) {
			logger.error("reload app update info error!", e);
		}finally{
			loading = false;
		}
	}

	/**
	 * 修正ios更新链接地址，如果链接是http(s)链接，则使用自己的下载服务
	 * @return
	 */
	public String fixIOSDownnloadUrl(String iosUrl){
		//如果是http或者https地址，说明是ios安装包
		if (!iosUrl.startsWith("itms-services://")) {
			String url = GlobalConfig.base_url_https + "open/ios/manifest.plist";
			try {
				iosUrl = "itms-services://?action=download-manifest&url=" + URLEncoder.encode(url, "utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		return iosUrl;
	}
	/**
	 * 修正ios更新链接地址，如果链接是http(s)链接，则使用自己的下载服务
	 * @return
	 */
	public String fixIOSDownnloadUrl(String iosUrl, String id){
		//如果是http或者https地址，说明是ios安装包
		if (!iosUrl.startsWith("itms-services://")) {
			String url = GlobalConfig.base_url_https + "open/ios/manifest-"+id+".plist";
			try {
				iosUrl = "itms-services://?action=download-manifest&url=" + URLEncoder.encode(url, "utf-8");
			} catch (UnsupportedEncodingException e) {
				logger.error("", e);
			}
		}
		return iosUrl;
	}
	
	public String getNewestDownloadUrl(){
		return iosVersion.getUrl();
	}

	/**
	 * 根据AppUpdateIos对象获取iOS安装地址
	 * @param ios
	 * @return
	 */
	public String getNewestDownloadUrl(AppUpdateIos ios){
		return fixIOSDownnloadUrl(ios.getDownload_url(),MD5.getMD5(ios.getId() + ID_HASH_SALT));
	}
	/**
	 * 获取AppUpdateIos
	 * @param id
	 * @return
	 */
	public AppUpdateIos getAppUpdateIos(int id){
		List<AppUpdateIos> list = getAppIosList();
		if (list != null && id < list.size()) {
			return list.get(id);
		}
		return null;
	}
	/**
	 * 获取AppUpdateIos
	 * @param bundleId
	 * @return
	 */
	public AppUpdateIos getAppUpdateIosByBundleId(String bundleId){
		List<AppUpdateIos> list = getAppIosList();
		for(AppUpdateIos ios:list) {
			if(ios.getBundle_id().equals(bundleId)) {
				return ios;
			}
		}
		return null;
	}

	public boolean isAppAudited(String bundleId){
		long len = getAllApps().stream().filter(appUpdateIos -> bundleId != null && bundleId.equals(appUpdateIos.getBundle_id())).count();
		return len > 0;
	}
	/**
	 * 获取AppUpdateIos
	 * @param encryptionId
	 * @return
	 */
	public AppUpdateIos getAppUpdateIos(String encryptionId){
		List<AppUpdateIos> list = getAppIosList();
		for(AppUpdateIos ios:list) {
			String id = MD5.getMD5(ios.getId()+ ID_HASH_SALT);
			if(id.equals(encryptionId)) {
				return ios;
			}
		}
		return null;
	}

	/**
	 * 根据bundle id判断是不是要是版本。判断标准：后台添加的App名称中包含“钥匙”
	 * @param bundle
	 * @return
	 */
	public boolean isYaoshiBundle(String bundle){
		AppUpdateIos updateIos = getAppUpdateIosByBundleId(bundle);
		if (updateIos != null && updateIos.isKeyApp()){
			return true;
		}
		return false;
	}


	/**
	 * 根据ip地址来选择下载app
	 * @param ip
	 * @return
	 */
	public AppUpdateIos selectAppUpdateIos(String ip){
		List<AppUpdateIos> list = getWeightedIosList(getAppIosList());

		long ipLong = NumberUtil.safeParseLong(ip.replaceAll("\\.", ""));

		logger.info("select app update ios:ip:{}, list:{}", ipLong, list);

		if (list.size() > 0){
			return list.get((int)(ipLong % list.size()));
		}
		return null;
	}
	
	/**
	 * 根据ip地址来选择下载app钥匙
	 * @param ip
	 * @return
	 */
	public AppUpdateIos selectAppUpdateIosKey(String ip){
		List<AppUpdateIos> origList = getAppIosList();
		origList = origList.stream().filter(AppUpdateIos::isKeyApp).collect(Collectors.toList());
		List<AppUpdateIos> list = getWeightedIosList(origList);

		long ipLong = NumberUtil.safeParseLong(ip.replaceAll("\\.", ""));

		logger.info("select app update ios:ip:{}, list:{}", ipLong, list);

		if (list.size() > 0){
			//按照以前的获取逻辑 获取钥匙
			return list.get((int)(ipLong % list.size()));
		}
		return null;
	}
	

	/**
	 * 根据权重获取列表，某些权重小的，在一段时间内不显示
	 * @return
	 */
	public List<AppUpdateIos> getWeightedIosList(List<AppUpdateIos> origList ){

		//权重小的放前面
		Collections.sort(origList, (o1, o2) -> o1.getWeight() - o2.getWeight());
		int len = origList.size();

		//计算当前是第几分钟
		Calendar cal = Calendar.getInstance();
		int min = cal.get(Calendar.MINUTE);


		List<AppUpdateIos> newList = new ArrayList<>();

		//根据权重检查是否要显示
		//先根据总权重，计算某个应用要“缺席”的时间，再根据其id, 计算其具体缺席的时间段
		for (int i = 0 ; i < len; ++ i){
			AppUpdateIos updateIos = origList.get(i);

			//权重大于等于100，则一直显示
			if (updateIos.getWeight() >= 100){
				newList.add(updateIos);
			}else{
				//"缺席"的时间长度，最低1分钟
				int absMin = 60 - (int)(updateIos.getWeight() / 100.0 * 60);

				//开始不显示的时间
				int startMin = (int)(60.0 / len  * i);
				int endMin = startMin + absMin;

				if (endMin > 60){
					startMin = 60 - absMin;
					endMin = 60;
				}

				if (min >= startMin && min < endMin){
					//正好在不显示的时间段，不加入新列表
				}else {
					newList.add(updateIos);
				}
			}
		}

		return newList;
	}
	/**
	 * 获取ios下载列表
	 * @return
	 */
	public List<AppUpdateIos> getAppIosList(){
		return getAllApps().stream().filter(appUpdateIos -> appUpdateIos.getStatus() == 1).collect(Collectors.toList());
	}


	/**
	 * 获取当前所有的App，包括已失效的
	 * @return
	 */
	public List<AppUpdateIos> getAllApps(){
		String key = "AppUpdateIos";
		List<AppUpdateIos> list = cachedAppUpdate.get(key);
		if(list == null) {
			list = appUpdateIosMapper.getList();
			if (list != null && list.size() > 0) {
				cachedAppUpdate.set(key, list);
			}
		}

		if (list == null){
			list = new ArrayList<>();
		}
		return list;
	}
	/**
	 * 更新下载数量
	 * 
	 * @param id
	 * @return
	 */
	public boolean updateAppDownloadNumber(int id){
		return appUpdateIosMapper.updateAppDownloadNumber(id) > 0;
	}
	private static AppUpdate parse(String content){
		try{
			JSONObject json = JSONObject.fromObject(content);
			String version = json.getString("version");
			String url = json.getString("url");
			String reason = "";
			if(json.has("content"))
					reason = json.getString("content");
			AppUpdate update = new AppUpdate();
			update.setVersion(version);
			update.setUrl(url);
			update.setContent(reason);
			return update;
			
		}catch(Exception e){
			logger.error("AppUpdateService parse app version property json error! content=" + content, e);
			return null;
		}
	}
	
	private static String toJson(String version, String url, String reason){
		JSONObject json = new JSONObject();
		json.put("version", version);
		json.put("url", url);
		if(reason == null)
			reason="";
		json.put("content", reason);
		return json.toString();
	}
	
	public boolean saveUpdateVersion(int type, String version, String url, String reason){
		if(type == Constants.platform_ios){
			return propertyService.updateIosAppVersion(toJson(version, url, reason));
		}else if(type == Constants.platform_android){
			return propertyService.updateAndroidAppVersion(toJson(version, url,reason));
		}
		return false;
	}
	
	public AppUpdate getIosVersion(){
		return iosVersion;
	}
	
	public AppUpdate getAndroidVersion(){
		return androidVersion;
	}
	
}
