package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserLoginRecord;


@Repository
public interface UserLoginRecordMapper {
	@Insert("INSERT INTO `money_user_login_record` (`user_id`, `name`, `avatar`, `country`, `province`, `city`, `did`, `idfa`, `appver`, `devicetype`, `model`, `os`, `net`, `ticket`, `createtime`, `ip`) VALUES "
			+ "(#{user_id}, #{name}, #{avatar}, #{country}, #{province}, #{city}, #{did}, #{idfa}, #{appver}, #{devicetype}, #{model}, #{os}, #{net}, #{ticket}, #{createtime}, #{ip});")
	public int add(UserLoginRecord record);
	
	@Select("SELECT DISTINCT ip FROM money_user_login_record WHERE user_id=#{0} limit 5")
	public List<String> getUserPreFiveRecordByIP(int userId);
	
	@Select("SELECT DISTINCT idfa FROM money_user_login_record WHERE user_id=#{0} limit 5")
	public List<String> getUserChangeFiveTimesIDFA(int userId);
	
	
	@Select("SELECT ip FROM money_user_login_record WHERE user_id=#{0} order by id desc limit 1")
	public String getUserLastLoginIp(int userId);
}

