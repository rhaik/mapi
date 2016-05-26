package com.cyhd.service.util;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.StringUtil;
import com.cyhd.common.util.structure.LRUCache;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class IpAddressUtil {

	public static String SINA_IP_URL = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=Json";

	/**
	 * 缓存最近访问的ip地址
	 */
	private static LRUCache<String, String> IP_AREA_CACHE = new LRUCache<>(10, 100);

	//初始化IpIp数据库
	static {
		IpIpUtil.load(IpAddressUtil.class.getResource("/ipdata/17monipdb.dat").getFile());
	}

	/**
	 * 使用新浪的接口获取IP地址信息
	 * @param ip
	 * @return
	 */
	public static String getAddressFromSina(String ip) {
		Map<String, String> params = new HashMap<>();
		params.put("ip", ip); 
	
		String rs = null;
		try {
			rs = HttpUtil.get(SINA_IP_URL, params);
			if(StringUtil.isNotBlank(rs)) {
				JSONObject json = new JSONObject(rs);  
				return json.optString("country") + " " + json.optString("province") + " " + json.optString("city");
			} 
		} catch(Exception e) {
			
		}
		return rs;
	}

	/**
	 * 使用本地的IpIp数据库获取IP地址信息
	 * @param ip
	 * @return
	 */
	public static String getAddressLocally(String ip){
		StringBuilder sb = new StringBuilder();
		try {
			String[] areas = IpIpUtil.find(ip);
			if (areas != null) {
				String last = "";
				for (String area : areas) {
					//避免出现“美国美国”这样的数据
					if (area != null && !area.equals(last)) {
						if (sb.length() > 0){
							sb.append(" ");
						}
						sb.append(area);
					}
					last = area;
				}
			}
		}catch (Exception exp){

		}
		return sb.toString().trim();
	}


	/**
	 * 获取IP地址信息，先用本地，获取不到再用新浪的接口<br/>
	 * 使用了LRU缓存
	 * @param ip
	 * @return
	 */
	public static String getAddress(String ip){
		String addr = IP_AREA_CACHE.get(ip);
		if (addr == null) {
			addr = getAddressLocally(ip);
			if (addr == null || addr.trim().isEmpty()) {
				addr = getAddressFromSina(ip);
			}
			IP_AREA_CACHE.put(ip, addr);
		}
		return addr;
	}


	public static void main(String[] args) {
		System.out.println(IpAddressUtil.getAddressFromSina("114.215.130.131"));
	}




	/**
	 * 读取IP数据的内部类，参考：https://github.com/17mon/java/blob/master/IP.java <br/>
	 * IP数据来自：https://www.ipip.net/download.html
	 */
	static class  IpIpUtil {

		public static boolean enableFileWatch = false;

		private static int offset;
		private static int[] index = new int[256];
		private static ByteBuffer dataBuffer;
		private static ByteBuffer indexBuffer;
		private static Long lastModifyTime = 0L;
		private static File ipFile;
		private static ReentrantLock lock = new ReentrantLock();

		public static void load(String filename) {
			ipFile = new File(filename);
			load();
			if (enableFileWatch) {
				watch();
			}
		}

		public static void load(String filename, boolean strict) throws Exception {
			ipFile = new File(filename);
			if (strict) {
				int contentLength = Long.valueOf(ipFile.length()).intValue();
				if (contentLength < 512 * 1024) {
					throw new Exception("ip data file error.");
				}
			}
			load();
			if (enableFileWatch) {
				watch();
			}
		}

		public static String[] find(String ip) {
			int ip_prefix_value = new Integer(ip.substring(0, ip.indexOf(".")));
			long ip2long_value = ip2long(ip);
			int start = index[ip_prefix_value];
			int max_comp_len = offset - 1028;
			long index_offset = -1;
			int index_length = -1;
			byte b = 0;
			for (start = start * 8 + 1024; start < max_comp_len; start += 8) {
				if (int2long(indexBuffer.getInt(start)) >= ip2long_value) {
					index_offset = bytesToLong(b, indexBuffer.get(start + 6), indexBuffer.get(start + 5), indexBuffer.get(start + 4));
					index_length = 0xFF & indexBuffer.get(start + 7);
					break;
				}
			}

				byte[] areaBytes;

				lock.lock();
				try {
					dataBuffer.position(offset + (int) index_offset - 1024);
					areaBytes = new byte[index_length];
					dataBuffer.get(areaBytes, 0, index_length);
				} finally {
					lock.unlock();
				}

				return new String(areaBytes, Charset.forName("UTF-8")).split("\t", -1);
			}

			private static void watch() {
				Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
					@Override
					public void run() {
						long time = ipFile.lastModified();
						if (time > lastModifyTime) {
							lastModifyTime = time;
							load();
						}
					}
				}, 1000L, 5000L, TimeUnit.MILLISECONDS);
			}

			private static void load() {
				lastModifyTime = ipFile.lastModified();
				FileInputStream fin = null;
				lock.lock();
				try {
					dataBuffer = ByteBuffer.allocate(Long.valueOf(ipFile.length()).intValue());
					fin = new FileInputStream(ipFile);
					int readBytesLength;
					byte[] chunk = new byte[4096];
					while (fin.available() > 0) {
						readBytesLength = fin.read(chunk);
						dataBuffer.put(chunk, 0, readBytesLength);
					}
					dataBuffer.position(0);
					int indexLength = dataBuffer.getInt();
					byte[] indexBytes = new byte[indexLength];
					dataBuffer.get(indexBytes, 0, indexLength - 4);
					indexBuffer = ByteBuffer.wrap(indexBytes);
					indexBuffer.order(ByteOrder.LITTLE_ENDIAN);
					offset = indexLength;

					int loop = 0;
					while (loop++ < 256) {
						index[loop - 1] = indexBuffer.getInt();
					}
					indexBuffer.order(ByteOrder.BIG_ENDIAN);
				} catch (IOException ioe) {
					ioe.printStackTrace();
				} finally {
					try {
						if (fin != null) {
							fin.close();
						}
					} catch (IOException e){
						e.printStackTrace();
					}
					lock.unlock();
				}
			}

			private static long bytesToLong(byte a, byte b, byte c, byte d) {
				return int2long((((a & 0xff) << 24) | ((b & 0xff) << 16) | ((c & 0xff) << 8) | (d & 0xff)));
			}

			private static int str2Ip(String ip)  {
				String[] ss = ip.split("\\.");
				int a, b, c, d;
				a = Integer.parseInt(ss[0]);
				b = Integer.parseInt(ss[1]);
				c = Integer.parseInt(ss[2]);
				d = Integer.parseInt(ss[3]);
				return (a << 24) | (b << 16) | (c << 8) | d;
			}

			private static long ip2long(String ip)  {
				return int2long(str2Ip(ip));
			}

			private static long int2long(int i) {
				long l = i & 0x7fffffffL;
				if (i < 0) {
					l |= 0x080000000L;
				}
				return l;
			}
		}

}
