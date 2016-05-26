package cn.wshz;

import java.io.IOException;

import com.cyhd.service.util.UserAgentUtil;

public class TestAgent {

	public static void main(String[] args) {
		//String userAgent="Mozilla/5.0 (iPhone; CPU iPhone OS 8_4 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12H143 QQ/5.2.0.1 NetType/WIFI Mem/41";
		String userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36";
		UserAgentUtil.UserAgent agent = new UserAgentUtil.UserAgent(userAgent);
		if(agent == null ){
			System.out.println(" is null ");
		}
		
		if(!(agent.isAndroid() || agent.isIPhone() || agent.isIPad())){
			System.out.println(" is not phone");
		}
		
		if(agent.isSafari()){
			System.out.println("safari");
		}
		
		if ( !agent.isWeixin() || agent.isSafari()) {
			try {
				System.out.println(" is null 2");
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
	}
}
