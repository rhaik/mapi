package com.cyhd.service.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import redis.clients.jedis.Tuple;

public interface IJedisDao {

	/**************** start 列表操作 
	 * @throws Exception TODO********************/
	public abstract List<String> getList(String key, int start, int end) throws Exception;

	public abstract boolean addToList(String key, String... value) throws Exception;

	public abstract long getListLen(String key) throws Exception;

	/**
	 * 保留最新的几条
	 * @param key
	 * @param len  保留几条
	 * @return
	 */
	public abstract boolean keepLen(String key, int len) throws Exception;
	
	
	public boolean removeFromList(String key, String value) throws Exception;

	/**************** end  列表操作 ********************/

	public abstract boolean remove(String key) throws Exception;

	public abstract boolean set(byte[] key, byte[] value) throws Exception;

	public abstract boolean set(String key, String value) throws Exception;

	public abstract String get(String key) throws Exception;

	public abstract byte[] get(byte[] key) throws Exception;

	public abstract boolean expire(String key, int seconds);

	public abstract long incr(String key) throws Exception;

	public abstract long decr(String key) throws Exception;

	/**
	 * 注意，本方法使用了NX指令，如果原来的key对应的值存在，则不会覆盖原值
	 * @param key
	 * @param value
	 * @param ttl
	 * @return
	 */
	public abstract boolean set(String key, String value, int ttl) throws Exception;

	public abstract boolean setSet(String key, String member, long score) throws Exception;
	
	public abstract boolean incSet(String key, String member, int scoreDelta) throws Exception;

	public abstract Set<String> getTops(String key, int max) throws Exception;

	public abstract long getRank(String key, String member) throws Exception;

	public abstract long getTotalSize(String key, int min, int max) throws Exception;
	/**
	 * 返回有序集 key 中，指定区间内的成员<br/>
	 * 注意 <b>尾部是包含的</b>
	 * @param key
	 * @param start
	 * @param stop
	 * @return
	 * @throws Exception
	 */
	public abstract Set<String> zrevrange(String key,int start,int stop) throws Exception;
	//Set数据结构操作 
	/**
	 * 将一个或多个 member 元素加入到集合(Set数据结构) key 当中
	 * @param key
	 * @param members
	 * @return
	 */
	public abstract long sadd(String key, String... members)throws Exception;
	/**
	 * 返回集合(Set数据结构) key 中的所有成员。不存在的 key 被视为空集合
	 * @param key
	 * @return
	 */
	public abstract Set<String> smembers(String key) throws Exception;
	/**
	 * 移除集合(Set数据结构) key 中的一个或多个 member 元素
	 * @param key
	 * @param members
	 * @return
	 */
	public abstract long srem(final String key, String... members)throws Exception;
	/**
	 * 返回集合 key 的基数(集合中元素的数量)
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public abstract Long scard(final String key) throws Exception;

	/**
	 * 判断member是否在key对应的set中
	 * @param key
	 * @param member
	 * @return
	 * @throws Exception
	 */
	boolean sismember(final String key, final String member) throws Exception;

	
	public abstract Long zcard(final String key) throws Exception;
	/**
	 * 将一个或多个 member 元素及其 score 值加入到有序集 key 当中<br/>
	 * @see {@link #setSet(String, String, long)} 的增强版
	 * @param key
	 * @param scoreMembers
	 * @return
	 * @throws Exception
	 */
	public abstract long zadd( final String key, final Map<String, Double> scoreMembers)throws Exception;
	/**{@link #zadd(String, Map)的增强版*/
	public abstract long zaddObj(final String key, Map<Object, Double> scoreMembers)throws Exception;
	/**{@link #zrevrange(String, int, int)} 的增强版 前提是调用{@link #zadd(String, Map)}插入数据**/
	public abstract Set<Object> zrevrangeObj(String key,int start,int stop) throws Exception;
	/**
     * Test if the specified key exists. The command returns "1" if the key
     * exists, otherwise "0" is returned. Note that even keys set with an empty
     * string as value will return "1".
     * 
     * Time complexity: O(1)
     * 
     * @param key
     * @return Boolean reply, true if the key exists, otherwise false
     */
	boolean exists(final String key)throws Exception;
	
	/****
	 * 对排序的Set操作
	 * @param key
	 * @param min
	 * @param max
	 * @param offset
	 * @param count
	 * @return Multi bulk reply specifically a list of elements in the specified
     *         score range.
	 */
	public abstract Set<Tuple> zrangeByScoreWithScores(final String key,final double min, final double max) throws Exception;
	
	public abstract Set<Tuple> zrangeByScoreWithScores( String key,double min,  double max,  int offset,int count) throws Exception;
	/***
	 * 返回有序集key中，score值介于max和min之间(默认包括等于max或min)的所有的成员。有序集成员按score值递减(从大到小)的次序排列。
	 * @param key
	 * @param max
	 * @param min
	 * @return
	 */
	public Set<Tuple> zrevrangeByScoreWithScores(final String key, final double max, final double min)throws Exception;
	
	/***
	 * ZREVRANGEBYSCORE
	 * 返回有序集key中，score值介于max和min之间(默认包括等于max或min)的所有的成员。有序集成员按score值递减(从大到小)的次序排列。
	 * @param key
	 * @param max
	 * @param min
	 * @param offset
	 * @param count
	 * @return
	 */
	public Set<Tuple> zrevrangeByScoreWithScores(final String key,final double max, final double min, final int offset,final int count) throws Exception;
	
	/****
	 * 
	 * @param key
	 * @param score
	 * @param member
	 * @return The new score
	 */
	public abstract double zincrby(final String key, final double score,final String member) throws Exception;
	/***
	 * add new 
	 * @param key
	 * @param score
	 * @param member
	 * @return Integer reply, specifically: 1 if the new element was added 0 if
     *         the element was already a member of the sorted set and the score
     *         was updated
	 */
	public long zadd(final String key, final double score, final String member) throws Exception;
	/***
	 * 返回有序集key中，成员member的score值。
	 * @param key
	 * @param member
	 * @return 成员member的score值
	 */
	public abstract double zscore(final String key, final String member) throws Exception;
	
	/***
	 * 返回有序集key中成员member的排名。其中有序集成员按score值递减(从大到小)排序。
	 * redis 排名以0为底，也就是说，score值最大的成员排名为0。<br/>
	 * 现在反悔0或-1就表示没有获得名次
	 * @param key
	 * @param member
	 * @return
	 * @throws Exception
	 */
	 public abstract long zrevrank(final String key, final String member) throws Exception;
}