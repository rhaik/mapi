package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import com.cyhd.service.constants.PropertiesConstants;

/**
 * 系统key value 信息(properties)
 */
public class Properties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ckey;  //关键字类型
    
    private String scope = PropertiesConstants.SCOPE_TEST; //服务器类型，

    private String cvalue;//关键值

    private Integer estate;//是否有效 1 有效  2 无效

    private Date createtime;//创建时间

    private Date updatetime;//修改时间

    public String getCkey()
    {
        return ckey;
    }

    public void setCkey(String ckey)
    {
        this.ckey = ckey;
    }

    public String getCvalue()
    {
        return cvalue;
    }

    public void setCvalue(String cvalue)
    {
        this.cvalue = cvalue;
    }

    public Integer getEstate()
    {
        return estate;
    }

    public void setEstate(Integer estate)
    {
        this.estate = estate;
    }

    public Date getCreatetime()
    {
        return createtime;
    }

    public void setCreatetime(Date createtime)
    {
        this.createtime = createtime;
    }

    public Date getUpdatetime()
    {
        return updatetime;
    }

    public void setUpdatetime(Date updatetime)
    {
        this.updatetime = updatetime;
    }

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}
	
	public void setScopeDeploy(){
		this.scope = PropertiesConstants.SCOPE_DEPLOY;
	}

	public void setScopeTest(){
		this.scope = PropertiesConstants.SCOPE_TEST;
	}
	
	@Override
	public String toString() {
		return "Properties [ckey=" + ckey + ", scope=" + scope + ", cvalue=" + cvalue + ", estate=" + estate + ", createtime=" + createtime + ", updatetime="
				+ updatetime + "]";
	}

}
