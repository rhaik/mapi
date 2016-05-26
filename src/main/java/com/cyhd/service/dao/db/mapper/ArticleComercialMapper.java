package com.cyhd.service.dao.db.mapper;

import com.cyhd.service.dao.po.ArticleComercial;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;


@Repository
public interface ArticleComercialMapper {

	@Select("select * from money_article_comercial where id=#{0}")
	public ArticleComercial getComercial(int id);

}