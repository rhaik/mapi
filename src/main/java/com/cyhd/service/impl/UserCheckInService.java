package com.cyhd.service.impl;

import com.cyhd.common.util.DateUtil;
import com.cyhd.service.constants.Constants;
import com.cyhd.service.dao.CacheDao;
import com.cyhd.service.dao.db.mapper.UserCheckInMapper;
import com.cyhd.service.dao.po.UserCheckInRecord;
import com.cyhd.service.dao.po.UserCheckInStat;
import com.cyhd.service.dao.po.UserFriendMessage;
import com.cyhd.service.dao.po.UserSystemMessage;
import com.cyhd.service.util.CacheUtil;
import com.cyhd.service.util.VersionUtil;
import com.cyhd.web.common.ClientInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

/**
 * 用户每日签到功能对应的服务
 * Created by hy on 9/16/15.
 */
@Service
public class UserCheckInService {

    /**
     * 用户的签到级别，根据签到天数决定用户获取的金币数量
     */
    public enum CheckInStage {

        FIRST(1, 50), SECOND(6, 80), THIRD(16, 100);

        int minDays;
        int amount;

        /**
         * 创建签到级别
         * @param minDays 最低天数（包含）
         * @param amount 奖励金额
         */
        CheckInStage(int minDays, int amount){
            this.minDays = minDays;
            this.amount = amount;
        }

        /**
         * 根据用户的签到天数获取其签到级别
         * @param days
         * @return
         */
        public static CheckInStage getStage(int days){
            CheckInStage userStage = FIRST;
            CheckInStage[] stages = CheckInStage.values();

            //从后往前找到天第一个签到天数大于设定天数的CheckInStage
            for (int i = stages.length - 1; i >= 0; -- i){
                CheckInStage stage = stages[i];
                if (days >= stage.minDays){
                    userStage = stage;
                    break;
                }
            }
            return userStage;
        }

        /**
         * 获取当前级别的下一个级别
         * @return
         */
        public CheckInStage nextStage(){
            CheckInStage nextStage = null;
            boolean isNext = false;
            for ( CheckInStage stage : CheckInStage.values() ){
                if (isNext){
                    nextStage = stage;
                    break;
                }
                if (stage == this){
                    isNext = true;
                }
            }
            return nextStage;
        }

        public int getMinDays() {
            return minDays;
        }

        public void setMinDays(int minDays) {
            this.minDays = minDays;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
    }


    private final static Logger logger = LoggerFactory.getLogger(UserCheckInService.class);

    private final static long USER_CHECKIN_TTL = Constants.day_millis * 1;

    @Resource(name = CacheUtil.MEMCACHED_RESOURCE)
    private CacheDao memcachedCacheDao;


    @Resource
    UserCheckInMapper userCheckInMapper;

    @Resource
    UserIntegalIncomeService integalIncomeService;

    @Resource
    UserFriendService userFriendService;


    /**
     * 获取最近的签到记录
     * @param userId
     * @return
     */
    public UserCheckInRecord getLatestCheckInRecord(int userId){
        List<UserCheckInRecord> records = getRecentRecords(userId);
        if (records != null && records.size() > 0){
            return records.get(0);
        }
        return null;
    }

    /**
     * 增加签到记录
     * @return
     */
    protected boolean addCheckInRecord(UserCheckInRecord record){
        int i = userCheckInMapper.addCheckInRecord(record);
        if (i > 0){
            removeCache(record.getUser_id());
        }
        return i > 0;
    }

    /**
     * 是否是今天的签到记录
     * @return
     */
    public boolean isTodayRecord(UserCheckInRecord record){
        if (record != null && DateUtil.isSameDay(record.getCheckin_time(), new Date())){
            return true;
        }
        return  false;
    }


    /**
     * 是否是昨天的签到记录
     * @param record
     * @return
     */
    public boolean isYesterdayRecord(UserCheckInRecord record){
        Date yesterday = DateUtil.addDate(new Date(), - 1);
        if (record != null && DateUtil.isSameDay(record.getCheckin_time(), yesterday)){
            return true;
        }
        return  false;
    }

    /**
     * 用户今天是否已经签到过
     * @param userId
     * @return
     */
    public boolean isCheckinToday(int userId){
        return isTodayRecord(getLatestCheckInRecord(userId));
    }

    /**
     * 用户签到操作
     * @param userId
     * @return
     */
    public boolean doCheckIn(int userId, ClientInfo clientInfo){
        UserCheckInRecord lastRecord = getLatestCheckInRecord(userId);

        //检查用户当天是否已经签到过
        if (isTodayRecord(lastRecord)){
            return false;
        }


        UserCheckInRecord todayRecord = new UserCheckInRecord();
        todayRecord.setCheckin_time(new Date());
        todayRecord.setUser_id(userId);


        //如果上次签到是昨天
        if (isYesterdayRecord(lastRecord)){
            todayRecord.setDays(lastRecord.getDays() + 1);
        }else {
            //昨天没有签到，从头开始算起
            todayRecord.setDays(1);
        }

        //根据累计签到时间计算奖励金额
        int income = Math.round(getCheckInStage(todayRecord.getDays()).getAmount() * getUserCheckinRate(userId));
        todayRecord.setIncome(income);

        //增加签到记录
        boolean isSuccess = addCheckInRecord(todayRecord);
        if (isSuccess){
        	int clientType = VersionUtil.getDeviceType(clientInfo.getClientType());
            //增加用户金币收入，万普的积分为金币
           if( integalIncomeService.updateIntegalIncome(userId, Constants.INTEGAL_SOURCE_WANPU, income, clientType)){
        	   integalIncomeService.addShare(userId, income, Constants.INTEGAL_SOURCE_WANPU, "签到", UserSystemMessage.TYPE_INTEGAL_SHARE_QIANDAO, clientType);
           }
        }

        logger.info("user {} do check in, result:{}, checkIn record:{}", userId, isSuccess, todayRecord);

        return isSuccess;
    }

    /**
     * 根据累计天数决定用户签到的签到级别
     * @param days
     * @return
     */
    public CheckInStage getCheckInStage(int days) {
        return CheckInStage.getStage(days);
    }

    /**
     * 清除缓存
     * @param userId
     */
    protected void removeCache(int userId){
        memcachedCacheDao.remove(CacheUtil.getUserCheckInListKey(userId));
        memcachedCacheDao.remove(CacheUtil.getUserCheckInStatKey(userId));
    }

    /**
     * 获取用户签到统计信息
     * @param userId
     * @return
     */
    public UserCheckInStat getUserCheckInStat(int userId){
        String cacheKey = CacheUtil.getUserCheckInStatKey(userId);
        UserCheckInStat checkInStat = (UserCheckInStat) memcachedCacheDao.get(cacheKey);
        if (checkInStat == null){
            checkInStat = userCheckInMapper.getUserCheckStat(userId);
            if (checkInStat != null){
                memcachedCacheDao.set(cacheKey, checkInStat, USER_CHECKIN_TTL);
            }
        }

        //返回默认的数据
        if (checkInStat == null){
            checkInStat = new UserCheckInStat(userId);
        }

        return checkInStat;
    }


    /**
     * 获取最近10条签到记录，有缓存
     * @param userId
     * @return
     */
    public List<UserCheckInRecord> getRecentRecords(int userId){
        String cacheKey = CacheUtil.getUserCheckInListKey(userId);
        List<UserCheckInRecord> records = (List<UserCheckInRecord>) memcachedCacheDao.get(cacheKey);
        if (records == null){
            records = getUserCheckInRecords(userId, 0 , 10);
            if (records != null) {
                memcachedCacheDao.set(CacheUtil.getUserCheckInListKey(userId), records, USER_CHECKIN_TTL);
            }
        }
        return records;
    }


    /**
     * 分页获取用户签到记录，无缓存
     * @param userId
     * @param start
     * @param limit
     * @return
     */
    public List<UserCheckInRecord> getUserCheckInRecords(int userId, int start, int limit){
        return userCheckInMapper.getUserCheckInRecords(userId, start, limit);
    }

    /**
     * 获取用户的签到奖励级别
     * @param userId
     * @return
     */
    public float getUserCheckinRate(int userId){
        float rate = 1f;

        // 双十二活动，已结束
//        int friends = userFriendService.countUserFriends(userId);
//
//        if (friends > 9){
//            rate = 2f;
//        }else if (friends > 4){
//            rate = 1.5f;
//        }


        return rate;
    }
}
