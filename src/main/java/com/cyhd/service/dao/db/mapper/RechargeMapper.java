package com.cyhd.service.dao.db.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.Recharge;
import com.cyhd.service.dao.po.RechargeDenomination; 
import com.cyhd.service.dao.po.RechargeLog;


@Repository
public interface RechargeMapper {
	//保存充值订单
	@Insert("INSERT INTO `money_recharge` (`order_sn`, `recharge_denomination_id`, `mobilephone`, `mobile_area`, `value`, `quantity`, "
			+ "`channel`, `pay_amount`, `user_id`, `status`, `createtime`, `third_oid`, `total_price`) VALUES "
			+ "(#{order_sn}, #{recharge_denomination_id}, #{mobilephone}, #{mobile_area}, #{value}, #{quantity}, "
			+ "#{channel},#{pay_amount}, #{user_id}, #{status}, now(), #{third_oid}, #{total_price})")
	public int add(Recharge recharge);
	
	//保存充值订单日志
	@Insert("INSERT INTO `money_recharge_log` (`recharge_id`, `status`, `createtime`, `remarks`) VALUES "
			+ "(#{recharge_id}, #{status}, now(), #{remarks})")
	public int addRechargeLog(RechargeLog recharge);
	
	@Update("update money_recharge set status=#{1}, total_price=#{2}, remarks=#{3} where id=#{0}")
	public int updateStatus(int id, int status, int total_price, String remarks);
	
	@Update("update money_recharge set status=#{1}, total_price=#{2}, remarks=#{3} where id=#{0} AND status=1")
	public int updateRechargeStatusById(int id, int status, int total_price, String remarks);
	
	@Select("select * from money_recharge where `order_sn`=#{0}")
	public Recharge getByOrderSn(long orderSn);
	
	@Select("select * from money_recharge where `user_id`=#{0} order by id DESC")
	public List<Recharge> getRechargeListByUserId(int userId);
	
	//查询所有面值
	@Select("select * from money_recharge_denomination where `channel`=#{0} AND state=1 order by sort")
	public List<RechargeDenomination> getRechargeDenominationList(int channel);
	
	@Select("select * from money_recharge_denomination where `id`=#{0}")
	public RechargeDenomination getRechargeDenomination(int id);
}