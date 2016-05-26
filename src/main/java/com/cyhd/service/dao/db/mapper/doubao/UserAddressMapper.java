package com.cyhd.service.dao.db.mapper.doubao;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.doubao.UserAddress;
 
@Repository
public interface UserAddressMapper {
	
	@Select("select * from money_user_address where user_id=#{0} and deleted=0 order by preferred desc, id asc")
	public List<UserAddress> getList(int userId);
	
	@Select("select * from money_user_address where id=#{0}")
	public UserAddress getById(int id);
	
	@Select("select * from money_user_address where user_id=#{0} AND preferred=1 and deleted=0 limit 1")
	public UserAddress getDefaultAddressByUserId(int userId);
	
	@Insert("INSERT INTO `money_user_address` (`user_id`, `name`, `mobile`, `address`, `createtime`, `preferred`) VALUES "
			+ "(#{user_id}, #{name}, #{mobile}, #{address}, #{createtime}, #{preferred})")
	public int saveUserAddress(UserAddress address);
	
	@Update("update money_user_address SET name=#{name}, mobile=#{mobile}, address=#{address}, createtime=#{createtime}, deleted=#{deleted}, preferred=#{preferred} where id=#{id}")
	public int updateAddress(UserAddress address);
	
	@Update("Update money_user_address set preferred=1 where user_id=#{0} AND id=#{1}")
	public int updateDefault(int userId, int id);
	
	@Update("Update money_user_address set preferred=0 where user_id=#{0}")
	public int updateCancelDefault(int userId);
	
	
	@Delete("update `money_user_address` set deleted=1 WHERE user_id=#{0} AND id=#{1}")
	public int deleteUserAddress(int userId, int id);
}