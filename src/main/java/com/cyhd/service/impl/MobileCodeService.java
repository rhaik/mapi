package com.cyhd.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.db.mapper.MobileCodeMapper;
import com.cyhd.service.dao.po.MobileCode;

/**
 */
@Service
public class MobileCodeService extends BaseService{
	
	@Resource
	private MobileCodeMapper mobileCodeMapper;
	
	@Resource
	private SmsService smsService;
	
	@Resource
	private AutoCallService autoCallService;
	
	/**
	 * 校验手机与验证码
	 * @return
	 */
	public boolean validMobileAndCode(String mobile,String code){
		boolean result = false;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("mobile", mobile);
		map.put("estate", Constants.ESTATE_Y);
		MobileCode mc = mobileCodeMapper.getByParam(map);
		if(mc != null){
			if((System.currentTimeMillis() - mc.getCreatetime().getTime()) >  Constants.VERCODE_TTL){
				this.deleteCodeById(mc.getId());
				result = false;
			}else if(code.equals(mc.getCode())){
				result = true;
				//将此验证码设为无效
                this.deleteCodeById(mc.getId());
			}else{
				if(mc.getValidnum() < MobileCode.CODE_VALID_NUM - 1){
	                mc.setValidnum(mc.getValidnum()+1);
	                this.codeValidnumPlus(mc.getId(),mc.getValidnum());  
	            }else{//验证超过3次，进行删除
	                this.deleteCodeById(mc.getId());
	            }  
			}
		}
		return result;
	}

	/**
     * 生成短信验证码
     * @param mobile
     * @param createtime
     * @return
     */
    public String makeMobileCode(String mobile){
        try{
        	Date createtime = new Date();
            long time = createtime.getTime(); 
       
            StringBuffer codeBuffer = new StringBuffer() ;
            Random random=new Random(time); 
            for(int i=1;i<=MobileCode.CODE_COUNT;i++){
                codeBuffer.append(MobileCode.CODESE_QUENCE[random.nextInt(10)]);
            }
            String code = codeBuffer.toString();
            MobileCode mobileCode = new MobileCode( mobile, code,  createtime, Constants.ESTATE_Y , 0);
            mobileCodeMapper.insert(mobileCode);
            return code;
        }catch(DataAccessException e){
			logger.error("makeMobileCode(String "+mobile, e);
            throw e;
        }
    }
    
    /**
     * 发送短信验证码
     * @param mobile
     * @param createtime
     * @return
     */
    public boolean sendMobileCode(String mobile){
    	try {
        	String code = this.makeMobileCode(mobile);
        	
        	if(code != null ){
        		return autoCallService.loginAuthCode(mobile, code);
        	}
		} catch (Exception e) {
			logger.error("sendMobileCode(String "+mobile+" )", e);
		}
    	return false;
    }
    /**
     * 校验次数+1
     * @param map
     * @return
     */
    public boolean codeValidnumPlus(long id,int validnum){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("id", id);
    	map.put("validnum", validnum);
    	return mobileCodeMapper.update(map) >= 1;
    }
    
    /**
     * 逻辑删除
     * @param id
     * @return
     */
    public boolean deleteCodeById(long id){
    	Map<String, Object> map = new HashMap<String, Object>();
    	map.put("id", id);
    	map.put("estate", Constants.ESTATE_N);
    	return mobileCodeMapper.update(map) >= 1;
    }
}

