package com.cyhd.web.common;


import com.cyhd.service.constants.Constants;

public class ClientInfo {
	
	private String appnm;
	
	private String appVer;
	
	private String clientType;
	
	private String model;
	
	private String os;
	
	private String screen;
	
	private String did;
	
	private String androidid ;
	
	private String token;
	
	private String idfa;
	
	private String dt;
	private int tz;
	private String channel;
	
	private String ipAddress;  //ip地址
	private String net; //网络情况，wifi，3g，none
	
	private double longitude; //经度
	private double latitude; //维度
	private double scale; //精确度（米）
	
	private int cityid ; // 城市id

	private String bid; //battery id
	
	public boolean isIos9(){
		return isIos() && (os!=null && os.contains("OS9"));
	}

	public String getAppnm() {
		return appnm;
	}

	public void setAppnm(String appnm) {
		this.appnm = appnm;
	}

	public String getAppVer() {
		return appVer;
	}

	public void setAppVer(String appVer) {
		this.appVer = appVer;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getDid() {
		return did;
	}

	public void setDid(String did) {
		this.did = did;
	}
	
	public boolean isIos(){
		return clientType != null && clientType.equalsIgnoreCase("ios");
	}
	
	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getAndroidid() {
		return androidid;
	}

	public void setAndroidid(String androidid) {
		this.androidid = androidid;
	}

	public String getDt() {
		return dt;
	}

	public void setDt(String dt) {
		this.dt = dt;
	}

	public int getTz() {
		return tz;
	}

	public void setTz(int tz) {
		this.tz = tz;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getScale() {
		return scale;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	public String getNet() {
		return net;
	}

	public void setNet(String net) {
		this.net = net;
	}

	public int getCityid() {
		return cityid;
	}

	public void setCityid(int cityid) {
		this.cityid = cityid;
	}

	public String getIdfa() {
		return idfa;
	}

	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}

	public int getPlatform(){
		return isIos()?Constants.platform_ios:Constants.platform_android;
	}

	public String getBid() {
		return bid;
	}

	public void setBid(String bid) {
		this.bid = bid;
	}

	public String getIOSDeviceName(){
		return this.getModel() != null ?(this.getModel().startsWith("iPhone") ? "iPhone":"iPad"):"";
	}

	/**
	 * 获取短的电池id，目前有两种格式，一种是AF24672443MU031440，只有十几位，另一种是4635443435303643385036464847304339000000000000010000010000000100，这种截断为最多40个字符
	 * @return
	 */
	public String getShortBid(){
		if (bid != null && bid.length() > 40){
			return bid.substring(0, 40);
		}
		return bid;
	}

	@Override
	public String toString() {
		return "ClientInfo [appnm=" + appnm + ", appVer=" + appVer + ", clientType=" + clientType + ", model=" + model + ", os=" + os + ", screen=" + screen
				+ ", did=" + did + ", androidid=" + androidid + ", token=" + token + ", idfa=" + idfa + ", dt=" + dt + ", tz=" + tz + ", channel=" + channel
				+ ", ipAddress=" + ipAddress + ", net=" + net + ", longitude=" + longitude + ", latitude=" + latitude + ", scale=" + scale + ", cityid="
				+ cityid + "]";
	}
	
	public String getOSVersion(){
		char[] chars = os.toCharArray();
		boolean isNnmStart = false;
		StringBuilder sb = new StringBuilder();
		for(char c:chars){
			if(!isNnmStart && Character.isDigit(c) ){
				isNnmStart = true;
			}
			if(isNnmStart){
				sb.append(c);
			}
		}
		return sb.toString();
	}
	public static void main(String[] args) {
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setOs("8.2");
		System.out.println(clientInfo.getOSVersion());
	}
}
