package com.cyhd.service.dao.db.mapper;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import com.cyhd.common.util.Pair;
import com.cyhd.service.dao.po.Device;

/**
 * Description:硬件设备信息映射类
 */
@Repository
public interface DeviceMapper {	
	
	public Device getDeviceById(long id) throws DataAccessException;
	
	public Device existDevice(long userId,String token) throws DataAccessException;
	
	public Device getDeviceByToken(String token) throws DataAccessException;
	
	public void add(Device device) throws DataAccessException;
	
	public void update(Device device) throws DataAccessException;
	
	public List<Device> getDevicesByUserId(long userId) throws DataAccessException;
	
	public List<String> getAllDevices() throws DataAccessException;

    public int updateCityId(String token, int cityid, Date date) throws DataAccessException;

    public Device getDeviceByTokenAndType(String token, int type) throws DataAccessException;

    public Object delete(long id, Date updatetime) throws DataAccessException;
	
    public List<Device> getDevicesByParam(Map<String, Object> param) throws DataAccessException;
    
    public int unbind(String token) throws DataAccessException;
    
    public int clearByUserOrToken(long userid, String token) throws DataAccessException;
    
    public int clearByUser(long userid) throws DataAccessException;
    
    public int clearByToken(String token) throws DataAccessException;
    
    public int countTotal() throws DataAccessException;
    
    public Long[] getAllUserId(int start,int size) throws DataAccessException;
    
    public List<Pair<BigInteger,Long>> getAllUserIdAndId(int start,int size) throws DataAccessException;
    
    public List<Long> getUserIdsByDeviceType(int deviceType, int start, int size);
    
    public List<Long> getUserIdsByDeviceTypeAndAppId(int deviceType, int appId, int start, int size);

}
