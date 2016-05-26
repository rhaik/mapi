package com.cyhd.service.impl.doubao;

import com.cyhd.service.dao.db.mapper.doubao.UserDuobaoCoinLogMapper;
import com.cyhd.service.dao.db.mapper.doubao.UserDuobaoCoinMapper;
import com.cyhd.service.dao.po.doubao.UserDuobaoCoin;
import com.cyhd.service.dao.po.doubao.UserDuobaoCoinLog;
import com.cyhd.service.impl.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by hy on 1/18/16.
 */
@Service
public class UserDuobaoCoinService extends BaseService {

    /**
     * 充值金额及省的金额，以元为单位
     */
    public enum RechargeAmount {

        TWO_COINS(2, 0), FIVE_COINS(5, 0), TEN_COINS(10, 1), FIFTEEN_COINS(15, 1.5f), TWENTY_COINS(20, 2), THIRTY_COINS(30, 3);

        int amount = 0;
        float reserved = 0;

        RechargeAmount(int amount, float reserved){
            this.amount = amount;
            this.reserved = reserved;
        }

        public int getAmount() {
            return amount;
        }

        public float getReserved() {
            return reserved;
        }

        public float getPayAmount(){
            return amount - reserved;
        }

        /**
         * 根据数量获取充值的配置
         * @param amount
         * @return
         */
        public static RechargeAmount getByAmount(int amount){
            for (RechargeAmount rechargeAmount : RechargeAmount.values()){
                if (rechargeAmount.getAmount() == amount){
                    return rechargeAmount;
                }
            }
            return null;
        }
    }


    @Resource
    UserDuobaoCoinMapper duobaoCoinMapper;

    @Resource
    UserDuobaoCoinLogMapper duobaoCoinLogMapper;


    public int add(UserDuobaoCoin userIncome) {
        return duobaoCoinMapper.add(userIncome);
    }

    /**
     * 增加用户的夺宝币
     * @param userId
     * @param amount
     * @return
     */
    public boolean addDuobaoCoin(int userId, int amount) {
        boolean flag = duobaoCoinMapper.addDuobaoCoin(userId, amount) > 0;
        if (flag){
            addUserDuobaoCoinLog(userId, 0, amount, UserDuobaoCoinLog.DuobaoLogAction.BALANCE_RECHARGE, "充值");
        }
        logger.info("addDuobaoCoin, userId={}, amount={}, result:{}", userId, amount, flag);
        return flag;
    }

    public UserDuobaoCoin getUserDuobaoCoin(int userId) {
        return duobaoCoinMapper.getUserDuobaoCoin(userId);
    }

    public long getTotalIncomes() {
        return duobaoCoinMapper.getTotalIncomes();
    }

    /**
     * 增加用户的夺宝币
     * @param userId
     * @param amount
     * @return
     */
    public boolean useCoinByDuobao(int userId, int amount, int duobaoOrderId, String productName) {
        boolean flag =  duobaoCoinMapper.userCoinByDuobao(userId, amount) > 0;
        if (flag){
            addUserDuobaoCoinLog(userId, duobaoOrderId, amount, UserDuobaoCoinLog.DuobaoLogAction.DUOBAO, productName);
        }
        logger.info("useCoinByDuobao, userId:{}, amount:{}, productName:{}, result:{}", userId, amount, productName, flag);
        return flag;
    }

    /**
     * 返回用户夺宝中金额
     * @param userId
     * @param amount
     * @return
     */
    public boolean returnUserDuobaoBalance(int userId, int amount, int orderProudctId) {
        boolean flag =  duobaoCoinMapper.returnUserDuobaoBalance(userId, amount) > 0;
        if (flag){
            addUserDuobaoCoinLog(userId, orderProudctId, amount, UserDuobaoCoinLog.DuobaoLogAction.RETURN_FEE, "退还夺宝币");
        }
        logger.info("returnUserDuobaoBalance, userId:{}, amount:{}, orderProductId:{}, result:{}", userId, amount, orderProudctId, flag);
        return flag;
    }

    /**
     * 用户完成夺宝，减少夺宝中金额
     * @param userId
     * @param amount
     * @return
     */
    public boolean finishUserDuobao(int userId, int amount) {
        boolean flag = duobaoCoinMapper.finishUserDuobao(userId, amount) > 0;
        return flag;
    }


    /**
     * 增加夺宝记录
     * @param userId
     * @param duobaoProductId
     * @param amount
     * @param logAction
     * @param remarks
     * @return
     */
    private boolean addUserDuobaoCoinLog(int userId, int duobaoProductId, int amount, UserDuobaoCoinLog.DuobaoLogAction logAction, String remarks) {
        UserDuobaoCoinLog duobaoCoinLog = new UserDuobaoCoinLog();
        duobaoCoinLog.setUser_id(userId);
        duobaoCoinLog.setDuobao_product_id(duobaoProductId);
        duobaoCoinLog.setAmount(amount);
        duobaoCoinLog.setAction(logAction.action);
        duobaoCoinLog.setType(logAction.type);
        duobaoCoinLog.setOperator_time(new Date());
        duobaoCoinLog.setRemarks(remarks);

        return duobaoCoinLogMapper.add(duobaoCoinLog) > 0;
    }
    /**
     * 抽奖的
     * 增加用户的夺宝币
     * @param userId
     * @param amount
     * @return
     */
    public boolean addDuobaoCoinByDraw(int userId, int amount,String reason) {
        boolean flag = duobaoCoinMapper.addDuobaoCoin(userId, amount) > 0;
        if (flag){
            addUserDuobaoCoinLog(userId, 0, amount, UserDuobaoCoinLog.DuobaoLogAction.DRAW_ADD, reason);
        }
        logger.info("addDuobaoCoin, userId={}, amount={}, result:{}", userId, amount, flag);
        return flag;
    }
}
