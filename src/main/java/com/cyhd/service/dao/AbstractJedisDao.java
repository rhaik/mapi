package com.cyhd.service.dao;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;
import redis.clients.util.SafeEncoder;

import com.cyhd.common.util.serializer.ByteSerializer;
import com.cyhd.common.util.serializer.Hessian2Serialize;
import com.cyhd.common.util.serializer.HessianSerialize;
import com.cyhd.common.util.serializer.JavaSerialize;
import com.cyhd.service.util.GlobalConfig;

public abstract class AbstractJedisDao implements IJedisDao {
	
	protected static final Logger logger = LoggerFactory.getLogger("redis");
	
	protected abstract JedisPool getJedisPool();
	protected abstract String getName();
	private ByteSerializer serializer;
	public AbstractJedisDao() {
		String serializerName = GlobalConfig.jedis_serializer;
		  
		if (serializerName == null) {
		      this.serializer = new HessianSerialize();
		    }else if (serializerName.equals("hessian")){
		    	this.serializer = new HessianSerialize();
		    } else if (serializerName.equals("hessian2")){
		    	this.serializer = new Hessian2Serialize();
		    }else if (serializerName.equals("java")){
		    	this.serializer = new JavaSerialize();
		    }else {
		      throw new RuntimeException("Crawl-appstore-redis不支持的序列化协议");
		    }
	}
	
	private void returnResource(JedisPool pool, Jedis redis) {
        if (redis != null) {
        	try{
        		pool.returnResource(redis);
        	}catch(Exception e){
        		logger.error(getName() + " jedis return resource error!", e);
        	}
        }
    }

	@Override
	public List<String> getList(String key, int start, int end) throws Exception{
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            List<String> ret = jedis.lrange(key, start, end);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao getList, key="+key +", ret=" + ret);
            return ret;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao getList, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	
	@Override
	public boolean addToList(String key, String... value) throws Exception{
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long ret = jedis.lpush(key, value);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao addToList, key="+key +", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao addToList, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	
	@Override
	public long getListLen(String key)throws Exception{
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long ret = jedis.llen(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao getListLen, key="+key +", ret=" + ret);
            return ret != null ? ret : 0;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao getListLen, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public boolean keepLen(String key, int len) throws Exception {
		if(len <= 0)
			return false;
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            String ret = jedis.ltrim(key, 0, len-1);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao keepLen, key="+key +", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao keepLen, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	/**
	 * 从list中删除数据
	 * @param key
	 * @param value
	 * @return
	 */
	@Override
	public boolean removeFromList(String key, String value) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long count = jedis.lrem(key, 1, value);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao removeFromList, key="+key +", value=" + value);
            return count != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao removeFromList, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public boolean remove(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long ret = jedis.del(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao remove, key="+key +", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao remove, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public boolean set(byte[] key, byte[] value) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            String ret = jedis.set(key, value);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao set, key="+key + ", value=" + value+", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao set, key="+key + ", value=" + value, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public boolean set(String key, String value) throws Exception {
		if(value == null || key == null){
			return false;
		}
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            String ret = jedis.set(key, value);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao set, key="+key + ", value=" + value+", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao set, key="+key + ", value=" + value, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public String get(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            String ret = jedis.get(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao get, key="+key + ", ret=" + ret);
            return ret;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao get, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public byte[] get(byte[] key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            byte[] ret = jedis.get(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao get, key="+key + ", ret=" + ret);
            return ret;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao get, key="+key, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	/**
	 * set expired key-value, only if not exist, ttl: in second.
	 */
	@Override
	public boolean set(String key, String value, int ttl) throws Exception {
		if(value == null ||key == null){
			return false;
		}
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            String ret = jedis.setex(key, ttl, value);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao set, key="+key + ", value=" + value + ", ttl="+ttl+ ", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao set, key="+key + ", value=" + value + ", ttl="+ttl, e);
            throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}

	@Override
	public boolean setSet(String key, String member, long score) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long ret = jedis.zadd(key, score, member);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao set, key="+key + ", member=" + member + ", score="+score + ", ret=" + ret);
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao set error! key="+key + ", member=" + member + ", score="+score, e);
        } finally {
            returnResource(pool, jedis);
        }
		return false;
		
	}

	@Override
	public boolean incSet(String key, String member, int scoreDelta) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Double ret = jedis.zincrby(key, scoreDelta, member);
            if(logger.isInfoEnabled()){
            	logger.info(getName() + " jedis dao increase! key="+key + ", member=" + member + ", score="+scoreDelta + ", ret=" + ret);
            }
            return ret != null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao increase error! key="+key + ", member=" + member + ", score="+scoreDelta, e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	
	@Override
	public Set<String> getTops(String key, int max) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao getTops! key="+key + ", max=" + max);
            return jedis.zrevrange(key, 0, max);
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao getTops error! key="+key + ", max=" + max, e);
        } finally {
            returnResource(pool, jedis);
        }
		return null;
	}
	
	@Override
	public long getRank(String key, String member) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long rank = jedis.zrevrank(key, member);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao getRank! key="+key + ", member=" + member + ", ret="+rank);
            if(rank != null)
            	return rank.longValue();
            return -1;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao increase error! key="+key + ", member=" + member, e);
        } finally {
            returnResource(pool, jedis);
        }
		return -1;
	}
	
	@Override
	public long getTotalSize(String key, int min, int max) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long total = jedis.zcount(key, min, max);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao getTotalSize! key="+key + ", min=" + min + ", max="+max + ", ret="+total);
            if(total != null)
            	return total.longValue();
            return 0;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao getTotalSize error! key="+key + ", min=" + min + ", max="+max, e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public long sadd(String key, String... members)throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long total = jedis.sadd(key,members);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao sadd! key="+key + ", ret="+total);
            if(total != null)
            	return total.longValue();
            return 0;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao sadd error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public Set<String> smembers(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Set<String> data = jedis.smembers(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao smembers! key="+key + ", ret="+data);
            return data;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao smembers error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public long srem(String key, String... members) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long data = jedis.srem(key,members);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao srem! key="+key + ", ret="+data);
            return data == null?0:data;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao srem error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}

	@Override
	public boolean sismember(String key, String member) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		boolean inSet = false;
		try {
			jedis = pool.getResource();
			inSet = jedis.sismember(key, member);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis dao sismember! key="+key + ", member=" + member + ", ret=" + inSet);
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis dao sismember error! key="+key , e);
			throw e;
		} finally {
			returnResource(pool, jedis);
		}
		return inSet;
	}

	@Override
	public Long scard(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long data = jedis.scard(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao scard! key="+key + ", ret="+data);
            return data == null?0:data;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao scard error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public Set<String> zrevrange(String key, int start, int stop)
			throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao getTops! key="+key + ", start=" + start+",stop="+stop);
            return jedis.zrevrange(key, start, stop);
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao getTops error! key="+key + ",start=" + start+",stop="+stop, e);
        } finally {
            returnResource(pool, jedis);
        }
		return null;
	}
	@Override
	public Long zcard(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long data = jedis.zcard(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao zcard! key="+key + ", ret="+data);
            return data == null?0:data;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao zcard error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public long zadd(String key, Map<String, Double> scoreMembers)
			throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Long ret = jedis.zadd(key, scoreMembers);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao zadd, key="+key + ", scoreMembers=" + scoreMembers );
            return ret == null?0:ret;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao zadd error! key="+key + ", scoreMembers=" + scoreMembers , e);
        } finally {
            returnResource(pool, jedis);
        }
		return 0;
	}
	
	@Override
	public long zaddObj(String key,Map<Object, Double> scoreMembers) throws Exception {
		if(scoreMembers == null || scoreMembers.isEmpty()){
			return 0;
		}
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Set<Entry<Object, Double>> entrys = scoreMembers.entrySet();
            Map<byte[], Double> map  = new HashMap<byte[], Double>(entrys.size());
            for(Entry<Object, Double> entry:entrys){
            	map.put(this.serializer.serialize(entry.getKey()), entry.getValue());
            }
            Long total = jedis.zadd(SafeEncoder.encode(key),map);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao zaddObj! key="+key + ", ret="+total);
            if(total != null)
            	return total.longValue();
            return 0;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao zaddObj error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public Set<Object> zrevrangeObj(String key, int start, int stop)
			throws Exception {
		
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Set<byte[]> datas = jedis.zrevrange(SafeEncoder.encode(key), start, stop);
            if(datas != null && datas.isEmpty() == false){
            	Set<Object> obj = new HashSet<Object>(datas.size());
            	for(byte[] data:datas){
            		obj.add(this.serializer.deserialize(data));
            	}
            	return obj;
            }
            
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao zrevrangeObj! key="+key );
            return null;
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
        	logger.error(getName() + " jedis dao zrevrangeObj error! key="+key , e);
        	throw e;
        } finally {
            returnResource(pool, jedis);
        }
	}
	@Override
	public boolean exists(String key) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
            jedis = pool.getResource();
            Boolean ret = jedis.exists(key);
            if(logger.isInfoEnabled())
            	logger.info(getName() + " jedis dao exists, key="+key + ", ret=" + ret );
            return ret.booleanValue();
        } catch (Exception e) {
        	pool.returnBrokenResource(jedis);
            logger.error(getName() + " jedis dao exists error! key="+key, e);
        } finally {
            returnResource(pool, jedis);
        }
		return false;
	}

	@Override
	public boolean expire(String key, int seconds) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Long ret = jedis.expire(key, seconds);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis expire key="+key + ", ret=" + ret );
			return ret != null && ret.intValue() == 1;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis expire error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return false;
	}

	public long incr(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Long ret = jedis.incr(key);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis incr key="+key + ", ret=" + ret );
			return ret.longValue();
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis incr error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return 0;
	}

	public long decr(String key) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Long ret = jedis.decr(key);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis decr key="+key + ", ret=" + ret );
			return ret.longValue();
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis decr error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return 0;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Set<Tuple> sets = jedis.zrangeByScoreWithScores(key,min,max);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zrangeByScoreWithScores key="+key + ", ret size=" + (sets == null?0:sets.size()) );
			return sets;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zrangeByScoreWithScores error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}
	@Override
	public Set<Tuple> zrangeByScoreWithScores(String key, double min, double max, int offset, int count) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Set<Tuple> sets = jedis.zrangeByScoreWithScores(key,min,max,offset,count);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zrangeByScoreWithScores key="+key + ", ret size=" + (sets == null?0:sets.size()) );
			return sets;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zrangeByScoreWithScores error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}
	@Override
	public double zincrby(String key, double score, String member) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Double newScore= jedis.zincrby(key, score, member);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zincrby key="+key + ", ret =" + newScore);
			return newScore==null?0:newScore.doubleValue();
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zincrby error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return 0;
	}
	@Override
	public long zadd(String key, double score, String member) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Long newScore= jedis.zadd(key, score, member);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zadd key="+key + ", ret =" + newScore);
			return newScore==null?0:newScore.longValue();
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zadd error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return 0;
	}
	@Override
	public double zscore(String key, String member) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Double newScore= jedis.zscore(key, member);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zscore key="+key + ", ret =" + newScore);
			return newScore==null?0:newScore.doubleValue();
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zscore error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return 0;
	}
	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Set<Tuple> sets = jedis.zrevrangeByScoreWithScores(key,min,max);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zrevrangeByScoreWithScores key="+key + ", ret size=" + (sets == null?0:sets.size()) );
			return sets;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zrevrangeByScoreWithScores error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}
	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String key, double max, double min, int offset, int count) {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Set<Tuple> sets = jedis.zrevrangeByScoreWithScores(key,max,min,offset,count);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zrevrangeByScoreWithScores key="+key + ", ret size=" + (sets == null?0:sets.size()) );
			return sets;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zrevrangeByScoreWithScores error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return null;
	}
	@Override
	public long zrevrank(String key, String member) throws Exception {
		Jedis jedis = null;
		JedisPool pool = getJedisPool();
		try {
			jedis = pool.getResource();
			Long rtv = jedis.zrevrank(key,member);
			if(logger.isInfoEnabled())
				logger.info(getName() + " jedis zrevrank key="+key + ",menber "+member+", ret =" + rtv);
			return rtv == null ?-1 :rtv.longValue()+1;
		} catch (Exception e) {
			pool.returnBrokenResource(jedis);
			logger.error(getName() + " jedis zrevrank error! key="+key, e);
		} finally {
			returnResource(pool, jedis);
		}
		return -1;
	}
}

