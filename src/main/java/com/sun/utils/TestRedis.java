package com.sun.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import redis.clients.jedis.Jedis;

public class TestRedis {
	
	private Jedis redis;

	@Before
	public void setup(){
		redis = new Jedis("127.0.0.1", 6379);
	}
	
	@Test
	public void testString(){
		redis.set("name", "0");
//		System.out.println(redis.get("name"));//jerry
		redis.append("name", " is handsome.");
//		System.out.println(redis.get("name"));//jerry is handsome
//		System.out.println(redis.type("name"));//string
//		System.out.println(redis.keys("jerr*"));//[]
//		System.out.println(redis.keys("nam*"));//[name]
		System.out.println(redis.keys("*") + " : " + redis.randomKey());
		System.out.println(redis.mget("name","age"));
//		redis.del("name");
//		System.out.println("before:" + redis.get("name"));
//		System.out.println(redis.dbSize());
//		redis.expire("name", 1);
//		System.out.println("after:" + redis.get("name") + "," + redis.ttl("name"));
//		redis.mset("name","tom","age","23","qq","123456");
//		redis.incr("age");//age+1
//		System.out.println(redis.get("name") + "-" + redis.get("age") + "-" + redis.get("qq"));
		redis.setnx("name", "jerry");
		System.out.println(redis.get("name"));
	}
	
	@Test
	public void testMap(){
		Map<String, String> map = Maps.newHashMap();
		map.put("name", "jerry");
		map.put("age", "33");
		map.put("qq", "123456");
		
		redis.hmset("user",map);
		
		List<String> rsmap = redis.hmget("user", "name", "age", "qq");
		System.out.println(rsmap);
		
		redis.hdel("user", "age");
		System.out.println(redis.hmget("user", "age"));//null
		System.out.println(redis.hlen("user"));//2,返回key为user的健中存放的值数目
		System.out.println(redis.exists("user"));//true
		System.out.println(redis.hkeys("user"));//[qq,name],map中所有的key
		System.out.println(redis.hvals("user"));//[123456,jerry],map中所有的value
		
		Iterator<String> iter = redis.hkeys("user").iterator();
		while(iter.hasNext()){
			String key = iter.next();
			System.out.println(key + " : " + redis.hmget("user", key));
		}
	}
	
	@Test
	public void testList(){
		redis.del("java framework");
		System.out.println(redis.lrange("java framework", 0, -1));
		//redis队列list头插入数据，先进后出（list最大长度2^32=42亿）
		redis.lpush("java framework", "spring");
		redis.lpush("java framework", "struts");
		redis.lpush("java framework", "hibernate");
		//lrange是按范围取出
		System.out.println(redis.lrange("java framework", 0, 0));//[hibernate]
		System.out.println(redis.lrange("java framework", 0, -1));//[hibernate,struts,spring],-1取出所有

		redis.del("java framework");
		//list尾插入数据
		redis.rpush("java framework", "spring");
		redis.rpush("java framework", "struts");
		redis.rpush("java framework", "hibernate");
		System.out.println(redis.lrange("java framework", 0, -1));
		System.out.println(redis.llen("java framework"));
	}
	
	@Test
	public void testSet(){
		redis.sadd("userSet", "jerry1");
		redis.sadd("userSet", "jerry2");
		redis.sadd("userSet", "jerry3");
		redis.sadd("userSet", "jerry4");
		
		redis.srem("userSet", "jerry4");//remove
		System.out.println(redis.smembers("userSet"));//[jerry1,jerry2,jerry3]
		System.out.println(redis.sismember("userSet", "jerry4"));//false
		System.out.println(redis.srandmember("userSet"));//jerry3
		System.out.println(redis.scard("userSet"));//3
		System.out.println(redis.sinter("userSet"));//[jerry1, jerry3, jerry2]
	}
	
	@Test
	public void testHash(){
		redis.hset("hashs", "entryKey", "entryValue");
		redis.hset("hashs", "entryKey1", "entryValue1");
		redis.hset("hashs", "entryKey2", "entryValue2");
		System.out.println(redis.hexists("hashs", "entryKey1"));
		System.out.println(redis.hget("hashs", "entryKey1"));
		System.out.println(redis.hgetAll("hashs"));//{entryKey=entryValue, entryKey2=entryValue2, entryKey1=entryValue1}
		System.out.println(redis.hkeys("hashs"));//[entryKey2, entryKey, entryKey1]
		System.out.println(redis.hvals("hashs"));//[entryValue, entryValue1, entryValue2]
	}
	
	@Test
	public void test(){
		redis.del("test");
		redis.rpush("test", "1");
		redis.lpush("test", "6");
		redis.lpush("test", "3");
		redis.lpush("test", "9");
		System.out.println(redis.lrange("test", 0, -1));//[9, 3, 6, 1]
	}
	
	@Test
	public void testJedisPool(){
		RedisUtil.getJedis().set("newname", "中文测试");	
		System.out.println(RedisUtil.getJedis().get("newname"));
	}
	
	@Test 
	public void testNotExist(){
		System.out.println(redis.get("tom_1111"));
	}
}
