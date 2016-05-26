
package com.cyhd.service.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;

import com.cyhd.common.util.ListUtil;
import com.cyhd.common.util.Pair;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.DeviceMapper;
import com.cyhd.service.dao.po.Device;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.CollectionUtil;
import com.cyhd.service.util.RedisUtil;

@Service
public class DeviceService extends BaseService{
   
	@Resource
	DeviceMapper deviceMapper;
	
	@Resource
	IdMakerService idService;
	
	@Resource(name=CacheUtil.MEMCACHED_RESOURCE)
	CacheDao cacheDao;
	
	@Resource(name=CacheUtil.RAM_LA_RESOURCE)
	CacheDao updateCacheDao;
	@Resource
	private UserService userService ;
	
	@Resource(name=RedisUtil.NAME_ALIYUAN)
	private IJedisDao deviceCacheDao;
	
	//@PostConstruct
	public void initCache(){
		try {
			int count = deviceMapper.countTotal();
			int cacheSize = deviceCacheDao.zcard(RedisUtil.DEVICE_ALL_USERID_KEY).intValue();
			//cache size not equals db
			int sum = 0;
			if(cacheSize < count){
				int size = 10000;
				int num = (count /size)+1;
				for(int i = 0; i <= num; i++){
					List<Pair<BigInteger, Long>> datas = deviceMapper.getAllUserIdAndId(i*size, (i+1)*size);
					if(datas == null){
						break;
					}
					
					sum += datas.size();
					
					Map<String, Double> scoreMembers = new HashMap<String, Double>(size);
					for(int j  = 0 ; j< datas.size(); j++){
						scoreMembers.put(Long.toString(datas.get(j).second), datas.get(j).first.doubleValue());
					}
					
					deviceCacheDao.zadd(RedisUtil.DEVICE_ALL_USERID_KEY, scoreMembers);
					
					if(datas.size() < size){
						break;
					}
				}
				logger.info("同步设备数据完成：共有数据:{}",sum);
			}else{
				logger.info("不用同步设备数据:cache 和 DB 相等",sum);
			}
		} catch (Exception e) {
			logger.info("init cache :{}",e);
		}
	}
	
	public List<Device> getDevicesByUserId(long userId){
		try{
			String key = CacheUtil.getDevicesKey(userId);
			List<Device> devices = null;
			if(devices == null){
				devices = this.deviceMapper.getDevicesByUserId(userId);
				if(devices != null)
					cacheDao.set(key, devices);
			}
			return devices;
		}catch(Exception e){
			logger.error("DeviceService.getDevicesByUserId("+ userId+ ") error", e);
			return null;
		}
	}
	
	public void clearCache(long userId){
		String key = CacheUtil.getDevicesKey(userId);
		cacheDao.remove(key);
		
	}
	/**
	 * 
	 * @param userId, 如果act=bind，userid有效，如果act=unbind，userId无效
	 * @param act bind/unbind
	 * @param token 
	 * @param type
	 * @return
	 */
    public boolean saveDevice(long userId, String act,String token, String deviceType, String deviceModel, String appVer, String ixin_token, String bat_token,String bundle_id){
        try{
        	int type = getDeviceType(deviceType);
           if(act.equals("bind") && userId > 0){
        	   return this.bindDevice(userId, token, type, deviceModel, appVer, ixin_token, bat_token,bundle_id);
           }else{
        	   return this.unBindDevice(token, ixin_token, bat_token);
           }
        }catch(RuntimeException e){
        	logger.error("DeviceService.saveDevice(act="+ act+ ",userid=" + userId + ",token= " + token +") error", e);
        }
        return false;
    }
    
    public boolean bindDevice(long userId, String token, int deviceType, String deviceModel, String appVer, String ixin_token, String bat_token,String bundle_id){
    	saveDeviceTokens(userId, token, deviceType, deviceModel, appVer, ixin_token, bat_token,bundle_id);
        this.clearCache(userId);
        
        //userService.updateUser(userId, null, appVer) ;
        return true;
    }
    
    public boolean unBindDevice(String token, String ixin_token, String bat_token){
    	long userId = 0l;
    	if(!StringUtils.isEmpty(token)){
	    	Device oldDevice = this.deviceMapper.getDeviceByToken(token);
	    	if(oldDevice != null){
	    		if(deviceMapper.unbind(token) > 0){
	    			userId = oldDevice.getUserid();
	    		}
	    	}
    	}
    	if(!StringUtils.isEmpty(ixin_token)){
    		if(userId > 0){
    			deviceMapper.unbind(ixin_token);
    		}else{
		    	Device oldDevice = this.deviceMapper.getDeviceByToken(ixin_token);
		    	if(oldDevice != null){
		    		if(deviceMapper.unbind(ixin_token) > 0){
		    			userId = oldDevice.getUserid();
		    		}
		    	}
    		}
    	}
    	if(!StringUtils.isEmpty(bat_token)){
    		if(userId > 0){
    			deviceMapper.unbind(bat_token);
    		}else{
		    	Device oldDevice = this.deviceMapper.getDeviceByToken(bat_token);
		    	if(oldDevice != null){
		    		if(deviceMapper.unbind(bat_token) > 0){
		    			userId = oldDevice.getUserid();
		    		}
		    	}
    		}
    	}
    	if(userId > 0)
    		this.clearCache(userId);
    	return true;
    }
    
    /**
     * 绑定无用户登陆的设备
     * @param token
     * @return boolean
     */
    public boolean saveDeviceNoUser(String token, String deviceType, String deviceModel, String appVer, String ixin_token, String bat_token,String bundle_id){
        boolean result = true;
        try{
        	result = saveDeviceTokens(0, token, getDeviceType(deviceType), deviceModel, appVer, ixin_token, bat_token,bundle_id);
        }catch(RuntimeException e){
        	logger.error("DeviceService.saveDevice(token) error", e);
        }
        return result;
    }
    
    private boolean saveDeviceTokens(long userId, String token, int deviceType, String deviceModel, String appVer, String ixin_token, String bat_token,String bundle_id){
    	try{
    		if(userId > 0){
    			deviceMapper.clearByUser(userId);
    		}
    		Date date = new Date();
    		long id = 0;
    		if(!StringUtils.isEmpty(token)){
    			Device device = new Device();
    			id = idService.getUniqRandomId();
                device.setId(id);
                device.setToken(token);
                device.setEstate(Constants.ESTATE_Y);
                device.setUserid(userId);
                device.setBundle_id(bundle_id);
                if(deviceType != Constants.platform_ios){
                	//友盟
                	if(token.length() == 44){
                		device.setTokenUM();
                	}else{
                		device.setTokenTypeGetui();
                	}
                }else{
                	device.setTokentype(Device.TOKEN_TYPE_IOS);
                }
                device.setAppver(appVer);
                device.setDevicemodel(deviceModel);
                device.setCreatetime(date);
                device.setDevicetype(deviceType);
                device.setUpdatetime(date);
                this.deviceMapper.add(device);
    		}
    		if(!StringUtils.isEmpty(ixin_token)){
    			Device device = new Device();
    			id = idService.getUniqRandomId();
    			device.setBundle_id(bundle_id);
                device.setId(id);
                device.setEstate(Constants.ESTATE_Y);
                device.setUserid(userId);
                device.setToken(ixin_token);
                device.setTokenTypeIxin();
                device.setAppver(appVer);
                device.setDevicemodel(deviceModel);
                device.setCreatetime(date);
                device.setDevicetype(deviceType);
                device.setUpdatetime(date);
                this.deviceMapper.add(device);
    		}
    		if(!StringUtils.isEmpty(bat_token)){
    			Device device = new Device();
    			device.setBundle_id(bundle_id);
    			id = idService.getUniqRandomId();
                device.setId(id);
                device.setEstate(Constants.ESTATE_Y);
                device.setUserid(userId);
                device.setToken(bat_token);
                device.setTokenTypeTx();
                device.setAppver(appVer);
                device.setDevicemodel(deviceModel);
                device.setCreatetime(date);
                device.setDevicetype(deviceType);
                device.setUpdatetime(date);
                this.deviceMapper.add(device);
    		}
    		if(userId > 0){
    			this.clearCache(userId);
    			//添加到缓存
    			if(id > 0){
    				try{
    					deviceCacheDao.setSet(RedisUtil.DEVICE_ALL_USERID_KEY, Long.toString(userId), id);
    				}catch(Exception e){
    					logger.info("add userId:{},id:{} to devices Cache ,cause by:{}:",userId,id,e);
    				}
    			}
    		}
    		return true;
    	}catch(Exception e){
    		String message = "DeviceService.saveDeviceTokens. userid=" + userId + ", token=" + token +", ixintoken=" + ixin_token + ", bat_token=" + bat_token;
    		logger.error(message + " error", e);
    	}
    	return false;
    }
    
    private static int getDeviceType(String deviceType){
    	boolean ios = deviceType != null && deviceType.equalsIgnoreCase("ios");
    	return ios ? Constants.platform_ios : Constants.platform_android;
    }

    //获取第几页的数据
    //该页数据可以先入缓存中 毕竟数据不变
    public Long[] getUserIdList(int page){
    	
    	int start = (page-1)*Constants.DEFAULT_DEVICE_PAGE_SIZE;
    	int stop = (page)*Constants.DEFAULT_DEVICE_PAGE_SIZE;
    	
    	Set<String> value;
		try {
			value = deviceCacheDao.zrevrange(RedisUtil.DEVICE_ALL_USERID_KEY, start, stop);
			if(value != null && value.size() > 0){
				return CollectionUtil.collectionToArray(value);
			}
		} catch (Exception e) {
			logger.error("get userids for cache :cause by:{}",e);
		}
		logger.warn("get device data from db");
		//redis 的实现不一样 Java是不包含尾 redis是包含的 所以尾部要加1
		return deviceMapper.getAllUserId(start, Constants.DEFAULT_DEVICE_PAGE_SIZE);
    
    }
    
    public int countAllUser(){
    	return deviceMapper.countTotal();
    }
    
    public List<Long> getDeviceUsers(int type, int start, int size){
    	return deviceMapper.getUserIdsByDeviceType(type, start, size);
    }
    
    public List<Long> getIosUsers(int start, int size){
    	return deviceMapper.getUserIdsByDeviceType(Constants.platform_ios, start, size);
    }
    
    public List<Long> getAndroidUsers(int start, int size){
    	return deviceMapper.getUserIdsByDeviceType(Constants.platform_android, start, size);
    }
    
    public List<Long> getIosNotAcceptUsers(int appId, int start, int size){
    	return deviceMapper.getUserIdsByDeviceTypeAndAppId(Constants.platform_ios, appId, start, size);
    }

//    /**
//     * @param token
//     * @param cityid
//     * @return
//     */
//    public int updateCityId(String token, int cityid) {
//        return deviceMapper.updateCityId(token, cityid, new Date());
//    }
    
}
