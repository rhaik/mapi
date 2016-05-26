package com.cyhd.service.impl;

import com.cyhd.common.util.*;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.impl.CacheLRULiveAccessDaoImpl;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserDraw;
import com.cyhd.service.dao.po.UserDrawLog;
import com.cyhd.service.dao.po.UserIncome;
import com.cyhd.service.util.GlobalConfig;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.vo.UserDrawLogVo;
import com.cyhd.web.exception.CommonException;
import com.cyhd.web.exception.ErrorCode;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 红包活动服务
 * Created by hy on 1/29/16.
 */
@Service
public class HongbaoActivityService extends BaseService {

    @Resource(name=RedisUtil.NAME_SELF)
    private IJedisDao hongbaoCache;

    /**
     * 奖励类型，1是现金，2是金币
     */
    public final static int TYPE_CASH = 1;
    /**奖励类型，1是现金，2是金币**/
    public final static int TYPE_COIN = 2;


    //系统红包数量
    public final static int SYSTEM_HONGBAO_NUM = 0;

    //系统用户id
    private final static int SYSTEM_USER_ID = 0;

    //用户每轮的最多抽奖次数
    public static final int MAX_DRAW_TIMES = 0;

    //注册的最小日期，之前的用户才能抽大奖
    private final static Date MIN_CREATE_DATE = DateUtil.parseDate("2015-12-01");

    /**
     * 允许的并发抢红包的数量
     */
    private Semaphore hongbaoSemaphore = new Semaphore(10);

    private final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Resource
    UserIncomeService userIncomeService;


    @Resource
    UserIntegalIncomeService integalIncomeService;


    @Resource
    private UserMessageService userMessageService;


    @Resource
    private UserDrawService userDrawService;


    @Resource
    private UserService userService;


    //系统红包数量缓存
    LiveAccess<Integer> systemHongbaoNumCache = null;

    //用户专属红包数量缓存
    CacheLRULiveAccessDaoImpl<Integer> userHongbaoNumCache = new CacheLRULiveAccessDaoImpl<>(Constants.minutes_millis, 500);

    /**
     * 获取剩余系统红包数量
     * 保存在userDraw里面，用户id为0
     * @return
     */
    public int getSystemHongbaoNum(){
        int num = 0;
        if (systemHongbaoNumCache != null && systemHongbaoNumCache.getElement() != null) {
            num = systemHongbaoNumCache.getElement();
        }else {
            UserDraw userDraw = userDrawService.getUserDrawFromDB(SYSTEM_USER_ID, GlobalConfig.ACTIVITY_ID);
            if (userDraw != null){
                num = userDraw.getBalance_times();
                systemHongbaoNumCache = new LiveAccess<>(Constants.minutes_millis, num);
            }
        }

        return num;
    }

    /**
     * 获取用户专属红包剩余数量
     * @param userId
     * @return
     */
    public int getUserHongbaoNum(int userId){
        int num = 0;
        if (userHongbaoNumCache.get("" + userId) != null){
            num = userHongbaoNumCache.get("" + userId);
        }else {
            UserDraw userDraw = userDrawService.getUserDrawFromDB(userId, GlobalConfig.ACTIVITY_ID);
            if (userDraw != null && userDraw.getBalance_times() > 0) {
                num = userDraw.getBalance_times();
                userHongbaoNumCache.set("" + userId, num);
            }
        }
        return num;
    }

    /**
     * 获取用户专属红包数量
     * @param userId
     * @return
     */
    public Pair<Integer, Integer> grabUserHongbao(int userId){
        Pair<Integer, Integer> amountPair = null;
        UserDraw userDraw = userDrawService.getUserDraw(userId, GlobalConfig.ACTIVITY_ID);
        if (userDraw != null && userDraw.getBalance_times() > 0){
            boolean flag = userDrawService.decrDrawBalance(userId, GlobalConfig.ACTIVITY_ID);
            if (flag){
                amountPair = determineHongbaoAmount(userId);
                addUserHongbaoIncome(userId, userId, amountPair);
                userHongbaoNumCache.remove("" + userId);
            }
        }

        logger.info("grab user hongbao, user:{}, hongbao:{}", userId, amountPair);
        return amountPair;
    }


    /**
     * 增加系统抽奖次数
     */
    public void resetSystemHongbaoNum(){
        boolean flag = userDrawService.updateDrawBalance(SYSTEM_USER_ID, GlobalConfig.ACTIVITY_ID, SYSTEM_HONGBAO_NUM);

        logger.info("resetSystemHongbaoNum, num:{}, success:{}", SYSTEM_HONGBAO_NUM, flag);
    }


    /**
     * 用户抢系统红包
     * @param userId
     * @return 小于10为金币数量，大于等于10为现金红包
     */
    public Pair<Integer, Integer> grabSystemHongbao(int userId) throws CommonException{
        Pair<Integer, Integer> amountPair = null;
        try {
            //并发数控制
            hongbaoSemaphore.acquire();

            UserDraw userDraw = userDrawService.getUserDraw(SYSTEM_USER_ID, GlobalConfig.ACTIVITY_ID);
            if (userDraw != null && userDraw.getBalance_times() > 0){

                //判断用户当前的抽奖次数
                Date lastHongbaoTime = getPreviousHongbaoTime();
                int userTimes = userDrawService.countUserDrawTimes(userId, GlobalConfig.ACTIVITY_ID, lastHongbaoTime);

                if (userTimes > MAX_DRAW_TIMES){
                    throw new CommonException(ErrorCode.ERROR_CODE_UNKNOWN, "您本轮系统红包机会已用完");
                }

                boolean flag = userDrawService.decrDrawBalance(SYSTEM_USER_ID, GlobalConfig.ACTIVITY_ID);
                if (flag){
                    amountPair = determineHongbaoAmount(userId);
                    addUserHongbaoIncome(userId, SYSTEM_USER_ID, amountPair);

                    systemHongbaoNumCache = null;
                }
            }

            TimeUnit.MILLISECONDS.sleep(50);

        } catch (InterruptedException exp) {
            logger.error(exp.getMessage(), exp);
        } finally {
            hongbaoSemaphore.release();
        }

        logger.info("grab system hongbao, user:{}, hongbao:{}", userId, amountPair);
        return amountPair;
    }


    /**
     * 增加用户的红包收入
     * @param userId
     * @param amountPair
     */
    protected void addUserHongbaoIncome(int userId, int srcUserId,Pair<Integer, Integer> amountPair){
        String orderId = userId + "-" + (System.currentTimeMillis() / 1000);
        String reason = "";
        if (amountPair.first == TYPE_CASH) {
            userIncomeService.addDrawIncome(userId, amountPair.second, "摇一摇红包");
            reason = MoneyUtils.fen2yuanS(amountPair.second) + "元红包";
        } else {
            integalIncomeService.addRewardedIntegral(userId, amountPair.second, "摇一摇红包", Constants.platform_ios, orderId);
            reason = amountPair.second + "金币";
        }
        UserDrawLog log = new UserDrawLog(userId, srcUserId, reason, GlobalConfig.ACTIVITY_ID, amountPair.second, UserDrawLog.UserDrawLogType.DECREMENT);

        //增加抽奖记录
        userDrawService.addDrawLog(log);

        //记录大红包
        if (amountPair.first == TYPE_CASH && log.getDraw_amount() > 50){
            try {
                UserDrawLog lastBig = getLastBigHongbao();
                if (lastBig == null || lastBig.getDraw_amount() < log.getDraw_amount()){
                    String cacheKey = RedisUtil.buildBigHongbaoKey(GlobalConfig.ACTIVITY_ID);
                    hongbaoCache.set(cacheKey, gson.toJson(log));
                    hongbaoCache.expire(cacheKey, 60);
                }

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }

        //发系统消息
        userMessageService.notifyActivityMessage(userId, log, Constants.platform_ios);

        logger.info("add hongbao income for user:{}, type:{}, amount:{}", userId, amountPair.first, amountPair.second);
    }

    private UserDrawLog getLastBigHongbao(){
        UserDrawLog lastBig = null;
        try {
            String bigHongbao = hongbaoCache.get(RedisUtil.buildBigHongbaoKey(GlobalConfig.ACTIVITY_ID));

            if (StringUtil.isNotBlank(bigHongbao)){
                lastBig = gson.fromJson(bigHongbao, UserDrawLog.class);
            }
        } catch (Exception e) {
            logger.error("", e);
        }

        return lastBig;
    }

    /**
     * 决定用户红包金额
     * @param userId
     * @return 小于10为金币，大于10为现金
     */
    public Pair<Integer, Integer> determineHongbaoAmount(int userId){
        Pair<Integer, Integer> amountPair = null; //默认是5个金币

        int rnd = ThreadLocalRandom.current().nextInt(5);
        if (rnd == 0){ //获得现金红包 20%
            int rndAmount = 10 + ThreadLocalRandom.current().nextInt(10);
            if (rndAmount > 14){ //大于14，概率50%，综合概率10%
                rndAmount = 10 + ThreadLocalRandom.current().nextInt(10); //还需要再大于14才会是15以上，综合概率5%
            }

            //看看是不是可以获得大奖，满足此条件的综合概率是1%（100个）
            if (rndAmount == 19){
                //67%的用户还是在1元内
                int ratio = ThreadLocalRandom.current().nextInt(3);

                //33%的概率看看能不能中大奖
                if (ratio == 0){
                    if (isUserCanGetBigHongbao(userId)){ //用户能够获取大奖
                        rndAmount = 21 + ThreadLocalRandom.current().nextInt(480);
                    }else { //不能获取大奖，重新设置为0.19元
                        rndAmount = 19;
                    }
                }else { //66%概率在1元内
                    rndAmount = 20 + ThreadLocalRandom.current().nextInt(80);
                }
            }

            amountPair = new Pair<>(TYPE_CASH, rndAmount);
        }else { //获得随机金币数量，5-9个
            int amount = 5 + ThreadLocalRandom.current().nextInt(5);
            amountPair = new Pair<>(TYPE_COIN, amount);
        }

        return amountPair;
    }


    public boolean isUserCanGetBigHongbao(int userId){
        boolean flag = false;
        User user = userService.getUserById(userId);
        if (user != null && user.getCreatetime().before(MIN_CREATE_DATE)){
            UserIncome userIncome = userIncomeService.getUserIncome(userId);

            //限时任务收入大于20元，总余额小于100元，才能获得大奖
            if (userIncome != null && userIncome.getTask_total() > 2000 && userIncome.getIncome() < 10000){
                flag = true;
            }
        }

        return flag;
    }

    /**
     * 获取下次红包发放的时间
     * @return
     */
    public String getNextHongbaoTime(){
        String hourDesc = "";
        Date now = GenerateDateUtil.getCurrentDate();
        if (DateUtil.insideTwoTime(now, GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END)){
            int hour = DateUtil.getHour(now);
            if (hour < 11){
                hourDesc = "今天 11:00";
            }else if (hour < 15){
                hourDesc = "今天 15:00";
            }else if (hour < 20){
                hourDesc = "今晚 20:00";
            }else {
                Date tomorrow = DateUtil.addDate(now, 1);
                if (DateUtil.insideTwoTime(tomorrow, GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END)) {
                    hourDesc = "明天 11:00";
                }
            }
        }

        return hourDesc;
    }

    /**
     * 获取当前红包的开始时间
     * @return
     */
    public Date getPreviousHongbaoTime(){
        Date previous = null;
        Date now = GenerateDateUtil.getCurrentDate();
        if (DateUtil.insideTwoTime(now, GlobalConfig.ACTIVITY_START, GlobalConfig.ACTIVITY_END)){
            int hour = DateUtil.getHour(now);
            if (hour < 11){
                Date date = DateUtil.getTodayStartDate();
                previous = DateUtil.getAddDate(date, Calendar.HOUR, -4);
            }else if (hour < 15){
                Date date = DateUtil.getTodayStartDate();
                previous = DateUtil.getAddDate(date, Calendar.HOUR, 11);
            }else if (hour < 20){
                Date date = DateUtil.getTodayStartDate();
                previous = DateUtil.getAddDate(date, Calendar.HOUR, 15);
            }else {
                Date date = DateUtil.getTodayStartDate();
                previous = DateUtil.getAddDate(date, Calendar.HOUR, 20);
            }
        }
        return previous;
    }



    /**
     * 获取最近的红包记录
     * @return
     */
    public List<UserDrawLogVo> getLatestHongbaoList(){
        List<UserDrawLog> drawLogList = userDrawService.getRewardedLog(GlobalConfig.ACTIVITY_ID, 0, 10);

        List<UserDrawLog> newDrawList = new ArrayList<>(drawLogList);
        if ( newDrawList.size() > 4){
            int times = 0;
            //最多循环10次
            while (newDrawList.size() > 4 && times < 10){
                times += 1;
                int rnd = 1 + ThreadLocalRandom.current().nextInt(newDrawList.size() - 1);
                if (newDrawList.get(rnd).getReason().contains("金币")){ //删除金币的日志
                    newDrawList.remove(rnd);
                }
            }
        }

        UserDrawLog lastBigHongbao = getLastBigHongbao();
        if (lastBigHongbao != null){
            newDrawList.add(0, lastBigHongbao);
        }
        return userDrawService.getUserDrawLogVos(newDrawList);
    }
}
