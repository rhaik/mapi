package com.cyhd.service.dao.db.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import com.cyhd.service.dao.po.Game;

@Repository
public interface GameMapper {

	@Select("select * from money_game where state=1 and status=1 order by order_num desc limit #{0} ,#{1}")
	public List<Game> getValid(int start, int size);
	
	
}
