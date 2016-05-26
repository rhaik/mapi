package com.cyhd.service.dao.db.mapper;

import com.cyhd.service.dao.po.ArticleWeixinAccount;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Repository
public interface ArticleWeixinAccountMapper {

	@Select("select * from money_article_weixin_account where id=#{0}")
	public ArticleWeixinAccount getWeixinAccount(int id);

}