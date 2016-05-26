package com.cyhd.service.vo;

import com.cyhd.service.dao.po.User;
import com.cyhd.service.impl.UserFriendService;

public class InviteUserVo {

	private User user;
	private int inviteNum;
	private int rank;
	private boolean displayDate = false;
	
	private String day;
	
	public int getInviteNum() {
		return inviteNum;
	}
	public void setInviteNum(int inviteNum) {
		this.inviteNum = inviteNum;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	
	public String getRankMessage(){
		if(inviteNum >= 100){
			return String.valueOf(rank);
		}else if(rank <= 0 || rank >30 || inviteNum < 10){
			return "1000+";
		}
		
		return String.valueOf(getRankByDefaultRule());
	}
	
	public String getHongBaoMessage(){
		String message = "0元";
		if(this.getRank() > 30){
			
		}else if(this.getRank() >= 21){
			message = "1W金币";
		}else if(this.getRank() >= 11){
			message = "20元";
		}else if(this.getRank() >= 6){
			message = "30元";
		}else if(this.getRank() == 5){
			message = "40元";
		}else if(this.getRank() == 4){
			message = "60元";
		}else if(this.getRank() == 3){
			message = "100元";
		}else if(this.getRank() == 2){
			message = "200元";
		}else if(this.getRank() == 1){
			message = "300元";
		}
		return message;
	}
	
	public int getRankByDefaultRule(){
		return UserFriendService.rankByInviterNum.indexOf(getInviteNum());
	}
	
	@Override
	public String toString() {
		return "InviteUserVo [user=" + user.getId() + ", inviteNum=" + inviteNum + ", rank=" + rank + "]\n<br/>";
	}
	public boolean isDisplayDate() {
		return displayDate;
	}
	public void setDisplayDate(boolean displayDate) {
		this.displayDate = displayDate;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}

}
