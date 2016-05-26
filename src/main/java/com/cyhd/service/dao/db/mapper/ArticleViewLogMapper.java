package com.cyhd.service.dao.db.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.ArticleViewLog;

@Repository
public interface ArticleViewLogMapper {

	@Insert("insert into money_article_view_log(`article_id`,`task_user_id`,`task_user_unionid`,`view_unionid`,`view_openid`, `ip` ,`createtime`) " +
			" values(#{article_id},#{task_user_id},#{task_user_unionid},#{view_unionid},#{view_openid}, #{ip}, #{createtime})")
	public int insert(ArticleViewLog log );
	
	/**改文章下是否存在unionID发布或查看过该任务*/
	@Select("select id from money_article_view_log where article_id=#{0} and view_unionid=#{1} limit 1")
	public Integer existByUser(int article_id,String unionid);

	@Select("select count(1) as count from money_article_view_log where article_id=#{0} and ip=#{1}")
	public Integer getReadCountByIp(int article_id,String ip);
}
