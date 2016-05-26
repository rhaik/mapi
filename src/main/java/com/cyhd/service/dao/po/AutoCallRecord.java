package com.cyhd.service.dao.po;

import java.util.Date;

public class AutoCallRecord {
	//有人接单
	public static final int TYPE_ORDER_ACCEPTED = 1;
	
	public static final int STATE_UNCALLED = 0;  //未拨打
	public static final int STATE_CONFIRMED = 1; // 预留
	public static final int STATE_UNCONFIRMED = 2; //预留
	public static final int STATE_DELAY = 3; // 预留
	public static final int STATE_UNREACHABLE = 4; //打不通
	public static final int STATE_REACHABLE = 5; //拨通（中间状态）
	public static final int STATE_OTHER = 6; //其他未知(挂了)
	
	private long id;
	private int type;
	private long resourceid;
	private String extrainfo;
	private String content;
	private int callstate;
	private Date createtime;
	private Date calltime;
	private int callcount;
	private Date replytime;
	private int replykey;
	private String phone;
	private String subphone;
	private long oprid;
	private Date replyendtime;
	private int callstatus;
	private int endreason;
	
	public static String getCallStateDesc(int state){
		String desc="未拨打";
		switch(state){
		case STATE_UNCALLED: desc= "未拨打"; break;
		case STATE_CONFIRMED: desc="确认有"; break;
		case STATE_UNCONFIRMED: desc= "确认没有"; break;
		case STATE_DELAY: desc= "延后再打"; break;
		case STATE_UNREACHABLE: desc= "打不通"; break;
		case STATE_OTHER: desc= "挂断"; break;
		default: desc="挂断"; break;
		}
		return desc;
	}
	
	public boolean isReplied(){
		return this.replytime != null && replykey > 0;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public long getResourceid() {
		return resourceid;
	}
	public void setResourceid(long resourceid) {
		this.resourceid = resourceid;
	}
	public String getExtrainfo() {
		return extrainfo;
	}
	public void setExtrainfo(String extrainfo) {
		this.extrainfo = extrainfo;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getCallstate() {
		return callstate;
	}
	public void setCallstate(int callstate) {
		this.callstate = callstate;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getCalltime() {
		return calltime;
	}
	public void setCalltime(Date calltime) {
		this.calltime = calltime;
	}
	public Date getReplytime() {
		return replytime;
	}
	public void setReplytime(Date replytime) {
		this.replytime = replytime;
	}
	public int getReplykey() {
		return replykey;
	}
	public void setReplykey(int replykey) {
		this.replykey = replykey;
	}
	
	public int getCallcount() {
		return callcount;
	}
	public void setCallcount(int callcount) {
		this.callcount = callcount;
	}
	
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public long getOprid() {
		return oprid;
	}
	public void setOprid(long oprId) {
		this.oprid = oprId;
	}
	
	public Date getReplyendtime() {
		return replyendtime;
	}

	public void setReplyendtime(Date replyendtime) {
		this.replyendtime = replyendtime;
	}

	public int getEndreason() {
		return endreason;
	}

	public void setEndreason(int endreason) {
		this.endreason = endreason;
	}

	public int getCallstatus() {
		return callstatus;
	}

	public void setCallstatus(int callstatus) {
		this.callstatus = callstatus;
	}

	public String getSubphone() {
		return subphone;
	}

	public void setSubphone(String subphone) {
		this.subphone = subphone;
	}

	@Override
	public String toString() {
		return "AutoCallRecord [id=" + id + ", type=" + type + ", resourceid="
				+ resourceid + ", extrainfo=" + extrainfo + ", content="
				+ content + ", callstate=" + callstate + ", createtime="
				+ createtime + ", calltime=" + calltime + ", callcount="
				+ callcount + ", replytime=" + replytime + ", replykey="
				+ replykey + ", phone=" + phone + ", oprid=" + oprid
				+ ", replyendtime=" + replyendtime + ", callstatus="
				+ callstatus + ", endreason=" + endreason + "]";
	}

}
