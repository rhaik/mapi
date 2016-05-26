package com.cyhd.service.dao.po.doubao;

import java.util.Date;

/**
 * 用户的夺宝币
 * Created by hy on 1/18/16.
 */
public class UserDuobaoCoin {

    private int user_id;
    private int balance;
    private int income;
    private int duobaoing;
    private int duobao_total;
    private Date updatetime;


    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getIncome() {
        return income;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public int getDuobaoing() {
        return duobaoing;
    }

    public void setDuobaoing(int duobaoing) {
        this.duobaoing = duobaoing;
    }

    public int getDuobao_total() {
        return duobao_total;
    }

    public void setDuobao_total(int duobao_total) {
        this.duobao_total = duobao_total;
    }

    public Date getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    @Override
    public String toString() {
        return "UserDuobaoCoin{" +
                "user_id=" + user_id +
                ", balance=" + balance +
                ", income=" + income +
                ", duobaoing=" + duobaoing +
                ", duobao_total=" + duobao_total +
                ", updatetime=" + updatetime +
                '}';
    }
}
