package com.cyhd.service.impl;

import com.cyhd.common.util.DateUtil;
import com.cyhd.common.util.NumberUtil;
import com.cyhd.common.util.Pair;
import com.cyhd.common.util.StringUtil;
import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.po.User;
import com.cyhd.service.dao.po.UserEnchashment;
import com.cyhd.service.dao.po.UserEnchashmentAccount;
import com.cyhd.service.impl.CellphoneAreaService.CellPhoneArea;
import com.cyhd.service.util.IpAddressUtil;
import com.cyhd.service.util.RedisUtil;
import com.cyhd.service.util.RequestUtil;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 反作弊服务
 * Created by hy on 2/29/16.
 */
@Service
public class AntiCheatService extends BaseService{

    @Resource(name=RedisUtil.NAME_SELF)
    private IJedisDao cacheDao;

    @Resource
    private UserTaskService userTaskService;
    
    @Resource
    private AppTaskService appTaskService;
    
    @Resource
    private UserLoginRecordService userLoginRecordService;
    
    @Resource
    private CellphoneAreaService cellphoneAreaService;

    @Resource
    UserEnchashmentService userEnchashmentService;

    //ip限制，10秒
    private final int taskIpTTL = 10;

    //每天允许1个ip最多接多少任务
    private static final int MAX_DAILY_TASK_NUM = 30;

    //重灾区的IP允许接的最多任务数量
    private static final int MAX_DAILY_TASK_BAD_IP = 5;

    //重灾区的整个区域（市级别）允许接的最多任务数量
    private static final int MAX_DAILY_TASK_BAD_AREA = 50;

    //国外单个国家允许接的最多任务数量
    private static final int MAX_DAILY_TASK_BAD_COUNTRY = 30;

    //某个区域（市级别）每天最多接限时任务的个数
    private static final int MAX_DAILY_TASK_OF_AREA = 1500;

    //坏的ip城市，用ip换到地址后，用endsWith来匹配
    public final static List<String> BAD_IP_AREA = Arrays.asList("梅州", "盐城", "广东", "阜新", "东莞");


    ExecutorService antiCheatExecutor;

    @PostConstruct
    public void init(){
        ThreadFactory threadFactory2 = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setName("anti_cheat_thread");
                return t;
            }
        };
        antiCheatExecutor = Executors.newFixedThreadPool(1, threadFactory2);
    }

    @PreDestroy
    public void destroy(){
        antiCheatExecutor.shutdown();
    }

    /**
     * 缓存上一个接任务的ip地址，不允许连续接任务
     * @param ip
     * @return
     */
    public boolean isContinuousTaskIp(String ip){
        boolean flag = false;
        try{
            String key = RedisUtil.getLastIOSTaskIpKey();

            String lastIP = cacheDao.get(key);
            if (lastIP != null && lastIP.equals(ip)){
                flag = true;
                logger.warn("continuous task ip:{}", ip);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return flag;
    }


    /**
     * 做任务是不是请求过于频繁，包括接任务，打开应用以及奖励用户部分
     * @param ip
     * @return
     */
    public boolean isFrequentTaskIp(String ip){
        boolean flag = false;
        try{
            String intervalKey = RedisUtil.buildIOSIPIntervalKey(ip);
            flag = cacheDao.exists(intervalKey);

            //如果请求过于频繁，针对重点区域进行惩罚，其他区域30秒后可恢复访问
            if (flag){
                logger.warn("task ip too frequent:{}", ip);
                punishTooFreqIPByArea(ip);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return flag;
    }


    /**
     * 是不是一天接过太多的任务，通过ip本身以及ip解析后的地址双重判断
     * @param ip
     * @return
     */
    public boolean isTooManyTaskFromIp(String ip){
        boolean isTooMany = false;
        int num = getTaskNumFromIp(ip);
        if (num > MAX_DAILY_TASK_NUM){
            isTooMany = true;
            logger.warn("too many tasks of a usual ip:{}", ip);
        }else if (num > MAX_DAILY_TASK_BAD_IP) {
            if (isIPFromBadArea(ip)) {
                isTooMany = true;
                logger.warn("too many tasks of a bad ip:{}", ip);
            }
        }

        //如果未到达限额，则根据地区再判断
        if (!isTooMany){
            String area = IpAddressUtil.getAddress(ip);
            int areaNum = getTaskNumFromArea(area);
            if (areaNum > MAX_DAILY_TASK_OF_AREA) {
                isTooMany = true;
                logger.warn("too many task from a area, ip:{}, area:{}", ip, area);
            }else if(isIPFromBadArea(ip)){
                if (areaNum > MAX_DAILY_TASK_BAD_AREA){
                    isTooMany = true;
                    logger.warn("too many tasks of bad area, ip:{}, area:{}", ip, area);
                }else if (!area.contains("中国") && areaNum > MAX_DAILY_TASK_BAD_COUNTRY){
                    isTooMany = true;
                    logger.warn("too many tasks of bad country, ip:{}, area:{}", ip, area);
                }
            }
        }

        return isTooMany;
    }


    public int getTaskNumFromArea(String area){
        int num = 0;
        try {
            String areaKey = RedisUtil.buildIpAreaKey(area);
            String numStr = cacheDao.get(areaKey);
            num = NumberUtil.safeParseInt(numStr);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return num;
    }

    /**
     * 根据ip获取接过任务的数量
     * @param ip
     * @return
     */
    public int getTaskNumFromIp(String ip){
        int num = 0;
        try {
            String key = RedisUtil.buildIOSTaskIp(ip);
            String numStr = cacheDao.get(key);
            num = NumberUtil.safeParseInt(numStr);
        }catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return num;
    }

    /**
     * 接任务成功后，缓存接任务的ip信息
     * @param ip
     * @return
     */
    public void cacheTaskUserIp(String ip){
        try {
            //保存上一个接任务的ip
            String key = RedisUtil.getLastIOSTaskIpKey();
            cacheDao.set(key, ip);

            cacheIPForTTL(ip, taskIpTTL);

            //保存接任务的ip数量
            String numKey = RedisUtil.buildIOSTaskIp(ip);
            cacheDao.incr(numKey);
            cacheDao.expire(numKey, DateUtil.getSecondToMidnight());

            //缓存每个地区的任务数量
            String area = IpAddressUtil.getAddress(ip);
            String areaKey = RedisUtil.buildIpAreaKey(area);

            cacheDao.incr(areaKey);
            cacheDao.expire(areaKey, DateUtil.getSecondToMidnight());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 用户放弃任务后，删除ip缓存
     * @param ip
     */
    public void removeTaskIPCache(String ip){
        try {
            String intervalKey = RedisUtil.buildIOSIPIntervalKey(ip);
            cacheDao.remove(intervalKey);

            String numKey = RedisUtil.buildIOSTaskIp(ip);
            cacheDao.decr(numKey);

            //缓存每个地区的任务数量
            String area = IpAddressUtil.getAddress(ip);
            String areaKey = RedisUtil.buildIpAreaKey(area);

            cacheDao.decr(areaKey);
        } catch (Exception e) {

        }
    }


    /**
     * 限制访问频繁的ip，ttl为限制多少秒
     * @param ip
     */
    public void cacheIPForTTL(String ip, int ttl){
        try {
            //控制ios接任务的时间, 30s只能接一个
            String intervalKey = RedisUtil.buildIOSIPIntervalKey(ip);
            cacheDao.set(intervalKey, "1", ttl);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }


    /**
     * 根据ip所在地区封禁ip访问时间，限制3分钟
     * @param ip
     */
    protected void punishTooFreqIPByArea(String ip){
        if (isIPFromBadArea(ip)){ //限制180秒
            cacheIPForTTL(ip, 180);
            logger.warn("punish ip 180 second:{}", ip);
        }
    }


    /**
     * ip地址是不是重灾区，国外也属于重灾区
     * @param ip
     * @return
     */
    public boolean isIPFromBadArea(String ip){
        boolean isBadIp = false;
        String area = IpAddressUtil.getAddress(ip);
        if (area != null) {
            for (String item : BAD_IP_AREA) {
                if (area.endsWith(item)) {
                    isBadIp = true;
                    break;
                }
            }

            //中国以外的ip，也加以限制
            if (!area.contains("中国")){
                isBadIp = true;
            }
        }
        return isBadIp;
    }
    /***
     * 判断是不是相同地区的ip 前提是非私网ip 
     * @param ip
     * @param ip2
     * @return
     */
    public boolean isSameAddressIp(String ip,String ip2){
    	if(StringUtil.isBlank(ip) || StringUtil.isBlank(ip2)){
    		return false;
    	}

        if (ip.equals(ip2)){
            return true;
        }

        String area = IpAddressUtil.getAddress(ip);
        String area2 = IpAddressUtil.getAddress(ip2);
        return area.equals(area2);
    }


    /**
     * 获取提现账号的作弊分数，分数越高，作弊嫌疑越重
     * @param request
     * @param user
     * @param enchashmentAccount
     * @return
     */
    public Pair<Integer, String> getEnchashAccountCheatScore(HttpServletRequest request, User user, UserEnchashmentAccount enchashmentAccount){
        int score = 0;
        StringBuilder detail = new StringBuilder();

        String ip = RequestUtil.getIpAddr(request);
        if (isIPFromBadArea(ip)){
            score += 5;
            detail.append("5;ip来自作弊重灾区:").append(IpAddressUtil.getAddress(ip)).append("\n");
        }
        if (StringUtil.isBlank(user.getAvatar())){
            score += 5;
            detail.append("5;用户未设置头像").append("\n");
        }
        if( StringUtil.isBlank(user.getCountry()) || StringUtil.isBlank(user.getProvince())
                ||StringUtil.isBlank(user.getCity())){
            score += 10;
            detail.append("10;用户的地区信息未设置").append("\n");
        }
        if (StringUtil.isBlank(user.getMobile()) || user.getMobile().startsWith("170") || user.getMobile().startsWith("171")){
            score += 5;
            detail.append("5;用户未设置手机号或使用虚拟运营商").append("\n");
        }

        //判断用户上次登录ip的区域，必须和提现的ip在同一区域
        String userLastLoginIp = userLoginRecordService.getUserLastLoginIp(user.getId());
        String userLastLoginArea = IpAddressUtil.getAddress(userLastLoginIp);
        String userRequestArea = IpAddressUtil.getAddress(ip);

        if (StringUtil.isBlank(userLastLoginArea) || StringUtil.isBlank(userRequestArea)){
            score += 10;
            detail.append("10;用户提现或登录ip区域为空，登录：").append(userLastLoginArea).append("，提现：").append(userRequestArea).append("\n");
        }else if(!userLastLoginArea.equals(userRequestArea)){
            if (userLastLoginArea.contains(userRequestArea) || userRequestArea.contains(userLastLoginArea)) {
                score += 5;
                detail.append("5;用户提现或登录ip区域不在同一城市，登录：").append(userLastLoginArea).append("，提现：").append(userRequestArea).append("\n");
            }else {
                score += 15;
                detail.append("15;用户提现和登录ip区域不一致，登录：").append(userLastLoginArea).append("，提现：").append(userRequestArea).append("\n");
            }
        }

        if (StringUtil.isNotBlank(user.getMobile())) {
            //判断用户绑定手机号的归属地
            CellPhoneArea userMobileArea = cellphoneAreaService.getCellPhoneArea(user.getMobile());

            //手机归属地必须和用户登录ip一致，不同城市，10分，不同省15分
            if (userMobileArea == null || !userLastLoginArea.contains(userMobileArea.getProvince()) || !userLastLoginArea.contains(userMobileArea.getCity()) ) {
                int mobileScore = 10;

                if (!userLastLoginArea.contains(userMobileArea.getProvince())){
                    mobileScore = 15;
                }
                score += mobileScore;

                detail.append(mobileScore + ";用户绑定的手机号和登录ip区域不一致，登录：").append(userLastLoginArea).append("，手机号：").append(userMobileArea).append("\n");
            }
        }

        return new Pair<>(score, detail.toString());
    }


    /**
     * 获取用户提现的作弊分数，目前满分30分
     * @param request
     * @param user
     * @param userEnchashment
     * @return
     */
    public Pair<Integer, String> getUserEnchashCheatScore(HttpServletRequest request,User user,UserEnchashment userEnchashment){
        int score = 0;
        StringBuilder detail = new StringBuilder();

        //判断用户上次登录ip的区域，必须和提现的ip在同一区域
        String userLastLoginIp = userLoginRecordService.getUserLastLoginIp(user.getId());
        String userRequestIp = userEnchashment.getIp();

        String userLastLoginArea = IpAddressUtil.getAddress(userLastLoginIp);
        String userRequestArea = IpAddressUtil.getAddress(userRequestIp);

        if (StringUtil.isBlank(userLastLoginArea) || StringUtil.isBlank(userRequestArea)){
            score += 10;
            detail.append("10;用户提现或登录ip区域为空，登录：").append(userLastLoginArea).append("，提现：").append(userRequestArea).append("\n");
        }else if(!userLastLoginArea.equals(userRequestArea)){
            if (userLastLoginArea.contains(userRequestArea) || userRequestArea.contains(userLastLoginArea)) {
                score += 5;
                detail.append("5;用户提现或登录ip区域不在同一城市，登录：").append(userLastLoginArea).append("，提现：").append(userRequestArea).append("\n");
            }else {
                score += 15;
                detail.append("15;用户提现和登录ip区域不一致，登录：").append(userLastLoginArea).append("，提现：").append(userRequestArea).append("\n");
            }
        }

        String  account = userEnchashment.getAccount();
        //如果是邮件，则只能全包含字母或者全部是数字
        int atIndex = account.indexOf("@");
        if( atIndex != -1){
            account = account.substring(0, atIndex);
            if(!StringUtil.isNumeric(account) && !StringUtil.isAlpha(account)){
                score += 5;
                detail.append("5;用户的提现账号不是全字母或数字").append("\n");
            }
        }else{
            //是手机
            //判断用户绑定手机号的归属地
            CellPhoneArea userMobileArea = cellphoneAreaService.getCellPhoneArea(user.getMobile());
            CellPhoneArea accountArea = cellphoneAreaService.getCellPhoneArea(account);
            if(accountArea == null || !accountArea.equals(userMobileArea)){
                score += 10;
                detail.append("10;用户提现手机号与其绑定的手机号区域不一致，提现：").append(accountArea).append(",绑定手机号：").append(userMobileArea).append("\n");
            }
        }

        //判断用户是否更换过5次或以上次数的idfa
        if(userLoginRecordService.isExistUserChangeFiveTimesIdfa(user.getId())){
            score += 5;
            detail.append("5;用户更换过5次及以上的idfa").append("\n");
        }

        return new Pair<>(score, detail.toString());
    }



    /***
     * 正常用户直接审核通过<br/>
     * 1、用户的省市信息不为空
	2、用户提现的ip与登录的ip在同一个城市，与其手机归属地一致
	3、如果支付宝账号是手机号：账号手机号与用户绑定的手机号一致或者归属地一致；如果是邮箱账号，邮箱账号只包含数字，或者只包含字母。
   	4、idfa经常变化的
     * @param request
     * @param user
     * @return
     */
    public boolean isAutoPassEnchashment(HttpServletRequest request,User user,UserEnchashment userEnchashment){
    	if(userEnchashment.getType() == UserEnchashment.ACCOUNT_TYPE_WX){
    		return false;
    	}
    	if(user.isBlack()
    			||StringUtil.isBlank(user.getProvince()) 
    			||StringUtil.isBlank(user.getCity())
    			||StringUtil.isBlank(user.getMobile())){
    		return false;
    	}

        //判断用户上次登录ip的区域，必须和提现的ip在同一区域
    	String userLastLoginIp = userLoginRecordService.getUserLastLoginIp(user.getId());
    	String userRequestIp = userEnchashment.getIp();
    	if(StringUtil.isBlank(userRequestIp) || StringUtil.isBlank(userLastLoginIp)){
    		return false;
    	}
    	String userLastLoginArea = IpAddressUtil.getAddress(userLastLoginIp);
    	String userRequestArea = IpAddressUtil.getAddress(userRequestIp);
    	if(!userLastLoginArea.equals(userRequestArea)){
    		return false;
    	}

        //判断用户绑定手机号的归属地
    	CellPhoneArea userMobileArea = cellphoneAreaService.getCellPhoneArea(user.getMobile());
    	if(userMobileArea == null 
    			|| userMobileArea.getProvince() == null 
    			|| userMobileArea.getCity() == null){
    		return false;
    	}

        //手机归属地必须和用户登录ip一直
    	if(! userLastLoginArea.contains(userMobileArea.getProvince()) 
    			|| !userLastLoginArea.contains(userMobileArea.getCity())){
    		return false;
    	}
    	
    	String  account = userEnchashment.getAccount();
    	//如果是邮件，则只能全包含字母或者全部是数字
    	int atIndex = account.indexOf("@");
    	if( atIndex != -1){
    		account = account.substring(0, atIndex);
    		if(!StringUtil.isNumeric(account) && !StringUtil.isAlpha(account)){
    			return false;
    		}
    	}else{
    		//是手机
    		CellPhoneArea accountArea = cellphoneAreaService.getCellPhoneArea(account);
    		if(accountArea == null || !accountArea.equals(userMobileArea)){
    			return false;
    		}
    	}

        //判断用户是否更换过5次或以上次数的idfa
    	if(userLoginRecordService.isExistUserChangeFiveTimesIdfa(user.getId())){
    		return false;
    	}
    	return true;
    }

    /**
     * 异步为提现账号评分
     * @param request
     * @param u
     * @param userAccount
     * @param userEnchashment
     */
    public void evaluateUserEnchash(HttpServletRequest request, User u, UserEnchashmentAccount userAccount, UserEnchashment userEnchashment) {
        try{
            antiCheatExecutor.execute(() -> {
                //首先获取提现账户的评分
                int ascore = userAccount.getScore();
                if (userAccount.getScore() < 0){ //未评分
                    Pair<Integer, String> accountScore = getEnchashAccountCheatScore(request, u, userAccount);
                    userEnchashmentService.setEncashAccountScore(u.getId(), accountScore.getFirst(), accountScore.getSecond());

                    ascore = accountScore.getFirst();

                    logger.info("用户的提现账户作弊评分，user:{}, account:{}, score:{}, detail:{}", u.getId(), userAccount.getAlipay_account(), accountScore.getFirst(), accountScore.getSecond());
                }

                Pair<Integer, String> enchashCheatScore = getUserEnchashCheatScore(request, u, userEnchashment);
                int enchashScore = enchashCheatScore.getFirst();
                String detail = enchashCheatScore.getSecond();

                if (ascore > 0){
                    enchashScore += ascore;
                    detail = ascore + ";提现账户作弊评分\n" + detail;
                }

                userEnchashmentService.setUserEncashScore(u.getId(), enchashScore, detail);

                logger.info("用户的提现作弊评分，user:{}, account:{}, amount:{}, score:{}, detail:{}", u.getId(), userAccount.getAlipay_account(), userEnchashment.getAmount(), enchashScore, detail);
            });
        }catch (Exception exp){}
    }

    /**
     * 异步为用户提现评分
     * @param request
     * @param u
     * @param account
     */
    public void evaluateEnchashAccount(HttpServletRequest request, User u, UserEnchashmentAccount account) {
        try{
            antiCheatExecutor.execute(() -> {
                Pair<Integer, String> accountScore = getEnchashAccountCheatScore(request, u, account);
                userEnchashmentService.setEncashAccountScore(u.getId(), accountScore.getFirst(), accountScore.getSecond());

                logger.info("用户的提现账户评分，user:{}, account:{}, score:{}, detail:{}", u.getId(), account.getAlipay_account(), accountScore.getFirst(), accountScore.getSecond());
            });
        }catch (Exception exp){}

    }
}
