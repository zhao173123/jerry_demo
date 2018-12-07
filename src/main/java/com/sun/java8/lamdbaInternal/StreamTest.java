package com.sun.java8.lamdbaInternal;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

/**
 * BaseStream->{IntStream, LongStream, DoubleStream, Stream} 
 * ·懒式执行，只有真正需要时才执行
 * ·可消费性，只能遍历一次
 * 
 * stream的操作分为中间操作和结束操作
 * 中间操作只是一种标记，只有结束操作才会触发执行。
 * ·中间操作总是会懒式执行,中间操作又有无状态和有状态之分
 * 无状态是指元素的处理不受前面元素的影响，而有状态的操作必须等到前面的所有元素操作完毕才能知道结果。
 * 		· 无状态（Stateless）：「unordered(),filter(),map(),mapToInt(),mapToLong(),mapToDouble(),
 * 							   flatMap(),flatMapToInt(),flatMapToLong(),flatMapToDouble(),peek()」
 * 		· 有状态（Stateful）：「distinct(),sorted(),limit(),skip()」
 * ·结束操作触发实际计算,结束操作又有短路操作和非短路操作之分
 * 短路操作是指不用处理全部元素就可以返回结果，比如找到第一个满足条件的元素。
 * 		· 非短路操作「forEach(),forEachOrdered(),toArray(),reduce(),collect(),max(),min(),count()」
 * 		· 短路操作「anyMatch(),allMatch(),noneMatch(),findFirst(),findAny()」
 * 
 * @author jerry
 *
 */
public class StreamTest {

	@Test
	public void testForEach(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too");
		stream.forEach(item->{
			System.out.println(item);
		});
	}
	
	@Test
	public void testFilter(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too");
		stream.filter(str -> str.length() == 3)
			.forEach(str -> System.out.println(str));
	}
	
	@Test
	public void testDistinct(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too", "Too");
		stream.distinct().forEach(str -> System.out.println(str));
	}
	
	@Test
	public void testSort(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too");
		stream.sorted((str1, str2)->str1.length() - str2.length())
			.forEach(str -> System.out.println(str));
	}
	
	@Test
	public void testMap(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too");
		stream.map(str->str.toUpperCase())
			.forEach(str->System.out.println(str));
	}
	
	/**
	 * <R> Stream<R> flatMap(Function<? super T,? extends Stream<? extends R>> mapper):
	 * 对每个元素执行mapper指定的操作，并用所有mapper返回的Stream中的元素组成一个新的Stream作为最终返回结果
	 * 通俗的讲flatMap()的作用就相当于把原stream中的所有元素都"摊平"之后组成的Stream，转换前后元素的个数和类型都可能会改变
	 */
	@Test
	public void testFlatMap(){
		Stream<List<Integer>> stream = Stream.of(Arrays.asList(1, 2), Arrays.asList(3, 4, 5));
		stream.flatMap(list->list.stream()).forEach(i->System.out.println(i));
	}
	
	/******************************************
	 * 
	 * 规约操作：通过某个连接动作将所有元素汇总成一个结果的过程
	 * 元素求和、求最大值或最小值、求元素总个数、将所有元素转换成一个列表或集合都属于规约操作
	 * stream有2个通用的规约操作：reduce和collect
	 * 其他诸如sum、max、min、count等只是因为常用，所以单独设为函数
	 * reduce()有3种重写形式：
	 * ·Optional<T> reduce(BinaryOperator<T> accumulator)
	 * ·T reduce(T identity, BinaryOperator<T> accumulator)
	 * ·<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)
	 * 说明：reduce最常用的地方就是从一堆值中生成一个值
	 * 
	 *****************************************/
	//需求：从一组单词中找出最长的单词
	@Test
	public void testGetMaxWord(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too");
		//Optional是（一个）值的容器，使用它可以避免null值的麻烦。
		//当然可以使用Stream.max(Compartor<? super T> comparator)来达到同样效果，但是reduce()自有存在的理由
		Optional<String> longest = stream.reduce((s1, s2) -> s1.length() >= s2.length() ? s1 : s2);
		System.out.println(longest.get());
	}
	//需求：求出一组单词的长度之和
	@Test
	public void testSumWord(){
		Stream<String> stream = Stream.of("I", "Love", "You", "Too");
		Integer lengthSum = stream.reduce(0, // 初始值（幺元）
				(sum, str) -> sum + str.length(), //累加器（新元素如何累加）
				(a, b) -> a+b); //部分和拼接器，并行执行时会用到（多个部分如何合并）
		System.out.println(lengthSum);
//		System.out.println(stream.mapToInt(str->str.length()));//效果相同
	}
	
	/*******************************************
	 * reduce擅长的是生成一个值，如果想要stream生成一个集合或者Map等复杂结构，
	 * collect()就是必然选择
	 * 先理解几个概念：
	 * ·Function是一个接口,identity()是Function接口的一个静态方法
	 * ·Function.identity()返回一个输出跟输入一样的Lambda表达对象，等价于t->t形式
	 * Collector（收集器）是为Stream.collect()量身打造的工具接口（类）。
	 * Stream转换为一个容器：
	 * 1.目标容器是什么（list|set|map）
	 * 2.新元素如何添加（是list.add还是map.put(k,v)）
	 * 3.如果进行规约操作，还要知道多个部分结果如何合并
	 * 所以，collect()定义为：
	 * <R> R collect(Supplier<R> supplier, BiConsumer<R,? super T> accumulator, BiConsumer<R,R> combiner)
	 * <R,A> R collect(Collector<? super T,A,R> collector)
	 * 
	 ******************************************/
	@Test
	public void testCollect(){
		Stream<String> stream = Stream.of("I", "love", "you", "too");
		//使用collect()生成Collection
		List<String> list = stream.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

		stream = Stream.of("I", "love", "you", "too");
		list = stream.collect(Collectors.toList());
		
		stream = Stream.of("I", "love", "you", "too");
		Set<String> set = stream.collect(Collectors.toSet());
		
		//使用toCollection指定规约容器类型
		stream = Stream.of("I", "love", "you", "too");
		ArrayList<String> arrayList = stream.collect(Collectors.toCollection(ArrayList::new));
		stream = Stream.of("I", "love", "you", "too");
		HashSet<String> hashSet = stream.collect(Collectors.toCollection(HashSet::new));
		
		/**
		 * 使用collect生成Map
		 * 通常有3种方式：
		 * ·Collectors.toMap():需要指定key和value
		 * ·Collectors.partitioningBy():对元素进行二分区操作时用到
		 * ·Collectors.groupingBy():对元素进行group操作时用到
		 */
		stream = Stream.of("I", "love", "you", "too");
		Map<String, String> map = stream.collect(Collectors.toMap(Function.identity(), String::new));
		map.forEach((k,v)->System.out.println(k + "=" + v));
		//partitioningBy和groupingBy参照LambdaTest.study3()
	}

	//字符串join
	@Test
	public void testJoin(){
		Stream<String> stream = Stream.of("I", "love", "you");
//		String joined = stream.collect(Collectors.joining());//Iloveyou
//		String joined = stream.collect(Collectors.joining(","));//I,love,you
		String joined = stream.collect(Collectors.joining(",", "{", "}"));
		Assert.assertEquals("{I,love,you}", joined);
	}
	
	
}

