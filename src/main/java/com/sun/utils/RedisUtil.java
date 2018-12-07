package com.sun.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public final class RedisUtil {
	
	private static String ADDR = "127.0.0.1";
	
	private static int PORT = 6379;
	
	private static int MAX_TOTAL = 3;
	private static int MAX_IDLE = 1;
	private static int MAX_WAIT = 1;
	private static int TIMEOUT = 10000;
	private static boolean TEST_ON_BORROW = true;
	private static JedisPool jedisPool = null;
	
	static{
		try{
			JedisPoolConfig config = new JedisPoolConfig();
			config.setMaxIdle(MAX_IDLE);
			config.setMaxTotal(MAX_TOTAL);
			config.setMaxWaitMillis(MAX_WAIT);
			config.setTestOnBorrow(TEST_ON_BORROW);
			jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public synchronized static Jedis getJedis(){
		try{
			if(jedisPool != null){
				Jedis resources = jedisPool.getResource();
				return resources;
			}else{
				return null;
			}
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public static void returnResource(final Jedis jedis){
		if(jedis != null){
			jedisPool.returnResource(jedis);
		}
	}
	
}
