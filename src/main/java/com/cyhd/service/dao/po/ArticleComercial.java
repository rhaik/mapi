package com.cyhd.service.dao.po;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.Serializable;

/**
 * 转发文章对应的广告
 * Created by hy on 9/29/15.
 */
public class ArticleComercial implements Serializable{
    private int id;
    private String name;
    private String content;
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

    public String getContent() {
        return content == null ?"": StringEscapeUtils.unescapeHtml(content);
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ArticleComercial{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", status=" + status +
                '}';
    }
}
