package com.cyhd.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.dao.db.mapper.GameMapper;
import com.cyhd.service.dao.po.Game;
import com.cyhd.service.util.RedisUtil;

@Service
public class GameService extends BaseService{

	@Resource
	private GameMapper gameMapper;

	private List<Game> gameList = new ArrayList<>();
	
	private volatile boolean loading = false;
	
	private final int size = 80;
	
	@Resource(name=RedisUtil.NAME_SELF)
	private IJedisDao gameCache;

	@PostConstruct
	public void loadGames(){
		if(loading){
			logger.info("已经加载游戏中");
			return ;
		}
		logger.info("加载游戏列表开始-----");
		try {
			loading = true;
			//这里就是显示的时候用 基本不操作 所以本地没有啦 就去加载
			gameList = gameMapper.getValid(0, size);
			logger.info("加载到游戏的数量:{}", gameList.size());
		} catch (Exception e) {
			logger.info("加载游戏列表出错");
		}finally {
			loading = false;
		}
	}
	
	public List<Game> getGames(int clientType,Predicate<Game> predicate){
		return this.getStreamByClientType(clientType,predicate).collect(Collectors.toList());
	}
	
	public Game getGameById(int id){
		return gameList
				.stream()
				.filter(game -> game.getId() == id)
				.findFirst()
				.orElse(null);
	}
	/***
	 * 获取付费的游戏按付费金额排序
	 * @return
	 */
	public List<Game> getPayGame(int clientType,Predicate<Game> predicate){
		return this.getStreamByClientType(clientType,predicate).filter(game -> game.isPayGame())
				.sorted(Comparator.comparing(game -> game.getPay()))
				.collect(Collectors.toList());
	}
	
	/***
	 * 按类别获得列表
	 * @param category
	 * @return
	 */
	public List<Game> getGamesByCategory(int clientType,String category,Predicate<Game> predicate){
		return this.getStreamByClientType(clientType,predicate).filter(game -> category.equals(game.getCategory()))
				.collect(Collectors.toList());
	}
	
	private Stream<Game> getStreamByClientType(int clientType,Predicate<Game> predicate){
		return gameList.stream()
				.filter(game -> {return (game.getPlatform() == clientType || game.getPlatform() == 3);})
				.filter(predicate);
	}
	
}
	
