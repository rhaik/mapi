package com.cyhd.service.dao.po;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cyhd.service.constants.Constants;
import com.cyhd.service.util.EmojUtil;
import com.cyhd.service.util.GlobalConfig;

public class User implements Serializable {
	
	private static final long serialVersionUID = 6229588907878358040L;
	
	private int id;
	private int groupid;
	private int user_identity;
	private String avatar;
	private String name;
	private int sex;
	private String realname;
	private String mobile;
	private String openid;
	private String unionid;
	private String invite_code;
	private String country;
	private String province;
	private String city;
	private int masked;
	private int devicetype;
	private String did;
	private String idfa;
	private int user_label_id;
	private String ticket;
	private Date createtime;
	private Date lastlogintime;
	private int source;     //0:默认，1：用户邀请， 其他：money_source表
	private Date bindtime;
	private  String user_qrcode;
	private Date qrcode_time;
	private int task_property;   //任务属性，按位运算，每一位表示一个新手任务，0：未完成，1：已完成
	
	public static final int TASK_INVITE = 0x0001;		//邀请好友任务
	public static final int TASK_SHARE = TASK_INVITE << 1;  //分享到朋友圈任务
	public static final int TASK_APP = TASK_INVITE << 2;  //试用app任务
	
	public static final int TASK_TRAN_ARTICLE = TASK_INVITE << 3; //新手的转发任务，转发收徒页面

	/**关注微信工作号的是不是新用户,发过奖励*/
	public static final int NEW_USER_FLAG = TASK_INVITE << 4;

	/**
	 * 钥匙版安装入口的任务
	 */
	public static final int TASK_YAOSHI_CLIP = TASK_INVITE << 5;


	public boolean isIos(){
		return this.devicetype == Constants.platform_ios;
	}
	
	public boolean isTaskInviteComplete(){
		return (this.task_property & TASK_INVITE) == TASK_INVITE;
	}
	
	public boolean isTaskShareComplete(){
		return (this.task_property & TASK_SHARE) == TASK_SHARE;
	}
	
	public boolean isTaskAppComplete(){
		return (this.task_property & TASK_APP) == TASK_APP;
	}
	
	public boolean isTranArticleComplete(){
		return (this.task_property & TASK_TRAN_ARTICLE) == TASK_TRAN_ARTICLE;
	}
	
	public boolean isRewardNewUserComplete(){
		return (this.task_property & NEW_USER_FLAG) == NEW_USER_FLAG;
	}

	/**
	 * 钥匙版安装入口的任务是否已完成
	 * @return
	 */
	public boolean isYaoshiClipComplete() {
		return (this.task_property & TASK_YAOSHI_CLIP) == TASK_YAOSHI_CLIP;
	}
	
	//是否生成过分享图片的标识 
	//1生成过
	private int gen_share_pic;
	/**生成过分享图片*/
	public static final int IS_GEN_SHARE_PIC = 1;
	
	//是否被封（黑名单）
	public boolean isBlack(){
		return this.getMasked() > 0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getGroupid() {
		return groupid;
	}
	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
	public int getUser_identity() {
		return user_identity;
	}
	public void setUser_identity(int user_identity) {
		this.user_identity = user_identity;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	
	public String getHeadImg(){
		if(avatar == null || avatar.isEmpty()){
			return GlobalConfig.default_avatar;
		}
		return avatar;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getUniName(){
		if(name == null || name.trim().length() == 0){
			return "小赚";
		}
		return EmojUtil.toCommonString(name);
	}
	
	public String getRawName(){
		if(name == null || name.trim().length() == 0){
			return "小赚";
		}
		String n= EmojUtil.removeEmoj(name);
		if(n == null || n.trim().length() == 0){
			return "小赚";
		}
		return n;
	}
	
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	
	public String getDid() {
		return did;
	}
	public void setDid(String did) {
		this.did = did;
	}

	public int getUser_label_id() {
		return user_label_id;
	}
	public void setUser_label_id(int user_label_id) {
		this.user_label_id = user_label_id;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public Date getLastlogintime() {
		return lastlogintime;
	}
	public void setLastlogintime(Date lastlogintime) {
		this.lastlogintime = lastlogintime;
	}
	public int getSource() {
		return source;
	}
	public void setSource(int source) {
		this.source = source;
	}
	public String getTicket() {
		return ticket;
	}
	public void setTicket(String ticket) {
		this.ticket = ticket;
	}
	
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getUnionid() {
		return unionid;
	}
	public void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getDevicetype() {
		return devicetype;
	}
	public void setDevicetype(int devicetype) {
		this.devicetype = devicetype;
	}

	public Date getBindtime() {
		return bindtime;
	}
	public void setBindtime(Date bindtime) {
		this.bindtime = bindtime;
	}
	public int getMasked() {
		return masked;
	}
	public void setMasked(int masked) {
		this.masked = masked;
	}

	public String getInvite_code() {
		return invite_code;
	}

	public void setInvite_code(String invite_code) {
		this.invite_code = invite_code;
	}

	public int getTask_property() {
		return task_property;
	}

	public void setTask_property(int task_property) {
		this.task_property = task_property;
	}
	
	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public int getRegistrationDay() {
		Date current = new Date();
		long total = current.getTime() - this.createtime.getTime();
		int today = (int)(total / (24 * 60 * 60 * 1000));
		return today > 0 ? today : 1; 
	}

	public boolean isGen_share_pic() {
		return gen_share_pic == IS_GEN_SHARE_PIC;
	}

	public void setGen_share_pic(int gen_share_pic) {
		this.gen_share_pic = gen_share_pic;
	}

	public String getUser_qrcode() {
		return user_qrcode;
	}

	public void setUser_qrcode(String user_qrcode) {
		this.user_qrcode = user_qrcode;
	}

	public Date getQrcode_time() {
		return qrcode_time;
	}

	public void setQrcode_time(Date qrcode_time) {
		this.qrcode_time = qrcode_time;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(500);
		sb.append("User{").append(
				"id=" ).append( id ).append(
				", user_identity=" ).append( user_identity ).append(
				", name='" ).append( name ).append( '\'').append(
				", mobile='" ).append( mobile ).append( '\'' ).append(
				", unionid='" ).append( unionid ).append( '\'' ).append(
				", idfa='" ).append( idfa).append('\'').append(
				", did='").append( did ).append( '\'' ).append(
				'}');
		return sb.toString();
	}


	public Map<String, Object> getBasicInfo(){
		Map<String, Object> infoMap = new HashMap<>();

		infoMap.put("id", "" + getUser_identity());
		infoMap.put("avatar", getHeadImg());
		infoMap.put("name", getName());
		infoMap.put("mobile", getMobile()==null? "" : getMobile());

		return infoMap;
	}
	public String getHideMobile() {
		if(this.mobile!=null && !this.mobile.isEmpty()) {
			return this.mobile.substring(0, 3) + "****" + this.mobile.substring(7);
		}
		return "";
	}
}
