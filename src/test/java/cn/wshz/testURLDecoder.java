package cn.wshz;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class testURLDecoder {

	public static void main(String[] args) throws Exception {
		//String data = URLDecoder.decode("https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxd9542c0eb613a9cc&redirect_uri=https%3A%2F%2Fwww.mapi.lieqicun.cn%2F%2Fwechat%2Fauth%3Facc%3D1%26userinfo%3D1%26url%3Dhttps%253A%252F%252Fwww.mapi.lieqicun.cn%252Fstatic%252Fhtml%252Fhelp%252Fqa.html&response_type=code&scope=snsapi_base&state=1#wechat_redirect","utf-8");
		String data = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx196c0c2042f9f0d5&redirect_uri=https%3A%2F%2Fwww.mapi.lieqicun.cn%2Fwechat%2Fauth%3Facc%3D4%26userinfo%3D362%26taskid%3D12%26a_id%3Db24bc442164a60db824082c14100b0c283c30303c2c0830337c72ba2ae3d113b%26url%3Dhttps%253A%252F%252Fwww.mapi.lieqicun.cn%252Fstatic%252Fhtml%252Fhelp%252Fqa.html&response_type=code&scope=snsapi_base&state=4#wechat_redirect";
		data = URLDecoder.decode(data,"utf-8");
		data = URLDecoder.decode(data,"utf-8");
		System.out.println(data);
	//System.out.println(URLEncoder.encode("https://www.mapi.lieqicun.cn/static/html/help/qa.html", "utf-8"));
	System.out.println(URLEncoder.encode("https://www.mapi.lieqicun.cn/wechat/auth?acc=4&userinfo=362&taskid=12&a_id=b24bc442164a60db824082c14100b0c283c30303c2c0830337c72ba2ae3d113b&url=", "utf-8"));
	System.out.println(URLEncoder.encode("http://www.baidu.com/s?wd=我是帅哥吗", "utf-8"));
	}
}
//https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx69cedca769fcd132&redirect_uri=https%3A%2F%2Fwww.mapi.lieqicun.cn%2Fstatic%2Fhtml%2Fhelp%2Fqa.html&response_type=code&scope=snsapi_base&state=1#wechat_redirect
//https%3A%2F%2Fwww.mapi.lieqicun.cn%2Fstatic%2Fhtml%2Fhelp%2Fqa.html
