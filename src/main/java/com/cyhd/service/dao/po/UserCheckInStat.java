package com.cyhd.service.dao.po;

import java.io.Serializable;

/**
 * 用户签到统计数据，主要包括累计天数和累计收益两项
 * Created by hy on 9/16/15.
 */
public class UserCheckInStat implements Serializable{
    int total_days;
    int total_income;
    int user_id;

    public UserCheckInStat(){

    }

    public UserCheckInStat(int userId){
        this.user_id = userId;
    }

    public int getTotal_days() {
        return total_days;
    }

    public void setTotal_days(int total_days) {
        this.total_days = total_days;
    }

    public int getTotal_income() {
        return total_income;
    }

    public void setTotal_income(int total_income) {
        this.total_income = total_income;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
