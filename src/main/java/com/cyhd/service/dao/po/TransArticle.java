package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.util.IdEncoder;

public class TransArticle implements Serializable {

	/**
	 */
	private static final long serialVersionUID = -7382206326857844791L;
	
	private int id;
	private String name;
	private String url;
	private String content;
	private String img;
	private int status;		// 状态(0:未审核1:已审核2:审核未通过,3:删除
	private int state;     //'1,--有效，2--无效',
	private int creater;
	private Date createtime;
	private int auditor;
	private Date audit_time;
	private String description;
	private int type; //文章类：1:应用内分享文章，2：微信内分享的文章
	private int read_num; //阅读数量
	private int zan_num; //赞的数量

	
	public String getEncodedId(){
		return IdEncoder.encode(id);
	}

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


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}


	public int getCreater() {
		return creater;
	}


	public void setCreater(int creater) {
		this.creater = creater;
	}


	public Date getCreatetime() {
		return createtime;
	}


	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}


	public int getAuditor() {
		return auditor;
	}


	public void setAuditor(int auditor) {
		this.auditor = auditor;
	}


	public Date getAudit_time() {
		return audit_time;
	}


	public void setAudit_time(Date audit_time) {
		this.audit_time = audit_time;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public String getImg() {
		return img;
	}


	public void setImg(String img) {
		this.img = img;
	}
	
	public String getDefaultImg(){
		if(StringUtils.isBlank(this.getImg())){
			return Constants.ARTICLE_DEFAULT_IMG;
		}
		return img;
	}

	public String getContent() {
		return content == null ?"":StringEscapeUtils.unescapeHtml(content);
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getRead_num() {
		return read_num;
	}

	public void setRead_num(int read_num) {
		this.read_num = read_num;
	}

	public int getZan_num() {
		return zan_num;
	}

	public void setZan_num(int zan_num) {
		this.zan_num = zan_num;
	}

	public void setRealReadNum(int readNum) {
		this.read_num += readNum;
		this.zan_num += readNum / (id % 13 + 10);
	}


	public long getHiddenId(){
		return getHiddenId(id);
	}

	public static long getHiddenId(int articleId){
		return articleId * 97 + 997;
	}

	public static int fromHiddenId(long hid){
		return (int)((hid - 997)/ 97);
	}
}
