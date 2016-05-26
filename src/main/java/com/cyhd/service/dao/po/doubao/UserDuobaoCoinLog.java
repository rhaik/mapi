package com.cyhd.service.dao.po.doubao;

import java.util.Date;

/**
 * 用户的夺宝币记录
 * Created by hy on 1/18/16.
 */
public class UserDuobaoCoinLog {

    //action小于10：充值，大于10小于20：使用夺宝币，大于20：退还夺宝币 2 抽奖
    public enum DuobaoLogAction {
        BALANCE_RECHARGE(1, 1), DUOBAO(11, 0), RETURN_FEE(21,1),
    	DRAW_ADD(2,1);
    	
        public int action;
        public int type;

        DuobaoLogAction(int v, int t){
            action = v;
            type = t;
        }

        public static DuobaoLogAction valueOf(int v){
            for (DuobaoLogAction action : DuobaoLogAction.values()){
                if (action.action == v){
                    return action;
                }
            }
            return null;
        }
    }

    private int user_id;
    private int duobao_product_id;
    private int action; //action小于10：充值，大于10小于20：使用夺宝币，大于20：退还夺宝币
    private int amount;
    private int total_amount;
    private int type;  //1:增加0减少
    private Date operator_time;
    private String remarks;


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDuobao_product_id() {
        return duobao_product_id;
    }

    public void setDuobao_product_id(int duobao_product_id) {
        this.duobao_product_id = duobao_product_id;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Date getOperator_time() {
        return operator_time;
    }

    public void setOperator_time(Date operator_time) {
        this.operator_time = operator_time;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
