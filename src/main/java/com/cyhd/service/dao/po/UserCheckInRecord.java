package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户每日签对应的数据
 * Created by hy on 9/16/15.
 */
public class UserCheckInRecord implements Serializable{
    int id;
    int user_id;
    int income; //本次签到的收入
    int days; //本次签到连续的天数
    Date checkin_time; //签到时间


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public Date getCheckin_time() {
        return checkin_time;
    }

    public void setCheckin_time(Date checkin_time) {
        this.checkin_time = checkin_time;
    }

    @Override
    public String toString() {
        return "UserCheckInRecord{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", income=" + income +
                ", days=" + days +
                ", checkin_time=" + checkin_time +
                '}';
    }
}
