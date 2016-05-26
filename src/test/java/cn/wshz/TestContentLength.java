package cn.wshz;

import java.io.FileInputStream;
import java.io.IOException;

public class TestContentLength {

	public static void main(String[] args) {
		FileInputStream inputStream = null;
		try{
			inputStream = new FileInputStream("D:/work/mapi/mapi/src/test/java/cn/wshz/test.txtx");
			byte[] b = new byte[1024];
			int len = 0;
			int length = 0;
			while(( len  = inputStream.read(b ))!= -1){
				length+=len;
				System.out.println(new String(b,0,len));
			}
			System.err.println(length);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
