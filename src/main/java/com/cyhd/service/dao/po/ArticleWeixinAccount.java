package com.cyhd.service.dao.po;

import java.io.Serializable;

/**
 * 转发任务要推广的微信公众号
 * Created by hy on 9/29/15.
 */
public class ArticleWeixinAccount implements Serializable{
    private int id;
    private String name;
    private String title;
    private String logo;
    private String account;
    private String qrcode;
    private int status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "ArticleWeixinAccount{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", qrcode='" + qrcode + '\'' +
                ", status=" + status +
                '}';
    }
}
