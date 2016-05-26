package com.cyhd.service.dao.db.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.User;


@Repository
public interface UserMapper {

	@Select("select * from money_user where openid=#{0}")
	public User getUserByOpenId(String openId);
	
	@Select("select * from money_user where did=#{0}")
	public User getUserByDid(String deviceId);
	
	@Select("select * from money_user where unionid=#{0}")
	public User getUserByUnionId(String unionId);
	
	@Select("select * from money_user where invite_code=#{0}")
	public User getUserByInviteCode(String code);
	
	@Select("select * from money_user where ticket=#{0}")
	public User getUserByTicket(String ticket);

	@Select("select * from money_user where id=#{0}")
	public User getUserById(int id);

	@Select("select * from money_user where user_identity=#{0}")
	public User getUserByIdentifyId(int id);
	
	@Update("update money_user set mobile=#{1}, bindtime=now() where id=#{0}")
	public int bindMobile(int id, String mobile);
	
	@Select("select * from money_user where mobile=#{0}")
	public User getUserByMobile(String mobile);

	@Update("update money_user set masked=#{1} where id=#{0}")
	public int setMasked(int userId, int mask);

	@Update("update money_user set task_property=(task_property | #{1}) where id=#{0}")
	public int updateTaskProperty(int userId, int bit);

	@Select("select max(id) from money_user")
	public int getMaxUserId();
	
	@Insert("INSERT INTO `money_user` (`groupid`, `user_identity`, `avatar`, `name`, `sex`, `openid`, `unionid`, `invite_code`, `country`,`province`,`city`, `devicetype`,`did`, `idfa`, `source`, `ticket`, `createtime`, `lastlogintime`) "
			+ "VALUES (#{groupid}, #{user_identity}, #{avatar}, #{name}, #{sex}, #{openid}, #{unionid}, #{invite_code}, #{country}, #{province}, #{city},#{devicetype}, #{did}, #{idfa}, #{source}, #{ticket},  #{createtime}, #{lastlogintime}) "
			+ "on duplicate key update avatar=#{avatar}, name=#{name}, sex=#{sex}, openid=#{openid}, country=#{country}, province=#{province}, city=#{city}, devicetype=#{devicetype}, ticket=#{ticket}, did=#{did}, `idfa`=#{idfa}, lastlogintime=#{lastlogintime}")
	public int addOrUpdate(User user);
	
	@Update("update money_user set gen_share_pic=1 where id=#{0}")
	public int updateGenSharePic(int userid);
	
	@Update("update money_user set devicetype=#{1} where id=#{0}")
	public int updateDeviceType(int userId, int deviceType);
	
	@Select("select * from money_user where devicetype=2 and idfa IS NOT NULL limit #{0},#{1}")
	public List<User> getList(int start, int size);
	
	@Select("select * from money_user where idfa=#{0}")
	public User getUserByIdfa(String idfa);
	
	@Insert("INSERT INTO `money_masked_user_log`(user_id,`masked`,`masktime`,`maskreason`,`operid`) VALUES(#{0},#{1},now(),#{2},#{3})")
	public int addUserMaskedLog(int userId,int masked,String maskreason,int operid);

	@Select("select openid from money_weixin_user where userid=#{1} and appid=#{0}")
	String getUserOpenID(String appId, int userId);
	
	@Update("update money_user set name=#{1} where id=#{0}")
	public int updateName(int id, String name);
	
	@Update("update money_user set avatar=#{1} where id=#{0}")
	public int updateAvatar(int userId, String avatar);
	
	@Update("update money_user set createtime=#{1} where id=#{0}")
	public int updateUserCreateTime(int userId,Date createTime);
	/**
	 * CREATE TABLE `money_masked_user_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `masked` tinyint(2) DEFAULT '0' COMMENT '是否被封禁，0：解封，1：封禁',
  `masktime` datetime DEFAULT NULL COMMENT '被封时间',
  `maskreason` varchar(128) DEFAULT NULL COMMENT '被封原因',
  `operid` int(11) DEFAULT NULL COMMENT '操作人id',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;


	 */

}
