package com.cyhd.service.dao.db.mapper;


import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.Source;


@Repository
public interface SourceMapper {
	@Select("select * from `money_source` WHERE `identity`=#{0} AND `status`=1")
	public Source getSourceByIdentity(String identity);
}