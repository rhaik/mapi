package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.UserThridPartIntegral;

@Repository
public interface UserThridPartIntegralMapper {

	public int insert(UserThridPartIntegral integral);
	
	public List<UserThridPartIntegral> getIntegralByUser(int userId);
	
	@Select("select * from money_user_thrid_part_integral where user_id = #{0} and (source=#{1} or source=6 or source=8 or source=12 or source=13 or source=14 or source=15 or source=16)   ORDER BY createtime DESC LIMIT #{3},#{4}")
	public List<UserThridPartIntegral> getUserThridPartIntegralList(int userId,int source,int clientType, int start, int size);
	//不想去XML中使用if
	@Select("select * from money_user_thrid_part_integral where user_id = #{0} and source in(2,3,4,5,7,9,10,11) and client_type=#{2} ORDER BY createtime DESC LIMIT #{3},#{4}")
	public List<UserThridPartIntegral> getUserThridPartIntegralListByJifen(int userId,int source,int clientType, int start, int size);
	
	@Select("select count(*) from money_user_thrid_part_integral where user_id = #{0} and (source=#{1} or source=6 or source=8 or source=12 or source=13 or source=14 or source=15 or source=16)")
	public int countUserThridPartIntegral(int userId,int source,int clientType);
	
	@Select("select count(*) from money_user_thrid_part_integral where user_id = #{0} and source in(2,3,4,5,7,9,10,11) and client_type=#{2}")
	public int countUserThridPartIntegralByJifen(int userId,int source,int clientType);
	
	@Select("select id from money_user_thrid_part_integral where udid = #{0} and adv_id=#{1} and trade_type=#{2} and open_udid=#{3} and source=7 limit 1 ")
	public Long repeatByDianJoy(String udid,String adv_id,int trade_type,String open_udid);

	@Select("select id from money_user_thrid_part_integral where order_id=#{0} and source=#{1} limit 1 ")
	public Long repeatByOrderId(String order_id,int source);
	
	@Select("select count(t.id) from (select id from money_user_thrid_part_integral where user_id=#{0} and itunes_id=#{1} limit 2) t ")
	public Integer repeatByUserAppExistThirdTimes(int userId,String appstore_id);

	@Select("select id from money_user_thrid_part_integral where `key`=#{0} and `adv_id`=#{1} and `source`=#{2} limit 2")
	public List<Long> repeatByWanPuIOS(String key, String adv_id, int source);
}
