package com.cyhd.service.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cyhd.service.dao.po.Ref;

/**
 * @author www.99dou.com
 * 
 */
public class Recharfe99douInterfaceUtil {
	private String partner = "";
	private String key = "";
	private String host = "";
	private String notify_url = "";
	private static String CHAR_SET = "UTF-8";
	private static Logger log = LoggerFactory.getLogger("recharge");
	public Recharfe99douInterfaceUtil() {
		this.host = "http://api2.99dou.com:8000/Api/Service";
		this.partner = "10705";
		this.key = "wfwy77RZPLzZXiEM7fS0SKFrtBW97B5ilEavIQr5uKfRFS0yrWH3XjmqPgJxaoGY";
		this.notify_url = GlobalConfig.base_url+"web/recharge/notify.3w";
	}

	/**
	 * 话费充值
	 * 
	 * @param out_trade_id
	 *            代理商交易号 唯一
	 * @param account
	 *            手机号 或者固话 如 075533377311
	 * @param account_info
	 *            账户信息:如，类型:固话\n营运商:联通
	 * @param quantity
	 *            数量，当数量大于1时有可能产生部分到账
	 * @param value
	 *            面额
	 * @param client_ip
	 *            客户端请求ip
	 * @param expired_mini
	 *            过期时间，如果超过改时间仍未进行充值，则自动退款
	 * @param msg
	 *            返回消息
	 * @return 0 成功 1 失败 -1 未知
	 */
	public int Huafei(String out_trade_id, String account, String account_info,
			int quantity, String value, String client_ip, int expired_mini,
			Ref<String> msg) {
		Map<String, String> pars = new HashMap<String, String>();

		pars.put("format", "xml");

		// /充值手机号
		pars.put("account", account);

		// /账户信息 如果为固话或者小灵通充值时 必须输入 运营商:电信
		pars.put("account_info", account_info);

		// 充值面额
		pars.put("value", value);

		// 充值数量
		pars.put("quantity", "" + quantity);

		// 订单超过指定时
		pars.put("expired_mini", "" + expired_mini);

		// /客户端调用ip
		pars.put("client_ip", client_ip);

		// 接收久久订单状态通知的地址 这个必须为公网网址
		pars.put("notify_url", notify_url);

		// /代理商订单序号
		pars.put("out_trade_id", out_trade_id);

		try {
			log.info("Huafei request params {}", pars.toString());
			String result = Post("Huafei", pars);
			log.info("Huafei request return {}", result);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(result
					.getBytes(CHAR_SET)));

			Element response = doc.getDocumentElement();
			Map<String, String> resultMap = new HashMap<String, String>();
			NodeList childs = response.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);
				resultMap.put(child.getNodeName(), child.getTextContent());
			}
			String code = resultMap.containsKey("code") ? resultMap.get("code")
					: "";

			msg.setValue(resultMap.containsKey("msg") ? resultMap.get("msg")
					: "");

			if (code.equals("0000") || code.equals("0004")) // 订单提交成功 || 订单重复
															// 建议做提交成功处理
			{
				if (!VerifySign(resultMap))
					throw new Exception("返回数据校验错误");
				return 0;
			} else if (code == "-1") {
				return -1;
			}
			return 1;
		} catch (Exception ex) {
			msg.setValue(ex.getMessage());
			return -1;
		}
	}

	/**
	 * 流量充值
	 * @param out_trade_id 代理订单编号，唯一
	 * @param account 手机号
	 * @param quantity 数量
	 * @param size 流量大小 如 10M 500M 1G 等
	 * @param type 0 全国  1 省内
	 * @param client_ip
	 * @param expired_mini
	 * @param msg
	 * @return
	 */	
	public int Liuliang(String out_trade_id, String account, int quantity,
			String size, int type, String client_ip, int expired_mini,
			Ref<String> msg) {
		Map<String, String> pars = new HashMap<String, String>();

		pars.put("format", "xml");

		// /充值手机号
		pars.put("account", account);

		// 流量大小 带单位
		pars.put("size", size);

		//
		pars.put("type", type + "");

		// 充值数量
		pars.put("quantity", quantity + "");

		// 订单超过指定时
		pars.put("expired_mini", expired_mini + "");

		// /客户端调用ip
		pars.put("client_ip", client_ip);

		// 接收久久订单状态通知的地址 这个必须为公网网址
		pars.put("notify_url", notify_url);

		// /代理商订单序号
		pars.put("out_trade_id", out_trade_id);

		try {
			String result = Post("Liuliang", pars);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(result
					.getBytes(CHAR_SET)));

			Element response = doc.getDocumentElement();
			Map<String, String> resultMap = new HashMap<String, String>();
			NodeList childs = response.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);
				resultMap.put(child.getNodeName(), child.getTextContent());
			}
			String code = resultMap.containsKey("code") ? resultMap.get("code")
					: "";

			msg.setValue(resultMap.containsKey("msg") ? resultMap.get("msg")
					: "");

			if (code.equals("0000") || code.equals("0004")) // 订单提交成功 || 订单重复
			// 建议做提交成功处理
			{
				if (!VerifySign(resultMap))
					throw new Exception("返回数据校验错误");
				return 0;
			} else if (code == "-1") {
				return -1;
			}
			return 1;
		} catch (Exception ex) {
			msg.setValue(ex.getMessage());
			return -1;
		}
	}

	public String MobileQuery(String account, Ref<String> msg) {
		Map<String, String> pars = new HashMap<String, String>();
		pars.put("format", "xml");

		// /充值手机号
		pars.put("account", account);

		try {
			String result = Post("MobileQuery", pars);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(result
					.getBytes(CHAR_SET)));

			Element response = doc.getDocumentElement();
			Map<String, String> resultMap = new HashMap<String, String>();
			NodeList childs = response.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);
				resultMap.put(child.getNodeName(), child.getTextContent());
			}
			String code = resultMap.containsKey("code") ? resultMap.get("code")
					: "";

			msg.setValue(resultMap.containsKey("msg") ? resultMap.get("msg")
					: "");

			if (code.equals("0000")) {
				if (!VerifySign(resultMap))
					throw new Exception("返回数据校验错误");
				return resultMap.containsKey("account_info") ? resultMap
						.get("account_info") : "";

			}
			return "";
		} catch (Exception ex) {
			msg.setValue(ex.getMessage());
			return "";
		}
	}

	private boolean VerifySign(Map<String, String> pars) {
		if (pars.containsKey("_sign")
				&& pars.get("_sign").equalsIgnoreCase(
						Build_mysign(pars, this.key, "utf-8")))
			return true;
		return false;
	}

	/**
	 * 直充，根据商品序号进行充值 ，可充话费，固话，QB,网游
	 * 
	 * @param out_trade_id
	 * @param product_id
	 *            商品序号，可以在代理商后台获取商品序号，如Q币的商品序号为 10316
	 * @param quantity
	 *            充值数量
	 * @param account
	 *            充值账户 话费时输入手机号，Q服务输入QQ号，网游时输入网游账户 如：1549332002
	 * @param account_info
	 *            账户信息，没有可不填,如 游戏区服：
	 * @param client_ip
	 *            客户端请求ip
	 * @param expired_mini
	 *            过期时间，如果超过改时间仍未进行充值，则自动退款
	 * @param msg
	 *            返回消息
	 * @return 0 成功 1 失败 -1 未知
	 */
	public int Direct(String out_trade_id, String product_id, int quantity,
			String account, String account_info, String client_ip,
			int expired_mini, Ref<String> msg) {
		Map<String, String> pars = new HashMap<String, String>();

		pars.put("format", "xml");

		// /充值手机号
		pars.put("account", account);

		// /账户信息 如果为固话或者小灵通充值时 必须输入 运营商:电信
		pars.put("account_info", account_info);

		// 充值面额
		pars.put("product_id", product_id);

		// 充值数量
		pars.put("quantity", "" + quantity);

		// 订单超过指定时
		pars.put("expired_mini", "" + expired_mini);

		// /客户端调用ip
		pars.put("client_ip", client_ip);

		// 接收久久订单状态通知的地址 这个必须为公网网址
		pars.put("notify_url", notify_url);

		// /代理商订单序号
		pars.put("out_trade_id", out_trade_id);

		try {
			String result = Post("Direct", pars);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(result
					.getBytes(CHAR_SET)));

			Element response = doc.getDocumentElement();
			Map<String, String> resultMap = new HashMap<String, String>();
			NodeList childs = response.getChildNodes();
			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);
				resultMap.put(child.getNodeName(), child.getTextContent());
			}

			String code = resultMap.containsKey("code") ? resultMap.get("code")
					: "";

			msg.setValue(resultMap.containsKey("msg") ? resultMap.get("msg")
					: "");

			if (code.equals("0000") || code.equals("0004")) // 订单提交成功 || 订单重复
															// 建议做提交成功处理
			{
				if (!VerifySign(resultMap))
					throw new Exception("返回数据校验错误");
				return 0;
			} else if (code == "-1") {
				return -1;
			}
			return 1;
		} catch (Exception ex) {
			msg.setValue(ex.getMessage());
			return -1;
		}
	}

	/**
	 * 查询订单状态
	 * 
	 * @param out_trade_id
	 *            代理商交易号
	 * @param success_qty
	 *            返回，成功数量
	 * @param fail_qty
	 *            返回 失败数量
	 * @param msg
	 *            返回错误消息
	 * @return 0 正确返回， 根据 success_qty,fail_qty 判断成功数据 ; 1 订单进行中 2 失败 订单不存在 -1
	 *         状态未知
	 */
	public int Query(String out_trade_id, Ref<Integer> success_qty,
			Ref<Integer> fail_qty, Ref<String> msg) {
		Map<String, String> pars = new HashMap<String, String>();

		pars.put("format", "xml");

		// /代理商订单序号
		pars.put("out_trade_id", out_trade_id);
		try {
			String result = Post("Query", pars);

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new ByteArrayInputStream(result
					.getBytes(CHAR_SET)));

			Element response = doc.getDocumentElement();
			Map<String, String> resultMap = new HashMap<String, String>();
			NodeList childs = response.getChildNodes();

			for (int i = 0; i < childs.getLength(); i++) {
				Node child = childs.item(i);
				resultMap.put(child.getNodeName(), child.getTextContent());
			}

			String code = resultMap.containsKey("code") ? resultMap.get("code")
					: "";

			msg.setValue(resultMap.containsKey("msg") ? resultMap.get("msg")
					: "");

			if (code.equals("0000")) {
				if (!VerifySign(resultMap))
					throw new Exception("返回数据校验错误");
				String status = resultMap.containsKey("status") ? resultMap
						.get("status") : "";

				if (status.equals("success") || status.equals("fail")) {
					success_qty.setValue(Integer.parseInt(resultMap
							.containsKey("success_qty") ? resultMap
							.get("success_qty") : ""));

					fail_qty.setValue(Integer.parseInt(resultMap
							.containsKey("fail_qty") ? resultMap
							.get("fail_qty") : ""));
					return 0;
				} else {
					msg.setValue(status);
					return 1;
				}
			} else if (code.equals("0012")) // 订单不存在
			{
				if (!VerifySign(resultMap))
					throw new Exception("返回数据校验错误");

				return 2;
			}
			return -1;
		} catch (Exception ex) {
			msg.setValue(ex.getMessage());
			return -1;
		}
	}

	/**
	 * 获取客户端IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		if (request == null) {
			try {
				return InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			return null;
		}
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 验证消息通知
	 * 
	 * @param map
	 * @param out_trade_id
	 * @param success_qty
	 * @param fail_qty
	 * @param msg
	 * @return
	 */
	public int VerifyNotify(Map<String, String> map, Ref<String> out_trade_id,
			Ref<Integer> success_qty, Ref<Integer> fail_qty, Ref<String> msg) {
		String newSign = Build_mysign(map, this.key, CHAR_SET);
		if (map.containsKey("_sign")
				&& newSign.equalsIgnoreCase(map.get("_sign"))) {

			out_trade_id.setValue(map.containsKey("out_trade_id") ? map
					.get("out_trade_id") : "");

			String status = map.containsKey("status") ? map.get("status") : "";

			if (status.equals("success") || status.equals("fail")) {
				success_qty.setValue(Integer.parseInt(map
						.containsKey("success_qty") ? map.get("success_qty")
						: ""));
				fail_qty.setValue(Integer.parseInt(map.containsKey("fail_qty") ? map
						.get("fail_qty") : ""));

				return 0;
			}
			msg.setValue("未知状态:" + status);
		} else {
			msg.setValue("校验失败");
		}
		return -1;
	}

	private String Post(String method, Map<String, String> pars)
			throws Exception {
		pars.put("partner", partner);
		pars.put("method", method);
		pars.put("sign_type", "md5");

		String sign = Build_mysign(pars, key, CHAR_SET);
		pars.put("_sign", sign);
		String query = Create_linkString(pars, CHAR_SET);

		String url = host;

		if (url.indexOf("?") != -1)
			url += "&_charset=" + CHAR_SET;
		else
			url += "?_charset=" + CHAR_SET;

		return PostRequest(url, query, CHAR_SET);
	}

	private final static String PostRequest(String url, String param,
			String _input_charset) throws Exception {
		OutputStreamWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			URL realUrl = new URL(url);
			// 打开和URL之间的连接
			HttpURLConnection conn = (HttpURLConnection) realUrl
					.openConnection();
			conn.setRequestMethod("POST");
			// 设置通用的请求属性
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("content-type",
					"application/x-www-form-urlencoded;charset="
							+ _input_charset);
			// 发送POST请求必须设置如下两行
			conn.setDoOutput(true);
			conn.setDoInput(true);
			// 获取URLConnection对象对应的输出流

			out = new OutputStreamWriter(conn.getOutputStream(), _input_charset);
			// 发送请求参数
			out.write(param);
			// flush输出流的缓冲
			out.flush();
			// 定义BufferedReader输入流来读取URL的响应
			in = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), _input_charset));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			throw e;
		}
		// 使用finally块来关闭输出流、输入流
		finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @param dicArray
	 *            要签名的数组
	 * @param key
	 *            安全校验码
	 * @param _input_charset
	 *            编码格式
	 * @return Build_mysign
	 */
	public String Build_mysign(Map<String, String> dicArray, String key,
			String _input_charset) {
		dicArray = mapSortByKey(dicArray);
		StringBuilder prestr = new StringBuilder();
		for (String name : dicArray.keySet()) {
			if (name.startsWith("_"))
				continue;
			String value = dicArray.get(name);
			if (value == null || value.isEmpty())
				continue;
			prestr.append("&" + name + "=" + value);
		}
		prestr.delete(0, 1);
		// 把拼接后的字符串再与安全校验码直接连接起来
		String mysign = getMD5(prestr.toString() + key, _input_charset); // 把最终的字符串签名，获得签名结果

		return mysign;
	}

	private final static SortedMap<String, String> mapSortByKey(
			Map<String, String> unsort_map) {
		TreeMap<String, String> result = new TreeMap<String, String>();

		Object[] unsort_key = unsort_map.keySet().toArray();
		Arrays.sort(unsort_key);

		for (int i = 0; i < unsort_key.length; i++) {
			result.put(unsort_key[i].toString(), unsort_map.get(unsort_key[i]));
		}
		return result.tailMap(result.firstKey());
	}

	private final static String getMD5(String s, String charset) {
		// 16进制下数字到字符的映射数组
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes(charset);
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String Create_linkString(Map<String, String> dicArray,
			String _input_charset) {
		StringBuilder prestr = new StringBuilder();
		try {
			for (String key : dicArray.keySet()) {
				String value = dicArray.get(key);
				if (value == null || value.isEmpty())
					continue;
				prestr.append("&" + key + "="
						+ URLEncoder.encode(value, _input_charset));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (prestr.length() > 0)
			return prestr.substring(1);
		return prestr.toString();
	}
	public static void main(String[] args){
		Recharfe99douInterfaceUtil dou = new Recharfe99douInterfaceUtil();
		Ref<String> msg = new Ref<String>();
		String rs = dou.MobileQuery("13681545052", msg);
		System.out.println(msg);
		System.out.println(rs);
	}
} 


