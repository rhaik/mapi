package com.cyhd.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.apache.commons.httpclient.methods.multipart.*;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	
	private static Logger logger = LoggerFactory.getLogger("webcall");
	
	private static final String default_charaset="utf-8";
	
	private static final int default_connect_timeout = 5000;
	private static final int default_socket_timeout = 5000;
	private static final int default_retries = 1;
	
	private static HttpClient makeHttpClient(int socketTimeout, int connectTimeout, int retries, String charset){
		HttpClient client = new HttpClient();
        HttpClientParams clientParams = client.getParams();
        clientParams.setParameter("http.socket.timeout", socketTimeout); 
        clientParams.setParameter("http.connection.timeout", connectTimeout); 
        // connection建立超时
        clientParams.setParameter("http.connection-manager.timeout", new Long(socketTimeout));
        clientParams.setParameter("http.method.retry-handler",
                new DefaultHttpMethodRetryHandler(retries, false)); // 如果Http出错，三次重试
        
        clientParams.setContentCharset(charset);//设置请求编码
        return client;
	}
	public static String postByForm(String url, Map<String,String> params) throws Exception {
        return postByForm(url, params, default_connect_timeout, default_socket_timeout, default_retries, default_charaset);
    }
	 public static String postByForm(String url, Map<String,String> params, int connectTimeout, int socketTimeout, int retries, String charset) throws Exception {
		    HttpClient client = makeHttpClient(connectTimeout, socketTimeout, retries, charset);
	        PostMethod post = new PostMethod(url);
	        String jsonStr = "";
	        InputStream ins = null;
	        long start = 0L;
	        try {
	        	post.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset="+charset); 
	            
	            if(params!=null&&!params.isEmpty()){
	                NameValuePair[] paramsPair = new NameValuePair[params.size()];
	                Iterator<Map.Entry<String,String>> iter = params.entrySet().iterator();//采用高效遍历法
	                int i=0;
	                while (iter.hasNext()) {
	                    Map.Entry<String,String> entry = (Map.Entry<String,String>)iter.next();
	                    NameValuePair jsonArry = new NameValuePair(entry.getKey(),entry.getValue());
	                    paramsPair[i] = jsonArry;
	                    i = i+1;
	                }
	                post.setRequestBody(paramsPair);
	            } 
	            if(logger.isInfoEnabled())
	            	logger.info("HttpUtil: post " + params.toString());
	            start = System.currentTimeMillis();
	            int status = client.executeMethod(post);
	            if(status == HttpStatus.SC_OK){  
	                jsonStr = post.getResponseBodyAsString();
	            }else{
					logger.error("HttpUtil.postByForm error, url={}, response code:{}, headers:{}",
							url, status, Arrays.toString(post.getResponseHeaders()));
					post.abort();//断连接
	            }
	        } catch (Exception e) {
	            if(logger.isErrorEnabled())
	            	logger.error("HttpUtil.simplePost error, url="+url, e);
	            throw e;//错误日志在调用层根据类型记录
	        }finally{
	            if (null != ins) {
	                try {
	                    ins.close();
	                } catch (IOException e) {
	                }
	            }
	            long time = System.currentTimeMillis()-start;
	            if(logger.isInfoEnabled())
	            	logger.info("调用接口"+url+"耗时："+time);
	            if(null != post){
	                post.releaseConnection();
	            }
	        }
	        return jsonStr;
	    }

	/**
	 * set encoding for params, Do Not use URLEncode.encode() when invoke this method.
	 * @param url
	 * @param params
	 * @param connectTimeout
	 * @param socketTimeout
	 * @param retries
	 * @param charset
	 * @return
	 * @throws Exception
	 */
	 public static String post(String url, Map<String,String> params, String body, int connectTimeout, int socketTimeout, int retries, String charset) throws Exception {
		    HttpClient client = makeHttpClient(connectTimeout, socketTimeout, retries, charset);
	        PostMethod post = new PostMethod(url);
	        if(body != null){
	        	post.setRequestEntity(new StringRequestEntity(body,"text/html", charset));
	        }
	        String jsonStr = "";
	        InputStream ins = null;
	        long start = 0L;
	        try {
	            post.addRequestHeader("Content-type","text/html;charset="+charset); 
	            
	            if(params!=null&&!params.isEmpty()){
	                NameValuePair[] paramsPair = new NameValuePair[params.size()];
	                Iterator<Map.Entry<String,String>> iter = params.entrySet().iterator();//采用高效遍历法
	                int i=0;
	                while (iter.hasNext()) {
	                    Map.Entry<String,String> entry = (Map.Entry<String,String>)iter.next();
	                    NameValuePair jsonArry = new NameValuePair(entry.getKey(),entry.getValue());
	                    paramsPair[i] = jsonArry;
	                    i = i+1;
	                }
	                post.setQueryString(paramsPair);
	            } 
	            if(logger.isInfoEnabled())
	            	logger.info("HttpUtil: post " + post.getQueryString());
	            start = System.currentTimeMillis();
	            int status = client.executeMethod(post);
	            if(status == HttpStatus.SC_OK){  
	                jsonStr = post.getResponseBodyAsString();
	            }else{
					logger.error("HttpUtil.post error, url={}, response code:{}, headers:{}",
							url, status, Arrays.toString(post.getResponseHeaders()));
	                post.abort();//断连接
	            }
	        } catch (Exception e) {
	            if(logger.isErrorEnabled())
	            	logger.error("HttpUtil.post error, url="+url, e);
	            throw e;//错误日志在调用层根据类型记录
	        }finally{
	            if (null != ins) {
	                try {
	                    ins.close();
	                } catch (IOException e) {
	                }
	            }
	            long time = System.currentTimeMillis()-start;
	            if(logger.isInfoEnabled())
	            	logger.info("调用接口"+url+"耗时："+time);
	            if(null != post){
	                post.releaseConnection();
	            }
	        }
	        return jsonStr;
	    }
	  
	    public static String post(String url, Map<String,String> params, String body) throws Exception {
	        return post(url, params, body, default_connect_timeout, default_socket_timeout, default_retries, default_charaset);
	    }
	    
	    
	    /**
	     * 如果是中文，必须对参数进行编码才能传进来
	     * @param url
	     * @param headers
	     * @param paramsMap
	     * @param connectTimeout
	     * @param socketTimeout
	     * @param retries
	     * @param charset
	     * @return
	     * @throws Exception
	     */
	    public static String get(String url, Map<String,String> headers,
	            Map<String,String> paramsMap, int connectTimeout, int socketTimeout, int retries, String charset) throws Exception {
	        HttpClient client = makeHttpClient(connectTimeout, socketTimeout, retries, charset);   
	        
            if(paramsMap!=null && !paramsMap.isEmpty()){
                Set<String> it = paramsMap.keySet();
                StringBuilder parameterSb = new StringBuilder(320);
                parameterSb.append(url);
                boolean contains = url.contains("?");
                for (String key : it) {
                    String value = paramsMap.get(key);
                    if(contains){
                    	//url += "&";
                    	parameterSb.append('&');
                    } else {
                    	//url += "?";
                    	parameterSb.append('?');
                    	contains = true;
                    }
                    //url += key + "=" + value;
                    parameterSb.append(key).append('=').append(value);
                }
                url = parameterSb.toString();
            }
	        
	        GetMethod getMethod = new GetMethod(url);
	        getMethod.addRequestHeader("Content-type","text/html;charset="+charset);
	        if(headers!=null&&!headers.isEmpty()){
	            Iterator<Map.Entry<String,String>> iter = headers.entrySet().iterator();//采用高效遍历法
	            while (iter.hasNext()) {
	                Map.Entry<String,String> entry = (Map.Entry<String,String>)iter.next();
	                getMethod.addRequestHeader(entry.getKey(),entry.getValue());
	            }
	        }
	        
	        String jsonStr = "";
	        InputStream ins = null;
	        long start = 0L;
	        try {
	            start = System.currentTimeMillis();
	            int status = client.executeMethod(getMethod);
	            if(status == HttpStatus.SC_OK){ 
	                //判断如果是GZIP格式则进行解压
	                if(null!=getMethod.getResponseHeader("Content-Encoding")){
	                    if(getMethod.getResponseHeader("Content-Encoding").getValue().trim().equalsIgnoreCase("gzip")){
	                        ins = getMethod.getResponseBodyAsStream();
	                        jsonStr =uncompress(ins, default_charaset);
	                    }else{              
	                        jsonStr = getMethod.getResponseBodyAsString();
	                    }
	                }else{
	                    jsonStr = getMethod.getResponseBodyAsString();
	                }
	                //responseJson = JSONObject.fromObject(jsonStr); 
	                //responseJson.put("code", status);
	            }else{
					logger.error("HttpUtil.get error, url={}, response code:{}, headers:{}",
							url, status, Arrays.toString(getMethod.getResponseHeaders()));
	                getMethod.abort();//断连接
	            }
	        }catch (Exception e) {
//	        	if(logger.isErrorEnabled())
//	            	logger.error("HttpUtil.get error, url="+url, e);
	        	throw new Exception("HttpUtil.get error, url="+url, e) ;//错误日志在调用层根据类型记录
	        }finally{
	            if (null != ins) {
	                try {
	                    ins.close();
	                } catch (IOException e) {
	                }
	            }
	            if(null != getMethod){
	                getMethod.releaseConnection();
	            }
	            long time = System.currentTimeMillis()-start;
	            if(logger.isInfoEnabled())
	            		logger.info("调用接口"+url+"耗时："+time);
	        }
	        return jsonStr;
	    }
	    
	    public static String get(String url,  Map<String, String> params, int timeout) throws Exception {
	    	return get(url, null, params, timeout, timeout, default_retries, default_charaset);
	    }
	    
	    public static String get(String url,  Map<String, String> params, String charset) throws Exception {
	    	return get(url, null, null, default_connect_timeout, default_socket_timeout, default_retries, charset);
	    }
	    
	    public static String get(String url, Map<String, String> params) throws Exception{
	    	return get(url, null, params);
	    }
	    
	    /**
	     * @return
	     */
	    public static String get(String url, Map<String,String> headers,
	            Map<String, String> paramsMap) throws Exception {
	        return get(url, headers, paramsMap, default_connect_timeout, default_socket_timeout, default_retries, default_charaset);
	    }
	    
	    public static String uncompress(InputStream in,String charset) throws IOException{
           GZIPInputStream gInputStream = new GZIPInputStream(in);
           byte[] by = new byte[1024];
           StringBuffer strBuffer = new StringBuffer();
           int len = 0;
           while ((len = gInputStream.read(by)) != -1) {
               strBuffer.append( new String(by, 0, len,charset) );
           }
           return strBuffer.toString();
	    }
	    
	    /*
	     * GZIP压缩字符串
	     */   
	    public static String compressStr(String str,String charset) throws IOException {   
	        if (str == null || str.length() == 0) {   
	            return str;   
	        }   
	        ByteArrayOutputStream out = new ByteArrayOutputStream();   
	        GZIPOutputStream gzip = new GZIPOutputStream(out);   
	        gzip.write(str.getBytes(charset)); 
	        gzip.close();   
	        return out.toString("ISO-8859-1");   
	    }   
	     
	    /*
	     * GZIP解压字符串
	     */   
	    public static String uncompressStr(String str,String charset) throws IOException {   
	       if (str == null || str.length() == 0) {   
	           return str;   
	       }   
	       ByteArrayOutputStream out = new ByteArrayOutputStream();   
	       ByteArrayInputStream in = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));   
	       GZIPInputStream gunzip = new GZIPInputStream(in);   
	       byte[] buffer = new byte[256];   
	       int n;   
	       while ((n = gunzip.read(buffer))>= 0) {   
	           out.write(buffer, 0, n);   
	       }      
	       return out.toString(charset);   
	     }  
	    /**
	     *获得请求的状态码 
	     * @param url
	     * @param params
	     * @return http 状态码(code)和重定向(redirect)后的网址
	     * <br/>
	     * 如果是 301 or 302 还有值redirect：重定向后的网址(不是很完整,没有参数)
	     * @throws Exception
	     */
	    public static Map<String,String>  getHttpStatus(String url,  Map<String, String> params) throws Exception{
	    	 HttpClient client = makeHttpClient(default_socket_timeout, default_connect_timeout, default_retries, default_charaset);
		        
	            if(params!=null && !params.isEmpty()){
	            	StringBuilder sb = new StringBuilder(320);
	            	sb.append(url);
	                Set<String> it = params.keySet();
	                for (String key : it) {
	                    String value = params.get(key);
	                    if(url.contains("?"))
	                    	sb.append("&");
	                    else 
	                    	sb.append("?");
	                    sb.append(key).append("=").append(value);
	                }
	                url = sb.toString();
	            }
		        
		        GetMethod getMethod = new GetMethod(url);
		        getMethod.setFollowRedirects(false);
		        int code = client.executeMethod(getMethod);
		        Map<String, String> map = new HashMap<>(2);
		        map.put("code", code+"");
		        if(code == 302 || code == 301){
		        	Header hd = getMethod.getResponseHeader("Location");
		        	//HeaderElement[] hes = hd.getElements();
				    map.put("redirect", hd.getValue());
		        }
		        return map;
	    }


	/**
	 * 模拟http表单形式上传文件
	 * @param url
	 * @param params
	 * @param files 文件Map，key是http表单名称，Pair第一个元素为文件名，第二个参数为文件数据
	 * @return
	 * @throws Exception
	 */
	 public static String upload(String url, Map<String, String> params, Map<String, Pair<String, byte[]>> files) throws Exception {
		 String jsonStr = null;
		 long start = System.currentTimeMillis();

		 HttpClient client =  makeHttpClient(default_socket_timeout, default_connect_timeout, default_retries, default_charaset);
		 PostMethod filePost = new PostMethod(url);

		 List<Part> partList = new ArrayList<>();
		 if (params != null) {
			 params.keySet().stream().forEach(name -> partList.add(new StringPart(name, params.get(name))));
		 }

		 if (files != null){
			 files.keySet().stream().forEach(name -> {
				 Pair<String, byte[]> fileData = files.get(name);
				 partList.add(new FilePart(name, new ByteArrayPartSource(fileData.first, fileData.second)));
			 });
		 }

		 filePost.setRequestEntity(new MultipartRequestEntity(partList.toArray(new Part[]{}), filePost.getParams()));
		 try {
			 start = System.currentTimeMillis();
			 int status = client.executeMethod(filePost);
			 if (status == HttpStatus.SC_OK) {
				 jsonStr = filePost.getResponseBodyAsString();
			 } else {
				 logger.error("HttpUtil.upload error, url={}, response code:{}, headers:{}",
						 url, status, Arrays.toString(filePost.getResponseHeaders()));
				 filePost.abort();//断连接
			 }
		 } catch (Exception e) {
			 if (logger.isErrorEnabled())
				 logger.error("HttpUtil.upload error, url=" + url, e);
			 throw e;//错误日志在调用层根据类型记录
		 } finally {
			 long time = System.currentTimeMillis() - start;
			 if (logger.isInfoEnabled())
				 logger.info("调用upload接口" + url + "耗时：" + time);
			 if (null != filePost) {
				 filePost.releaseConnection();
			 }
		 }
		 return jsonStr;
	 }
}
