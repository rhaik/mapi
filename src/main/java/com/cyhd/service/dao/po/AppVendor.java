package com.cyhd.service.dao.po;

/**
 *
 * 应用的厂商信息
 * Created by hy on 9/15/15.
 */
public class AppVendor {
    private int id;
    private String name;
    private String app_key;
    private String app_secret;

    private String service_name; //对应的点击服务名称

    private String click_url; //不为空表示需要点击

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

    public String getApp_key() {
        return app_key;
    }

    public void setApp_key(String app_key) {
        this.app_key = app_key;
    }

    public String getApp_secret() {
        return app_secret;
    }

    public void setApp_secret(String app_secret) {
        this.app_secret = app_secret;
    }


    public String getClick_url() {
        return click_url;
    }

    public void setClick_url(String click_url) {
        this.click_url = click_url;
    }

    public String getService_name() {
        return service_name;
    }

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    @Override
    public String toString() {
        return "AppVendor{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", app_key='" + app_key + '\'' +
                '}';
    }
}
