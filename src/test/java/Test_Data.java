import java.net.URLDecoder;
import java.util.Map;

import com.cyhd.common.util.Helper;
import com.cyhd.web.common.util.AESCoder;


public class Test_Data {

	public static void main(String[] args) throws Exception {
		String _data="5NMHGmP%2F2Rt57TQqJEtBT2CBEcpF0BQzBd0lsQAou1krEyzKv%2BW3LmTd3zOZ2GB9dJ%2BJwX9nMVRm%0A%2FvLft3u7%2FA%3D%3D%0A";
		//有时候需要转两次
		String contents = URLDecoder.decode(_data, "utf-8");
		contents = URLDecoder.decode(contents, "utf-8"); 
		
		System.out.println(contents);
		String sign = "587207c12255156fa682d84a623a5399";
		String cKey = "mzdq"+sign.substring(sign.length()-12);
		String s = AESCoder.decrypt(contents, cKey);
		if(s != null){
			//s = URLDecoder.decode(s, "utf-8");
			Map mp = Helper.getEncodedUrlParams(s);
			System.out.println(mp);
		}
	}
}
