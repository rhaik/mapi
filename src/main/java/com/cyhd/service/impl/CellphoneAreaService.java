package com.cyhd.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import org.springframework.stereotype.Service;

import com.cyhd.common.util.HttpUtil;

import net.sf.json.JSONObject;

@Service
public class CellphoneAreaService extends BaseService {

	private static final String API_URL = "http://a.apix.cn/apixlife/phone/phone?phone=";
	private static Map<String, String> requestHeader = new HashMap<>(3);
	static{
		requestHeader.put("accept", "application/json");
		requestHeader.put("content-type", "application/json");
		requestHeader.put("apix-key", "c0938cf180a74a5366311452c4125d9b");
	}
	private static final Map<String, String> paramsMap = new HashMap<String, String>(1);

	private static CacheLRULiveAccessDaoImpl<CellPhoneArea> cellPhoneAreaCache = new CacheLRULiveAccessDaoImpl<>(200);
	
	public CellPhoneArea getCellPhoneArea(String phone){
		CellPhoneArea area = cellPhoneAreaCache.get(phone);
		if (area == null) {
			try {
				String respponse = HttpUtil.get(API_URL + phone, requestHeader, paramsMap);

				JSONObject json = JSONObject.fromObject(respponse);
				json = json.getJSONObject("data");

				area = new CellPhoneArea();
				area.setCity(json.getString("city"));
				area.setOperator(json.getString("operator"));
				area.setProvince(json.getString("province"));
				area.setTelephone(phone);

				cellPhoneAreaCache.set(phone, area);

				return area;
			} catch (Exception e) {
				logger.error("get cellphone area ,phone :{},cause :", phone, e);
			}
		}
		return area;
	}
	public static class CellPhoneArea implements Serializable{
		
		private String province;
		private String operator;
		private String telephone;
		private String city;
		
		public String getProvince() {
			return province;
		}
		public void setProvince(String province) {
			this.province = province;
		}
		public String getOperator() {
			return operator;
		}
		public void setOperator(String operator) {
			this.operator = operator;
		}
		public String getTelephone() {
			return telephone;
		}
		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}
		public String getCity() {
			return city;
		}
		public void setCity(String city) {
			this.city = city;
		}
		
		@Override
		public int hashCode() {
			int code = 0;
			if(province != null){
				code = province.hashCode()* 31;
			}
			if(city != null){
				code += city.hashCode();
			}
			return code ;
		}
		
		@Override
		public boolean equals(Object obj) {
			boolean equals = false;
			if(obj == null){
				return false;
			}
			CellPhoneArea area = (CellPhoneArea) obj;
			if(area.getProvince() != null){
				equals = area.getProvince().equals(this.getProvince());
			}
			if(equals && area.getCity() != null){
				equals = area.getCity().equals(this.getCity());
			}
			return equals;
		}

		@Override
		public String toString() {
			return "{" +
					"省='" + province + '\'' +
					", 运营商='" + operator + '\'' +
					", 号码='" + telephone + '\'' +
					", 城市='" + city + '\'' +
					'}';
		}
	}
}
