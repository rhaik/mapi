package com.cyhd.service.dao.db.mapper.doubao;



import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.ThridShishicai;
 

@Repository
public interface ThridShishicaiMapper {
	
	@Select("select * from money_thrid_shishicai where periods=#{0} limit 1")
	public ThridShishicai getByPeriods(int periods);
	
	@Select("select * from money_thrid_shishicai WHERE lottery_time > DATE_FORMAT(now(),'%Y-%m-%d') AND lottery_number=''  order by periods asc limit 1")
	public ThridShishicai getNolotteryPeriods();
	
	@Select("select *  from money_thrid_shishicai where DATE_FORMAT(lottery_time,'%Y-%m-%d') = DATE_FORMAT(now(),'%Y-%m-%d') order by periods desc")
	public List<ThridShishicai> getList();
	
	@Insert("INSERT INTO `money_thrid_shishicai` (`periods`, `lottery_number`, `lottery_time`) VALUES " +
			"(#{periods}, #{lottery_number}, #{lottery_time}) " +
			" ON DUPLICATE KEY UPDATE lottery_number=#{lottery_number}")
	public int add(ThridShishicai o);
	
	@Update("Update money_thrid_shishicai set lottery_number=#{1} where periods=#{0}")
	public int updateLotteryNumber(int periods, String lotteryNumber);
}