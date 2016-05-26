package com.cyhd;

import com.cyhd.common.util.HttpUtil;
import com.cyhd.common.util.MD5Util;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.util.RequestSignUtil;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by hy on 10/13/15.
 */
public class TestVendorCallback {

    public static String vendorForZaker(String idfa){
        String appkey = "eb5a153caf6acf0c9838e07092b0c841";
        String appsecret = "605d5c80f73a38a5f0dd4ed15dc394f3";
        String adid = "2427";

        Map<String, String> params = new HashMap<>();
        params.put("appkey", appkey);
        params.put("adid", adid);
        params.put("timestamp", "" + (System.currentTimeMillis() / 1000));
        params.put("device_id", idfa);

        String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(params) + appsecret);
        params.put("sign",sign);

        System.out.println(RequestSignUtil.getSortedRequestString(params));
        System.out.println("");
        String url = "http://third.miaozhuandaqian.com/www/vendor/callback.3w?" + RequestSignUtil.getSortedRequestString(params);
        System.out.println(url);

        return url;
    }

    public static void vendorForYoupinhui(){
        String appkey = "b6311db5161f91dc9d37200f4e8b0ce3";
        String appsecret = "f1db0f8fc34f779574b13dd42a3b1132";
        String adid = "1877";
        String idfa = "A97F6107-4030-4925-8C23-17736672E5F4";

        Map<String, String> params = new HashMap<>();
        params.put("appkey", appkey);
        params.put("adid", adid);
        params.put("timestamp", "" + (System.currentTimeMillis() / 1000));
        params.put("device_id", idfa);

        String sign = MD5Util.getMD5(RequestSignUtil.getSortedRequestString(params) + appsecret);
        params.put("sign",sign);

        System.out.println(RequestSignUtil.getSortedRequestString(params));
        System.out.println("");
        System.out.println("http://third.miaozhuandaqian.com/www/vendor/callback.3w?" + RequestSignUtil.getSortedRequestString(params));
    }

    public static void main(String[] args){
        LineNumberReader reader = new LineNumberReader(new InputStreamReader(System.in));
        try {
            for (String line = reader.readLine(); line != null; line = reader.readLine()){
                if (StringUtil.isNotBlank(line.trim())) {
                    String url = vendorForZaker(line.trim());

                    try {
                        HttpUtil.get(url, null);

                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
