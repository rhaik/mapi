package cn.wshz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.cyhd.common.util.AesCryptUtil;
import com.cyhd.common.util.Base64;
import com.cyhd.service.constants.Constants;

public class TestStream {

	public static void main(String[] args) throws UnsupportedEncodingException {
		String url ="https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxdbf4211400463a5e&redirect_uri=https%3A%2F%2Fwww.mapi.lieqicun.cn%2Fwww%2Fnew_user%2Farticle%2Fauth%3Facc%3D64e6806c14e65ae3028280428382140183c3420302c18283b507c17abb9b1c4f%26u_id%3Dcts4Kz8wPQdhSKns8TnV%26taskid%3DcSmzLL%2F0KA%3D%3D&response_type=code&scope=snsapi_userinfo&state=5&isWeiXin=true#wechat_redirect";
		System.out.println(URLDecoder.decode(url, "utf-8"));
		
		String data="cyv4745 IyFuNZMW7l5Fcw==";
		String data2 = AesCryptUtil.encrypt("85182104", Constants.ARTICLE_AES_PASSWORD);
		System.out.println(data2);
		System.out.println(AesCryptUtil.encrypt("85182104", Constants.ARTICLE_AES_PASSWORD));
		System.out.println(AesCryptUtil.encrypt("85182104", Constants.ARTICLE_AES_PASSWORD));
		System.out.println(AesCryptUtil.encrypt("85182104", Constants.ARTICLE_AES_PASSWORD));
		//cpnUwC1FhU4gYvHyP0to
		//crtbSyiU17l0EhWE/n3m
		byte[] dataByte = Base64.decode("csaY95h BU4scGVXmxPk1");
		System.out.println(AesCryptUtil.decrypt("734f16ee4ad62f846dc7f5c2d326e6eb",Constants.ARTICLE_AES_PASSWORD));
		
	}

}
