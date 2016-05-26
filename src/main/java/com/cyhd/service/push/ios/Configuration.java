package com.cyhd.service.push.ios;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.cyhd.service.util.GlobalConfig;

/**
 *
 * IOS推送的配置，支持根据bundle id来获取不同的推送服务<br/>
 * 推送证书放在/cert/文件夹下，名称格式为：aps_development_XXXX.p12， XXXX为bundle id按点分隔的第二段，例如：im.jiansheng.liu，则证书为：aps_development_jiansheng.p12，
 */
public class Configuration {

    public final static String REGEX_NEWLINE = "\n|\r\n|\r|\n\r";
    public final static String HTML_BR = "<br/>";

    // PushServer和Master之间心跳检测周期，30秒
    public static final long KEEP_ALIVE_CHECK_INTERVAL = 30 * 1000L;
    public final static String SANDBOX = "sandbox";
    public final static String PRODUCT = "product";

    // ios push server
    private static String iosPushServer;

    public final static String getIOSPushServer() {
        return iosPushServer;
    }

    // iphone push启动参数
    private static boolean iphonePushStart;

    public final static boolean isIPhonePushStart() {
        return iphonePushStart;
    }

    // iphone push服务证书
    //  private static String iphonePushCer;
    private static Map<String, String> iphonePushCerMap;

    /**
     * 获取bundle id的第二段
     * @param bundleId
     * @return
     */
    public final static String getIPhonePushCer(String bundleId) {
        String[] arry = bundleId.split("\\.");
        if (arry.length < 2) {
            return null;
        }
        return iphonePushCerMap.get(arry[1]);
    }

    // iphone push服务证书的密码
    private static String iphonePushPwd;

    public final static String getIPhonePushPwd() {
        return iphonePushPwd;
    }

    // 推荐单push间隔时间
    private static int recPushIntDays;

    public final static int getRecPushIntDays() {
        return recPushIntDays;
    }

    // 应用未启动的天数(触发push推荐单的逻辑)
    private static int recNolaunchDays;

    public final static int getRecNolaunchDays() {
        return recNolaunchDays;
    }

    // 数字提醒间隔天数
    private static int badgeIntDays;

    public final static int getBadgeIntDays() {
        return badgeIntDays;
    }

    // 导出任务启动时间(系统启动后的秒数)
    private static int expSecsAfSysStart;

    public final static int getExpSecsAfSysStart() {
        return expSecsAfSysStart;
    }

    // 两次导出任务之间的间隔(check当天数据是否成功导出)
    private static int expIntSecs;

    public final static int getExpIntSecs() {
        return expIntSecs;
    }

    // push任务启动时间(系统启动后的秒数)
    private static int pushSecsAfSysStart;

    public final static int getPushSecsAfSysStart() {
        return pushSecsAfSysStart;
    }

    // 两次push任务之间的间隔
    private static int pushIntSecs;

    public final static int getPushIntSecs() {
        return pushIntSecs;
    }

    // 不活跃设备清除任务的时间(星期几/几点/几分)
    private static int clearDayOfWeek;

    public final static int getClearDayOfWeek() {
        return clearDayOfWeek;
    }

    private static int clearHourOfDay;

    public final static int getClearHourOfDay() {
        return clearHourOfDay;
    }

    private static int clearMinOfHour;

    public final static int getClearMinOfHour() {
        return clearMinOfHour;
    }

    private static boolean IS_SERVER_RUNNING = true;

    public final static boolean isServerRunning() {
        return IS_SERVER_RUNNING;
    }

    public static void serverStart() {
        IS_SERVER_RUNNING = true;
    }

    public static void serverStop() {
        IS_SERVER_RUNNING = false;
    }

    static {
        try {
            iphonePushCerMap = new HashMap<String, String>();
            iosPushServer = GlobalConfig.getValue("ios.push.server");
            iphonePushStart = Boolean.valueOf(GlobalConfig.getValue("iphone.push.start"));
            if (iphonePushStart) {
//	        if(GlobalConfig.isDeploy)
//	        	iphonePushCer = Thread.currentThread().getContextClassLoader().getResource("apns_production.p12").getFile();
//	        else
                //iphonePushCer = Thread.currentThread().getContextClassLoader().getResource(GlobalConfig.getValue("iphone.push.certification")).getFile();
                String certPath = Configuration.class.getClassLoader().getResource("").getPath() + "cert/";
                File file = new File(certPath);
                File[] files = file.listFiles();
                String fileName = null;
                for (File f : files) {
                    if (f == null || f.isDirectory()) {
                        continue;
                    }
                    fileName = f.getName();
                    if (fileName.lastIndexOf(".p12") == -1) {
                        continue;
                    }
                    if (PRODUCT.equals(getIOSPushServer())) {
                        if (fileName.startsWith("aps_development_")) {
                            continue;
                        }
                    } else {
                        if (fileName.startsWith("aps_production_")) {
                            continue;
                        }
                    }
//    			//String cert = Thread.currentThread().getContextClassLoader().getResource(fileName).getFile();
//    			String cert = FileUtils.readFileToString(f, "utf-8");
                    String key = fileName.substring(fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
                    iphonePushCerMap.put(key, f.getAbsolutePath());
                }
                iphonePushPwd = GlobalConfig.getValue("iphone.push.password");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public static void main(String[] args) {
//        Configuration config = new Configuration();
//        
//    }

}
