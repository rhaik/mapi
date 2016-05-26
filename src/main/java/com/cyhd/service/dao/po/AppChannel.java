package com.cyhd.service.dao.po;

import java.io.Serializable;

import com.cyhd.common.util.StringUtil;

public class AppChannel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 401861482548099220L;
	private int id;
	private String channel;
	private String channel_id;
	private String click_url;
	private String callback_url;
	private String query_url;
	private int status;
	/**快速任务的服务名**/
	private String service_name;
	
	
	public static final int CHANNEL_JUYOUQIAN = 1; //聚有钱
	public static final int CHANNEL_QUMI = 2; //趣米
	public static final int CHANNEL_DIANRU = 3; //点入
	public static final int CHANNEL_MEITU = 4; //美图
	public static final int CHANNEL_ZHANGSHANG = 5; //掌上
	public static final int CHANNEL_YOUMI = 6; //有米
	public static final int CHANNEL_YOUMI_DISTINCT = 7; //有米(排重)
	public static final int CHANNEL_ADSAGE = 8; //艾德思奇
	public static final int CHANNEL_ZHIMENG = 9; //指盟
	public static final int CHANNEL_DIANRU_DISTINCT = 10; //点入排重
	public static final int CHANNEL_51HB = 11; //51红包
	public static final int CHANNEL_QUMI_DISTINCT = 12; //趣米(排重)
	/**磨盘的排重和点击*/
	public static final int CHANNEL_MOPAN = 13;
	/**磨盘的排重和点击带回调地址的**/
	public static final int CHANNEL_MOPAN_CALLBACK = 14;
	/**掉钱眼不回调*/
	public static final int CHANNEL_DIAOQIANYANER_NOT_CALLBACK = 15;
	/**友钱 赚友*/
	public static final int CHANNEL_YOUQIAN_CALLBACK = 16;
	/**行者天下**/
	public static final int CHANNEL_XINZHETIANXIA_CALLBACK = 17;
	/***51红包回调*/
	public static final int CHANNEL_51HONGBAO_CALLBACK = 20;
	/***爱普友邦*/
	public static final int CHANNEL_AIPUYOUBANG_CALLBACK = 18;
	/**多顽回调*/
	public static final int CHANNEL_DUOWAN_CALLBACK = 19;
	/***米迪积分墙*/
	public static final int CHANNEL_MIDI_CALLBACK = 21;
	/***铜板墙 排重*/
	public static final int CHANNEL_TONGBANQIANG_NOT_DISTINCT = 22;
	/***爱普友邦 新**/
	public static final int CHANNEL_AIPUYOUBANG_NEW_NOT_CALLBACK = 23;
	/***聚有钱 回调*/
	public static final int CHANNEL_JUYOUQIAN_CALLBACK = 24;
	
	/***懒猫排重*/
	public static final int CHANNEL_LANMAO_DISTINCT = 25;
	/**爱普友邦 回调**/
	public static final int CHANNEL_AIPUYOUBANG_NEW_CALLBACK = 26;
	/**友钱(泉州澎湃)**/
	public static final int CHANNEL_YOUQIAN_QZPP_NOT_CALLBACK = 27;
	/**秒乐排重**/
	public static final int CHANNEL_MIAOLE_DISTINCT = 28;
	/**无锡飞梦 不回调**/
	public static final int CHANNEL_WUXIFEIMENG_NOTCALLBACK = 29;
	/**聚有钱 实时排重**/
	public static final int CHANNEL_JUYOUQIAN_REAL_TIME_DISTINCT = 30;
	
	/**点入的快速任务**/
	public static final int CHANNEL_QUICK_DIANRU = 31;
	/**点乐的快速任务**/
	public static final int CHANNEL_QUICK_DIANJOY = 32;
	
	/**应用雷达回调*/
	public static final int CHANNEL_ANN9_CALLBACK = 33;
	
	/**应用雷达不回调*/
	public static final int CHANNEL_ANN9_NOT_CALLBACK = 34;
	
	/**热番茄不回调**/
	public static final int CHANNEL_REFANQIE_NOT_CALLBACK = 35;
	/**氪金回调**/
	public static final int CHANNEL_KEJIN_CALLBACK = 36;
	/**氪金不回调**/
	public static final int CHANNEL_KEJIN_NOT_CALLBACK = 37;
	/***试客排重**/
	public static final int CHANNEL_SHIKE_DISCINCT = 38;
	/***试客回调**/
	public static final int CHANNEL_SHIKE_CALLBACK = 39;
	
	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getChannel() {
		return channel;
	}


	public void setChannel(String channel) {
		this.channel = channel;
	}


	public String getChannel_id() {
		return channel_id;
	}


	public void setChannel_id(String channel_id) {
		this.channel_id = channel_id;
	}


	public String getClick_url() {
		return click_url;
	}


	public void setClick_url(String click_url) {
		this.click_url = click_url;
	}


	public String getCallback_url() {
		return callback_url;
	}


	public void setCallback_url(String callback_url) {
		this.callback_url = callback_url;
	}


	public String getQuery_url() {
		return query_url;
	}


	public void setQuery_url(String query_url) {
		this.query_url = query_url;
	}


	public int getStatus() {
		return status;
	}


	public void setStatus(int status) {
		this.status = status;
	}
	
	public boolean isCallBack() {
		return StringUtil.isNotBlank(callback_url);
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getService_name() {
		return service_name;
	}


	public void setService_name(String service_name) {
		this.service_name = service_name;
	}
	
}
