package com.cyhd.service.dao.po;

import java.io.Serializable;

/**
 * 有时间信息id生成(id_withtimemaker)
 */
public class IdWithTimeMaker implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;//主键id，自动递增

    private String mark;//

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getMark()
    {
        return mark;
    }

    public void setMark(String mark)
    {
        this.mark = mark;
    }
}
