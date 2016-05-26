package com.cyhd;

import java.util.Map;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.service.util.EmojUtil;

public class TestHttp {

	public static void main(String[] args) {
		String url = "http://wall.imopan.com/app/go.jsp?source=miaozhuan&appid=xunyiwenyao&&idfa=62CA45F9-A64D-4F04-88A8-00C79C3BA3E6&clientIp=134.123.31.32";
		//String url = "http://wall.imopan.com/app/go.jsp?source=mopan&appid=950918862";
		try {
			//String respone = HttpUtil.get(url, null);
//			Map<String, String> respone = HttpUtil.getHttpStatus(url, null);
//			System.out.println(respone);
			System.out.println(EmojUtil.removeEmoj("aaha周峰"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
