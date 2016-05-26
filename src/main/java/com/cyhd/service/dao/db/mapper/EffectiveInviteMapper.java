package com.cyhd.service.dao.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.EffectiveInvite;

@Repository
public interface EffectiveInviteMapper {

	@Insert("INSERT INTO money_effective_invite(`user`,`num`,`day`) values(#{user},#{num},#{day}) on duplicate key update `num`=`num`+#{num}")
	public int insertOrUpdate(EffectiveInvite effectiveInvite);
	
	@Select("SELECT `num` FROM money_effective_invite where `user`=#{0} AND `day`=#{1}")
	public int getUserEffectiveInviteNum(int userId,String days);
	
	@Select("select count(id) + 1 from money_effective_invite where num > #{0} and day=#{1}")
	public int getUserEffectiveInviteRank(int nums,String days);
	// 实际的sql 拆成两个 方便看
}
