package com.cyhd.service.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 灰度策略工具类
 * Created by hy on 1/26/16.
 */
public class GrayStrategyUtil {


    private static final Set<Integer> GRAY_USERS = new HashSet<>(Arrays.asList(
            161, //小萍
            1549378, //聪文
            6722, //马唯
            623662, //陈雪1
            308278, //陈雪2
            245924, //婷婷
            1756703, //张通
            1344067, //任楠
            1908877,1549378,374187,1849063,1344067,1193186
    ));

    //是不是灰度用户
    public static boolean isGrayUser(int userId){
        //测试环境一律通过
        if (!GlobalConfig.isDeploy){
            return true;
        }
        if (userId <= 100){
            return true;
        }
        return GRAY_USERS.contains(userId);
    }


    public static boolean isPublicGrayUser(int userId){
        if (isGrayUser(userId)){
            return true;
        }
        return (userId % 10) < 3;
    }

    //不是灰度用户
    public static boolean isNotGrayUser(int userId){
        return !isGrayUser(userId);
    }
}
