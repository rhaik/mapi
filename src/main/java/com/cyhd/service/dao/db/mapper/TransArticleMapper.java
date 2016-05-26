package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.TransArticle;

@Repository
public interface TransArticleMapper {

	@Select("select * from money_trans_article where status=1 limit #{0}, #{1}")
	public List<TransArticle> getArticles(int start, int size);
	
	@Select("select * from money_trans_article where id=#{0}")
	public TransArticle getArticle(int id);

	@Select("select sum(view_num) from money_user_article_task where article_id=#{0}")
	public Integer getReadNum(int articleId);
}
