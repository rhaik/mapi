package com.cyhd.service.impl;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.po.Account;
import com.cyhd.service.dao.po.UserInfo;
import com.cyhd.service.util.RedisUtil;

@Service
public class WeixinService extends BaseService {
	
	//private QHttpClient httpClient = new QHttpClient("utf-8");
	
	@Resource
	AccountService acountService;
	
	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao accessTokenCache;  
	
	/**
	 * {
	   "openid":" OPENID",
	   " nickname": NICKNAME,
	   "sex":"1",
	   "province":"PROVINCE"
	   "city":"CITY",
	   "country":"COUNTRY",
	    "headimgurl":   "http://wx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/46", 
		"privilege":[
		"PRIVILEGE1"
		"PRIVILEGE2"
	    ]
	}
	 * @param account
	 * @param accessToken
	 * @param openId
	 * @return
	 */
	public UserInfo getUserInfo(Account account, String accessToken, String openId){
		if(logger.isInfoEnabled()){
			logger.info("prepare to get userinfo from wx, accesstoken={}, openid={}", accessToken, openId);
		}
		
		String url = "https://api.weixin.qq.com/sns/userinfo?access_token="+ accessToken+"&openid="+ openId +"&lang=zh_CN";
		try{
			String content = HttpUtil.get(url, null, "utf-8");
			if(logger.isInfoEnabled()){
				logger.info("fetch from wx, return: " + content);
			}
			JSONObject ujson = JSONObject.fromObject(content);
			if(ujson.has("errcode")){
				logger.error("errorcode=" + ujson.get("errcode"));
				return null;
			}
			UserInfo userInfo = new UserInfo();
			if(ujson.has("openid"))
				userInfo.setOpenid(ujson.getString("openid"));
			if(ujson.has("nickname"))
				userInfo.setNickname(ujson.getString("nickname"));
			if(ujson.has("sex"))
				userInfo.setSex(ujson.getInt("sex"));
			if(ujson.has("province"))
				userInfo.setProvince(ujson.getString("province"));
			if(ujson.has("city"))
				userInfo.setCity(ujson.getString("city"));
			if(ujson.has("country"))
				userInfo.setCountry(ujson.getString("country"));
			if(ujson.has("headimgurl"))
				userInfo.setHeadimgurl(ujson.getString("headimgurl"));
			if(logger.isInfoEnabled()){
				logger.info("[notice]fetch from wx, userinfo=" + userInfo);
			}
			return userInfo;
		}catch(Exception e){
			logger.error("[notice] web auth getuserinfo error", e);
		}
		return null;
	}

	/**
	 * 获取默认公众号的access token
	 * @return
	 */
	public String getAccessToken(){
		return getAccessToken(acountService.getDefaultAccount());
	}

	/**
	 * 根据公众号信息获取access token
	 * @param wxAccount
	 * @return
	 */
	public String getAccessToken(Account wxAccount){
		String accessToken = null;
		String cacheKey = RedisUtil.buildAccessTokenKey(wxAccount.getWxappid());
		try{
			accessToken = accessTokenCache.get(cacheKey);
		}catch(Exception e){
			logger.error("get Access token from redis error!",e);
		}

		if(StringUtils.isEmpty(accessToken)){
			accessToken = getAccessTokenFromWx(wxAccount);

			logger.info("get access from weixin:{}", accessToken);
			try{
				accessTokenCache.set(cacheKey, accessToken, 60);
			}catch(Exception e){
				logger.error("set Access token to redis error!",e);
			}
		}else{
			logger.info("get access token from cache, value={}", accessToken);
		}

		return accessToken;
	}

	/**
	 * 调用微信接口获取公众号信息
	 * @param account
	 * @return
	 */
	private String getAccessTokenFromWx(Account account){
		try{
			String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ account.getWxappid()+"&secret="+account.getWxappsecret();
			String ret = HttpUtil.get(url, null);
			if(logger.isInfoEnabled()){
				logger.info("Get accesstoken from wx, account={}, ret={}", account, ret);
			}
			JSONObject json = JSONObject.fromObject(ret);
			if(json.has("errcode")){
				logger.error("Get accesstoken from wx, errorcode={}", json.get("errcode"));
				return null;
			}
			String accessToken = json.getString("access_token");
//			int expires_in = json.getInt("expires_in");
//			account.setWxaccesstoken(accessToken);
//			account.setWxtokenexpiretime(expires_in);
//			account.setWxtokenfetchtime((int)(System.currentTimeMillis()/1000));
//			
//			acountService.updateAccessToken(account);
			
			return accessToken;
		}catch(Exception e){
			logger.error("get accesstoken error, ");
			return null;
		}
		
	}
}
