package com.cyhd.service.dao.db.mapper;

import com.cyhd.service.dao.po.UserCheckInRecord;
import com.cyhd.service.dao.po.UserCheckInStat;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by hy on 9/16/15.
 */
@Repository
public interface UserCheckInMapper {

    @Select("SELECT * FROM money_user_checkin_record WHERE user_id=#{0} ORDER BY id DESC LIMIT 1")
    public UserCheckInRecord getLatestRecord(int userId);

    @Select("SELECT * FROM money_user_checkin_record WHERE user_id=#{0} ORDER BY id DESC LIMIT #{1},#{2}")
    public List<UserCheckInRecord> getUserCheckInRecords(int userId, int start, int limit);

    @Insert("INSERT INTO `money_user_checkin_record` (`id`, `user_id`, `income`, `days`, `checkin_time`) VALUES  " +
            " (#{id}, #{user_id}, #{income}, #{days}, #{checkin_time}) ")
    public int addCheckInRecord(UserCheckInRecord record);


    @Select("select user_id, count(1) as total_days, sum(income) as total_income from money_user_checkin_record where user_id=#{0}")
    public UserCheckInStat getUserCheckStat(int userId);

}
