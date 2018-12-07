package com.sun.java8.lamdbaInternal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import org.junit.Test;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * 为了引入Lamdba，java8新增了java.util.function包，里面包含常用的函数接口
 * 
 * Collections中新增的方法：
 * 1.foreach(Consumer<? super E> action)：对容器中的每个元素执行action指定的动作
 * 
 * @author jerry
 *
 */
public class CollectionsTest {

	//需求：假设有一个字符串列表，需要打印出其中所有长度大于3的字符串
	@Test
	public void testForeach(){
		ArrayList<String> list = new ArrayList<String>(Arrays.asList("I", "love", "you", "too"));
		//JDK7
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).length() > 3)
				System.out.println("jdk7:" + list.get(i).toString());
		}
		//JDK8-1
		list.stream().forEach(new Consumer<String>(){//匿名内部类迭代实现Consumer接口
			@Override
			public void accept(String s) {
				if(s.length() > 3)
					System.out.println("jdk8:" + s);
			}
		});
		//JDK8-2
		list = new ArrayList<String>(Arrays.asList("I", "love", "you", "too"));
		list.stream().forEach(item->{// 类型推导的作用，不要关心accept、consumer这些实现
			if(item.length() > 3)
				System.out.println("jdk8:" + item);
		});
	}

	//需求：假设有一个字符串列表，需要删除其中所有长度大于3的字符串
	//删除容器中所有满足filter指定条件的数据，
	//其中Predicate是一个函数接口，只有一个待实现方法test(T t)
	@Test
	public void testRemoveIf(){
		ArrayList<String> list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
		//JDK7
		Iterator<String> it = list.iterator();
		while(it.hasNext()){
			if(it.next().length() > 3)// 删除长度大于3的字符串
				it.remove();
		}
		//JDK8-1
		list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
		list.removeIf(new Predicate<String>(){ // removeIf()结合匿名内部类实现
			@Override
			public boolean test(String str) {
				return str.length() > 3;
			}
		});
		//JDK8-2
		list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
		list.removeIf(str -> str.length() > 3);
	}
	
	//需求：假设有一个字符串列表，将其中所有长度大于3的元素转换成大写，其余元素不变
	//对每个元素执行operator指定的操作，并用操作结果来替换原来的元素
	//其中UnaryOperator是一个函数接口，里面只有一个待实现函数T apply(T t)
	@Test
	public void testReplaceAll(){
		ArrayList<String> list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
		//JDK7
		for(int i = 0; i < list.size(); i++){
			String str = list.get(i);
			if(str.length() > 3)
				list.set(i, str.toUpperCase());
		}
		//JDK8-1
		list.replaceAll(new UnaryOperator<String>(){
			@Override
			public String apply(String str) {
				if(str.length() > 3)
					return str.toUpperCase();
				return str;
			}
		});
		//JDK8-2
		list.replaceAll(str->{
			if(str.length() > 3)
				return str.toUpperCase();
			return str;
		});
	}
	
	//需求：假设有一个字符串列表，按照字符串长度增序对元素排序
	//sort(Comparator<? super E> c):根据c指定的比较规则对容器元素进行排序
	@Test
	public void testSort(){
		ArrayList<String> list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
		//JDK7
		Collections.sort(list, new Comparator<String>(){
			@Override
			public int compare(String o1, String o2) {
				return o1.length() - o2.length();
			}
		});
		//JDK8
		list.sort((str1, str2) -> str1.length() - str2.length());
	}
	
	//Spliterator<E> spliterator()：返回容器的可拆分迭代器
	//spliterator可以逐个迭代，也可以批量迭代，批量迭代可以降低迭代的开销
	//spliterator是可拆分的，一个Spliterator可以调用trySplit()拆分为2个，一个this，一个是新返回的那个
	//2个迭代器没有重复
	
	//需求：假设有一个数字到对应英文单词的Map，请输出Map中的所有映射关系
	@Test
	public void testMapForeach(){
		Map<Integer, String> map = Maps.newHashMap();
		map.put(1, "one");
		map.put(2, "two");
		map.put(3, "three");
		//JDK7
		System.out.println("JDK7:");
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			System.out.println(entry.getKey() + "=" + entry.getValue());
		}
		
		//JDK8-1
		System.out.println("JDK8-1:");
		map.forEach(new BiConsumer<Integer, String>(){
			@Override
			public void accept(Integer k, String v) {
				System.out.println(k + "=" + v);
			}
		});
		//JDK8-2
		System.out.println("JDK8-2:");
		map.forEach((k, v)->{
			System.out.println(k + "=" + v);
		});
	}
	
	//需求：假设有一个数字到对应英文单词的Map，输出4对应的英文单词，如果不存在则输出NoValue
	//V getOrDefault(Object key, V defaultValue):按照给定的key查询map中对应的value
	//如果没有找到就返回defaultValue
	//使用该方法可以省去查询指定键值是否存在的麻烦
	@Test
	public void testGetOrDefault(){
		HashMap<Integer, String> map = new HashMap<>();
		map.put(1, "one");
		map.put(2, "two");
		map.put(3, "three");
		//JDK7
		if(map.containsKey(4)){
			System.out.println(map.get(4));
		}else{
			System.out.println("NoValue");
		}
		//JDK8
		System.out.println(map.getOrDefault(4, "NoValue"));
	}
	
	//V putIfAbsent(K key, V value):不存在key值的映射或映射值为null时，才将value指定的值放入map；否则map不做修改
	//该方法将判断和赋值合二为一，比较方便
	
	//remove(Object key):根据指定key删除map映射
	//remove(Object key, Object value):java8新增,key正好映射到value才删除，否则啥都不做
	
	//replace(K key, V value):只有在当前Map中key的映射存在时才用value去替换原来的值，否则什么也不做
	//replace(K key, V oldValue, V newValue):只有在当前Map中key的映射存在且等于oldValue时才用newValue去替换原来的值，否则什么也不做．

	//需求：假设有一个数字到对应英文单词的Map，请将原来映射关系中的单词都转换成大写．
	//replaceAll
	@Test
	public void testMapReplaceAll(){
		HashMap<Integer, String> map = new HashMap<>();
		map.put(1, "one");
		map.put(2, "two");
		map.put(3, "three");
		//JDK7
		for(Map.Entry<Integer, String> entry : map.entrySet()){
			entry.setValue(entry.getValue().toUpperCase());
		}
		//JDK8-1
		map.replaceAll(new BiFunction<Integer, String, String>(){
			@Override
			public String apply(Integer Integer, String value) {
				return value.toUpperCase();
			}
		});
		//JDK8-2
		map.forEach((k, v) -> v.toUpperCase());
	}
	
	//merge(K key, V value, BiFunction<? super V,? super V, ? extends V> remappingFunction)
	//1.如果Map中key对应的映射不存在或者为null，则将value（不能是null）关联到key上
	//2.否则执行remappingFunction，如果执行结果非null则用该结果跟key关联，否则在Map中删除key的映射
	//实例：map.merge(key, newMsg, (v1, v2) -> v1+v2);//将错误信息拼接到原来的信息上
	
	//compute(K key, BiFunction<? super K,? super V,? extends V> remappingFunction)：
	//把remappingFunction的计算结果关联到key上，如果计算结果为null，则在Map中删除key的映射
	//实例：map.compute(key, (k,v) -> v==null ? newMsg : v.concat(newMsg));//错误信息拼接
	
	//V computeIfAbsent(K key, Function<? super K,? extends V> mappingFunction):
	//只有在当前Map中不存在key值的映射或映射值为null时，才调用mappingFunction，并在mappingFunction执行结果非null时，将结果跟key关联
	@Test
	public void testComputeIfAbsent(){
		Map<Integer, Set<String>> map = new HashMap<>();
		//JDK7
		if(map.containsKey(4)){
			map.get(4).add("One");
		}else{
			Set<String> set = Sets.newHashSet();
			set.add("One");
			map.put(1, set);
		}
		//JDK8
		map.computeIfAbsent(4, v -> new HashSet<String>()).add("One");//computeIfAbsent()将条件判断和添加操作合二为一，使代码更加简洁
	}
	
	
}
