package com.cyhd;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.cyhd.service.dao.IJedisDao;
import com.cyhd.service.util.RedisUtil;

import redis.clients.jedis.Tuple;

public class TestRedis {

	public static void main(String[] args) {
		//testSortSet();
	}

	private static void testSortSet() {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		IJedisDao dao = context.getBean(RedisUtil.NAME_SELF,IJedisDao.class);
		String key = "user_tody_invite_finsh_task_rank_2016-05-06";
		String member = "3";
		try {
			Set<Tuple> sets = dao.zrevrangeByScoreWithScores(key, 0, 1000);
			sets.forEach(tuple -> {
				try {
					System.err.println(tuple.getElement() +" -> "+tuple.getScore()+" -> "+dao.zrevrank(key, tuple.getElement()));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
