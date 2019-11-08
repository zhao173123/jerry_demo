package com.sun.java8;

import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class LambdaTest {
	
	@Test
	public void testSort0() {
		List<String> names = Arrays.asList("02", "03", "01", "10", "8");
		Collections.sort(names, Comparator.reverseOrder());
		System.out.println(names);// [8, 10, 03, 02, 01]
	}

	//List<String> 排序
	@Test
	public void testSort1() {
		List<String> names = Arrays.asList("06", "4", "10", "3", "5");
		Collections.sort(names, Comparator.comparing(Integer::valueOf));
		System.out.println(names);// [3, 4, 5, 06, 10]
	}

	// lambda表达式和其好基友Stream的配合：map
	//List<String>迭代：大写字母变小写
	@Test
	public void testSort2() {
		List<String> names = new ArrayList<>();
		names.add("Taobao");
		names.add("baiDu");
		// map方法接受一个lamdba表达式，就是一个java.util.function.Function的实例
		 List<String> lowercaseNames0 = names.stream().map(name -> name.toLowerCase()).collect(Collectors.toList());
		 System.out.println(lowercaseNames0);// [taobao, baidu]

		// Method Reference
		 List<String> lowercaseNames1 = names.stream().map(String::toLowerCase).collect(Collectors.toList());
		System.out.println(lowercaseNames1);// [taobao,baidu]
		
        String result = names.stream().collect(Collectors.toList()).toString();
		System.out.println("string : " + result);//string : [Taobao, baiDu]
	}

	//FluentIterable的使用
	@Test
	public void testSort3() {
		List<String> names = new ArrayList<String>();
		names.add("Taobao");
		names.add("baidu");
		List<String> lowerCaseNames = FluentIterable.from(names).transform(name -> name.toLowerCase()).toList();
		System.out.println(lowerCaseNames);//[taobao,baidu]
	}

	/**
	 * Strings.padEnd(String, int min, char)
	 * 如果string.length < min, 使用char补齐
	 */
	@Test
	public void test0() {
		String[] arr = {"b", "a", "c"};
		for (Integer i : Lists.newArrayList(1, 2, 3)) {
			Stream.of(arr).map(item -> Strings.padEnd(item, i, '@')).forEach(System.out::print);
			System.out.println();//bac b@a@c@ b@@a@@c@@
		}
	}
	
	@Test
	public void test1(){
		Stream.iterate(1, item -> item+1).limit(10).forEach(System.out::print);// 12345678910
		System.out.println(" ");

		Stream<Integer> integerStream = Stream.of(1,2,3,5);
		Iterator<Integer> it = integerStream.iterator();
		System.out.println(it.next() + " ");//1
	}
	
	@Test
	public void test2(){
		List<Integer> nums = Lists.newArrayList(1,null,3,4,null,6);
		long count = nums.stream().filter(num->num!=null).count();//不为null的元素数量
		System.out.println(count);// 4

		List<Integer> nums2 = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
		//filter:1 1 2 3 4 5 6 7 8 9 10
		//distinct:1 2 3 4 5 6 7 8 9 10
		//peek:生成一个包含原stream所有元素的stream，同时提供一个消费函数，新stream每个元素被消费都会执行给定的消费函数
		//skip:返回一个丢弃原stream的前n个元素后剩下的元素组成的新stream
		//limit：截取前n个元素
		System.out.println(nums2.stream()
				.filter(num -> num != null)
				.distinct()
				.mapToInt(num -> num *2)// mapToInt:转换数据 1 2 3 4 5 6 7 8 9 10 -> 2 4 6 8 10 12 14 16 18 20
//				.peek(System.out::print)// 2 4 6 8 10 12 36
				.skip(2)// 6 8 10 12 14 16 18 20
				.limit(4)// 6 8 10 12
				.sum());// 36
	}
	
	//old list -> new list
	@Test
	public void test4(){
		List<Integer> nums2 = Lists.newArrayList(1,1,null,2,3,4,null,5,6,7,8,9,10);
		List<Integer> numsWithoutNull = nums2.stream()
				.filter(num -> num!=null)
				.collect(() -> new ArrayList<Integer>(), 
						(list, item) -> list.add(item), 
						(list1, list2) -> list1.addAll(list1))
				.subList(0, 5);
		numsWithoutNull.forEach(e -> System.out.print(e + " "));// 1 1 2 3 4 
	}
	
	//stream.reduce:聚合操作
	@Test
	public void test5(){
		List<Integer> ints = Lists.newArrayList(1,2,3,4,5,6,7,8,9,10);
		System.out.println("ints sum is : " + ints.stream().reduce((sum, item) -> sum + item).get());//55
		System.out.println("ints sum is : " + ints.stream().reduce(0, (sum, item) -> sum + item));//55
	}

	//按照name过滤list，查找第一个符合条件的数据
	@Test
	public void testFilter(){
		List<Employee> employees = initEmployees();
		Employee ep = employees.stream()
				.filter(employee->employee.getName().equals("jerry"))
				.findFirst()
				.get();
		System.out.println("name: " + ep.getName());// name: jerry
	}
	
	@Test
	public void study1(){
		//foreach迭代
		Stream<String> stream = Stream.of("I", "Love", "you!");
		stream.forEach(str->System.out.print(str + " "));// I Love you! 
		System.out.println();
		//filter:函数原型Stream<T> filter(Predicate<? super T> predicate)，作用是返回一个只包含满足predicate条件元素的Stream
		//保留长度=3的字符
		Stream<String> stream1 = Stream.of("I", "Love", "you", "too");
		stream1.filter(str->str.length()==3).forEach(str->System.out.print(str + " "));// you too 
		System.out.println();
		//distinct：Stream<T> distinct()，作用是返回一个去除重复元素之后的Stream
		Stream<String> stream2 = Stream.of("I", "Love", "you", "too", "too");
		stream2.distinct().forEach(str->System.out.print(str + " "));// I Love you too 
		System.out.println();
		//sorted()：Stream<T>　sorted()+Stream<T>　sorted(Comparator<? super T> comparator)
		//自然排序+自定义比较排序,只能排序一次
		Stream<String> stream3 = Stream.of("I", "love", "you", "too");
		//I you too love
		stream3.sorted(Comparator.comparingInt(String::length)).forEach(str -> System.out.print(str + " "));
		System.out.println();
		//实验List<Intger>类型的排序
		List<Integer> ints = Lists.newArrayList(1, 2, 3);
		ints = ints.stream().sorted(Comparator.comparingInt(ints3 -> ints3)).collect(Collectors.toList());
		ints.forEach(i->System.out.print(i + " "));//1 2 3
		System.out.println();
		ints = ints.stream().sorted((ints1, ints2)-> ints2 - ints1).collect(Collectors.toList());
		ints.forEach(i->System.out.print(i + " "));//3 2 1
		System.out.println();
		//实验List<Employee>中age的排序
		List<Employee> employees = initEmployees();
		employees = employees.stream().sorted(Comparator.comparingInt(Employee::getAge)).collect(Collectors.toList());
		System.out.println("start sort List<Employee>.....");
		employees.forEach(e->System.out.print(e.getAge() + " "));// 18 19 23
		System.out.println();
		employees = employees.stream().sorted((e1,e2)->e2.getAge() - e1.getAge()).collect(Collectors.toList());
		employees.forEach(e->System.out.print(e.getAge() + " "));// 23 19 18
		System.out.println();
		System.out.println("end sort List<Employee>.....");
		//map():<R> Stream<R> map(Function<? super T,? extends R> mapper)
		//作用是返回一个对当前所有元素执行mapper之后的结果组成的Stream
		//直观的说，就是对每个元素按照某种操作进行转换，转换前后Stream中元素的个数不会改变，但元素的类型取决于转换之后的类型
		Stream<String> stream4 = Stream.of("I", "love", "you", "too");
		stream4.map(str->str.toUpperCase()).forEach(str->System.out.print(str + " "));// I LOVE YOU TOO
		System.out.println(" ");
		//flatMap():<R> Stream<R> flatMap(Function<? super T,? extends Stream<? extends R>> mapper)
		//作用是对每个元素执行mapper指定的操作，并用所有mapper返回的Stream中的元素组成一个新的Stream作为最终返回结果。
		//通俗的讲flatMap()的作用就相当于把原stream中的所有元素都"摊平"之后组成的Stream，转换前后元素的个数和类型都可能会改变
		Stream<List<Integer>> stream5 = Stream.of(Arrays.asList(1,2), Arrays.asList(3,4,5));
		stream5.flatMap(list->list.stream()).forEach(item->System.out.print(item + " "));//1 2 3 4 5
	}
	
	//规约操作：通过某个连接动作将所有元素汇总成一个结果的过程
	//元素求和、求最大值/最小值、求出元素总个数、将所有元素转换成一个列表或集合，都属于规约操作
	//stream有2个规约操作：reduce()+collect(),也有一些简化的操作：sum()+max()+min()+count()
	@Test
	public void study2(){
		//reduce():可以实现从一组元素中生成一个值
		//sum()、max()、min()、count()等都是reduce操作
		//reduce()的方法定义的3种重写方式：
		//1.Optional<T> reduce(BinaryOperator<T> accumulator)
		//2.T reduce(T identity, BinaryOperator<T> accumulator)
		//3.<U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator, BinaryOperator<U> combiner)
		
		//for example:从一组单词中找出最长的单词
		//Optional是一个值的容器，可以避免null
		Stream<String> stream1 = Stream.of("I", "Love", "you", "too");
		Optional<String> longest = stream1.reduce((s1, s2)->s1.length()>=s2.length() ? s1 : s2);
		System.out.println(longest.get());// Love
		//求单词长度之和
		Stream<String> stream2 = Stream.of("I", "Love", "you", "too");
		Integer lengthSum = stream2.reduce(0,//初始值 
								(sum, str)->sum+str.length(),//累加器
								(a,b)->a+b);//部分和拼接器，并行执行时才会用到
		System.out.println("firstSum: " + lengthSum);// 11
		
		stream2 = Stream.of("I", "Love", "you", "too");
		lengthSum = stream2.mapToInt(str->str.length()).sum();
		System.out.println("secondSum: " + lengthSum);//11
	}
	
	/**
	 * collect():lambda入门函数
	 * Collector是为Stream.collect()方法量身打造的工具接口（类）
	 * <p>
	 * 接口中的具体方法有两种，default方法和static方法，identity()就是Function接口的一个静态方法
	 * Function.identity()返回一个输出跟输入一样的Lambda表达式对象，等价于形如t -> t形式的Lambda表达式
	 * </p>
	 * <p>
	 * String::length格式解析:方法引用，代替特定格式的表达式
	 * 如果Lambda表达式的全部内容就是调用一个已有的方法，那么可以用方法引用来替代Lambda表达式
	 * 细分为4类，比如：
	 * 1.引用静态方法：Integer::sum
	 * 2.引用某个对象的方法List::add
	 * 3.引用某个类的方法String::length
	 * 4.引用构造方法HashMap::new
	 * </p>
	 * ps:将一个stream转换为一个容器需要做那些工作：
	 * 1.目标容器（set，list，map）
	 * 2.新元素如何添加到容器中
	 * 3.如果并行的进行规约，还需要告诉collect()多个部分结果如何进行合并
	 * 据此，collect()方法定义为：
	 * <R> R collect(Supplier<R> supplier, BiConsumer<R,? super T> accumulator, BiConsumer<R,R> combiner)
	 * 3个参数对应上述三条分析
	 * 另一种定义：<R,A> R collect(Collector<? super T,A,R> collector)
	 */
	@Test
	public void study3(){
		/**将stream规约成list的2种方式 **/
		Stream<String> stream1 = Stream.of("I", "Love", "you", "too");
		List<String> list2 = stream1.collect(Collectors.toList());//方式1，比较通用
		list2.forEach(item->System.out.print(item+ " "));//I Love you too
		System.out.println();
		//方式2:
		//2.1 目标容器 ArrayList:new 
		//2.2 新元素如何添加 ArrayList:add 
		//2.3 规约时多个结果如何合并 ArrayList::addAll
		stream1 = Stream.of("I", "Love", "you", "too");
		list2 = stream1.collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
		System.out.print("ArrayList1:");
		list2.forEach(item->System.out.print(item+ " "));//I Love you too
		System.out.println();
		/**  
		 * 使用Collectors工具很方便的生成list/set/map，
		 * 但是因为我们并不知道返回的是接口类型是什么（不确定）
		 * 如果需要指定容器的实际类型，可以通过Collectors.toCollection(Supplier<C> collectionFactory)完成
		 *     
		 **/
		//Stream->List
		stream1 = Stream.of("I", "Love", "you", "too");
//		List<String> list1 = stream1.collect(Collectors.toList());//->List
		ArrayList<String> list1 = stream1.collect(Collectors.toCollection(ArrayList::new));
		System.out.print("ArrayList2:");
		list1.forEach(item->System.out.print(item + " "));//I Love you too
		System.out.println();
		stream1 = Stream.of("I", "Love", "you", "too");
//		Set<String> set1 = stream3.collect(Collectors.toSet());//->Set
		HashSet<String> set1 = stream1.collect(Collectors.toCollection(HashSet::new));
		System.out.print("HashSet1:");
		set1.stream().forEach(item->System.out.print(item + " "));//too Love I you
		System.out.println();
		
		/**************************************************************************************************** 
		 * Stream背后依赖某种数据源，可以是数组、容器等，但不能是Map 
		 * 反过来从Stream生成Map是可以的，但我们要清楚Map的key和value是什么
		 * 通常以下三种情况下collect()的结果是Map
		 * 1.Collectors.toMap():用户指定key、value
		 * 2.Collectors.partitioningBy()：
		 * 		对元素进行二分区操作时用到,适用于将stream中的元素依据某个二值逻辑（满足条件或不满足）分成互补相交的两部分
		 * 		一般而言，最终形成的结果就是true-{}/false-{}
		 * 3.Collectors.grougingBy()：
		 * 		对元素做group操作时用到，按照某个属性对数据进行分组。属性相同的元素会被对应到Map的同一个key上。
		 * 		增强版groupingBy允许对元素分组之后再执行某种运算，比如求和、计数、平均值、类型转换等。
		 * 		这种先将元素分组的收集器叫做上级收集器，之后执行的其他运算叫做下游收集器
		 * 
		 * **************************************************************************************************/
		stream1 = Stream.of("I", "Love", "you", "too");
		//1.Collectors.toMap(...)
		//key:java.util.function.Function.identity()
		//value:String
		Map<String, String> map = stream1.collect(Collectors.toMap(java.util.function.Function.identity(), String::new));//->Map
		map.forEach((k,v)->System.out.println(k + ":" + v));
		//1.1 toMap
		List<Employee> employees = initEmployees();
		Map<String, Employee> mapEmployee = employees.stream()
				.collect(Collectors.toMap(Employee::getName, employee->employee, (k1,k2) -> k1));
		Collection<Employee> cols1 = mapEmployee.values();
		System.out.println("Object toMap start.......");
		cols1.forEach(item->{
			//name:michael,age:19,depart:mainiway
			//name:tom,age:18,depart:c2m
			//name:jerry,age:23,depart:c2m
			System.out.println("name:" + item.getName() + ",age:" + item.getAge() + ",depart:" + item.getDepartment().getDepartName());
		});
		System.out.println("Object toMap end.......");
		//2.Collectors.partitioningBy(...) 按照年龄分组
		employees = initEmployees();
		Map<Boolean, List<Employee>> passingFailing = employees.stream().collect(Collectors.partitioningBy(s->s.getAge() > 20));
		Collection<List<Employee>> cols = passingFailing.values();
		System.out.println("partitioningBy age=>");
		cols.forEach(items->{
			// items(0).get(0):name:michael,age:19,depart:mainiway
			// items(1).get(0):name:jerry,age:23,depart:c2m
			// items(1).get(1):name:tom,age:18,depart:c2m
			items.stream().forEach(item->{
				System.out.println("name:" + item.getName() + ",age:" + item.getAge() + ",depart:" + item.getDepartment().getDepartName());
			});
			System.out.println();
		});
		//3.Collectors.grougingBy(...) 按照部门分组员工
		employees = initEmployees();
		Map<Department, List<Employee>> byDept = employees.stream()
				.collect(Collectors.groupingBy(Employee::getDepartment));
//		//Map.values()转为List
		cols = byDept.values();
		System.out.println("groupingBy=>");
		cols.forEach(items->{
			// items(0).get(0):name:tom,age:18,depart:c2m
			// items(0).get(1):name:michael,age:19,depart:mainiway
			// items(1).get(0):name:jerry,age:23,depart:c2m
			items.stream().forEach(item->{
				System.out.println("name:" + item.getName() + ",age:" + item.getAge() + ",depart:" + item.getDepartment().getDepartName());
			});
			System.out.println();
		});
		//按照年龄来分组
		//Map的正常遍历还是比较好使，values的使用不灵活
		//Map.values()转为数组->Object[] list = byDept.values().toArray();
		employees = initEmployees();
		Map<Integer, List<Employee>> byAges = employees.stream()
				.filter(employee->employee.age<20)
				.collect(Collectors.groupingBy(employee->employee.age, Collectors.toList()));
		List<List<Employee>> result = new ArrayList<List<Employee>>(byAges.values());
		System.out.println("Map.values()=>");
		// name:tom,age:18,depart:c2m
		// name:michael,age:19,depart:mainiway
		result.forEach(item->{
			item.forEach(employee->{
				System.out.println("name:" + employee.getName() + ",age:" + employee.getAge() +
						",depart:" + employee.getDepartment().getDepartName());
			});
		});
		//使用下游收集器统计每个部门的人数
		employees = initEmployees();
		Map<Department, Long> totalByDept = employees.stream()
				.collect(Collectors.groupingBy(Employee::getDepartment,//上游收集器（分组） 
						Collectors.counting()));//下游收集器
		System.out.println("统计每个部门的人数=>");
		// mainiway:1
		// c2m:2
		totalByDept.forEach((k ,v)->System.out.println(k.getDepartName() + ":" + v));
		//按照部门对员工分布组，并只保留员工的名字
		employees = initEmployees();
		Map<Department, List<String>> byDepart = employees.stream()
				.collect(Collectors.groupingBy(Employee::getDepartment,//key:department
						Collectors.mapping(Employee::getName, //value:list<String> 下游收集器
						Collectors.toList())));//更下游收集器
		System.out.println("按照部门对员工分布组=>");
		// mainiway:[michael]
		// c2m:[jerry, tom]
		byDepart.forEach((k, v) -> System.out.println(k.getDepartName() + ":" + v));
 	}
	
	//使用collect()做字符串join
	//Collectors.joining()方法有三种重写形式，分别对应三种不同的拼接方式
	@Test
	public void study4(){
		Stream<String> stream1 = Stream.of("I", "love", "you");
		// 使用Collectors.joining()拼接字符串
		String result1 = stream1.collect(Collectors.joining());
		System.out.println(result1);//Iloveyou
		
		stream1 = Stream.of("I", "love", "you");
		result1 = stream1.collect(Collectors.joining(","));
		System.out.println(result1);//I,love,you
		
		stream1 = Stream.of("I", "love", "you");
		result1 = stream1.collect(Collectors.joining(",", "{", "}"));
		System.out.println(result1);//{I,love,you}
	}
	
	/**
	 * java stream 流水线
	 * 理解stream更关心：1.流水线 2.自动并行
	 *  
	 * 流水线解决方案：
	 * 使用某种方式记录用户的每一次操作，当用户调用结束后将之前的操作叠加到一起在一次迭代中全部执行
	 * 有几个问题需要解决：
	 * 1.<p>用户的操作如何记录</p>：一个完整的操作是指<数据来源、操作、回调函数>构成的三元数组，stream中使用stage概念，
	 *   并用某种实例化后的PipelineHelper来代表stage，将具有顺序的stage连到一起构成整个流水线
	 * 
	 *	Collection            Head<------------->StatelessOp<------>StatelessOp<--------->StatefulOp
     *       	     stream()            filter()                 map()               sort()
	 *  data source---------->stage0()------------->stage1()---------->stage2()--------------->stage3()
	 * PS:Collections.stream()得到Head也就是stage0，紧接着调用一系列的中间操作，不断产生新的stream.
	 * 这些stream对象以双向链表的形式组织在一起，构成了整个流水线。
	 * 每一个Stage都记录了前一个Stage和本次的操作以及回调函数，依靠这种结构就能建立起对数据源的所有操作。
	 * 以上就是stream记录操作的方式
	 * 2.<p>操作如何叠加</p>：Sink接口协调相邻Stage之间的调用关系
	 * 	Sink接口：
	 * 		方法名					作用
	 *		void begin(long size)	开始遍历元素之前调用该方法，通知Sink做好准备。
	 *		boolean cancellationRequested()	是否可以结束操作，可以让短路操作尽早结束。
	 *		void accept(T t)		遍历元素时调用，接受一个待处理元素，并对元素进行处理。
	 *								Stage把自己包含的操作和回调方法封装到该方法里，
	 *								前一个Stage只需要调用当前Stage.accept(T t)方法就行了。
	 *		void end()				所有元素遍历完成之后调用，通知Sink没有更多的元素了。
	 * 	有了上面的协议，相邻Stage之间调用就很方便了，每个Stage都会将自己的操作封装到一个Sink里，
	 *  前一个Stage只需调用后一个Stage的accept()方法即可，并不需要知道其内部是如何处理的。
	 * 	当然对于有状态的操作，Sink的begin()和end()方法也是必须实现的：
	 * 	    比如Stream.sorted()是一个有状态的中间操作，
	 * 		其对应的Sink.begin()方法可能创建一个乘放结果的容器，
	 * 		而accept()方法负责将元素添加到该容器，
	 * 		最后end()负责对容器进行排序
	 * 	对于短路操作，Sink.cancellationRequested()也是必须实现的，
	 * 	比如Stream.findFirst()是短路操作，只要找到一个元素，cancellationRequested()就应该返回true，以便调用者尽快结束查找。
	 * 	<p>Sink的四个接口方法常常相互协作，共同完成计算任务。实际上Stream API内部实现的的本质，就是如何重载Sink的这四个接口方法。</p>
	 *  有了Sink对操作的包装，Stage之间的调用问题就解决了，
	 *  执行时只需要从流水线的head开始对数据源依次调用每个Stage对应的Sink.{begin(), accept(), cancellationRequested(), end()}方法就可以了
	 * 3.<p>叠加后的操作如何执行</p>
	 * 	结束操作会创建一个包装了自己操作的Sink，这也是流水线中最后一个Sink，
	 * 	这个Sink只需要处理数据而不需要将结果传递给下游的Sink（因为没有下游）。
	 * 	对于Sink的[处理->转发]模型，结束操作的Sink就是调用链的出口。
	 * 4.<p>执行后的结果（如果有）如何保存</p>
	 * 	 不是所有的stream结束操作都需要结果，有些操作只是使用了副作用，比如foreach（事实上，除了打印之外其他场景应该避免使用副作用）
	 * 	 大多数场景下应该使用归约操作代替副作用操作
	 *   <th>因为stream可能会并行执行，副作用无法保证正确性和效率。</th>
	 *   
	 *   有结果的Stream结束操作
	 *   -----------------------------------------------
	 *   -返回类型     对应的结束操作
	 *   -boolean     anyMatch()|allMatch()|noneMatch()
	 *   -Optional    findFirst()|findAny()
	 *   -规约结果	  reduce()|collect()
	 *   -数组         toArray()
	 *   -----------------------------------------------
	 *   
	 */
	@Test
	public void study5(){
		/** 求出以字母A开头的字符串的最大长度 **/
		List<String> list = Arrays.asList("abc","Abc", "Akjddj");
		int longestLength = list.stream()
	              .filter(s -> s.startsWith("A"))
	              .mapToInt(String::length)
	              .max()
	              .getAsInt();
		 System.out.println(longestLength);//6
		 
		 final ArrayList<String> result1 = Lists.newArrayList();
		 //错误的使用方式（副作用）
		 list.stream().filter(s->s.startsWith("A")).forEach(item->result1.add(item));
		 result1.forEach(item->System.out.print(item + " "));//Abc Akjddj
		 System.out.println();
		 //正确的收集方式
		 ArrayList<String> result2 = (ArrayList<String>) list.stream().filter(s->s.startsWith("A")).collect(Collectors.toList());
		 result2.forEach(item->System.out.print(item + " "));//Abc Akjddj
	}
	
	@Test
	public void testConvertMapKeysToList(){
		Map<String, String> map = Maps.newHashMap();
		map.put("1", "a");
		map.put("2", "b");
		map.put("3", "c");
		map.put("4", "d");
		List<String> keys = map.keySet().stream().collect(Collectors.toList());
		keys.forEach(item->{
			System.out.print(item + " ");
		});
	}
	
	@Test
	public void testCompartor(){
		List<Department> departments = Lists.newArrayList(new Department("1", "mainiway"), new Department("2", "c2m"));
//		departments.sort((d1,d2)->d1.getDepartName().compareTo(d2.getDepartName()));
		Collections.sort(departments, Comparator.comparing(Department::getDepartName));
		assertEquals(departments.get(0).getDepartName(), "c2m");
		assertEquals(departments.get(1).getDepartName(), "mainiway");
	}
	
	@Test
	public void testArrayIsEmpty(){
		List<String> goodsImages = Arrays.asList("acb", "cgd","abc", "", null)
									.stream()
									.filter(goodsImage->!StringUtils.isEmpty(goodsImage))
									.collect(Collectors.toList());
		goodsImages.stream().forEach(image->System.out.print(image + ","));
		System.out.println();
		String[] result = goodsImages.toArray(new String[0]);
		for(int i = 0; i < result.length; i++){
			System.out.print(result[i] + " ");
		}
	}
	

	private List<Employee> initEmployees() {
		List<Employee> employees = Lists.newArrayList();
		Employee e1 = new Employee();
		e1.setName("jerry");
		e1.setAge(23);
		Department dt1 = new Department();
		dt1.setId("1");
		dt1.setDepartName("c2m");
		e1.setDepartment(dt1);
		employees.add(e1);
		Employee e2 = new Employee();
		e2.setName("tom");
		e2.setAge(18);
		e2.setDepartment(dt1);
		employees.add(e2);
		Employee e3 = new Employee();
		e3.setName("michael");
		e3.setAge(19);
		Department dt2 = new Department();
		dt2.setId("1");
		dt2.setDepartName("mainiway");
		e3.setDepartment(dt2);
		employees.add(e3);
		return employees;
	}
	
	public class Employee{
		
		private String name;
		private Integer age;
		private Department department;
		
		public Employee(){
			super();
		}
		
		public Employee(String name, int age){
			super();
			this.name = name; 
			this.age = age;
		}

		public Integer getAge() {
			return age;
		}

		public void setAge(Integer age) {
			this.age = age;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		public String getName(){
			return this.name;
		}
		
		public void setDepartment(Department department) {
			this.department = department;
		}

		public Employee(String name, Integer age){
			this.name = name;
			this.age = age;
		}
		
		public Department getDepartment(){
			return this.department;
		}
		
		public String toString(){
			return "age : " + age + ", name : " + name;
		}
		
	}
	
	public class Department{
		private String id;
		private String departName;
		
		public Department(){
			
		}
		
		public Department(String id, String departName){
			this.id = id;
			this.departName = departName;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDepartName() {
			return departName;
		}
		public void setDepartName(String departName) {
			this.departName = departName;
		}
	}

	/**
	 *	jdk8引入了3个原始类型持久化流IntStream|DoubleStream|LongStream
	 *	分别将流中的元素转化成int|long|double，从而避免了装箱操作。
	 *  映射->数值流 mapToInt|mapToDouble|mapToLong
	 *
	 *
	 */
	@Test
	public void testIntStream(){
		int[] nums = {1, 2, 3, 4};
		//将int[]转为List<Integer>
		//先装箱，然后collect将结果转为list
		List<Integer> temp = IntStream.of(nums).boxed().collect(Collectors.toList());
		for (Integer i : temp) {
			System.out.println(i);
		}

		//IntStream实现斐波那契数列
		//IntUnaryOperator：接受int对象返回int对象
		IntStream is = IntStream.iterate(1, new IntUnaryOperator() {
			private int prev = 0;
			@Override
			public int applyAsInt(int operand) {
				int temp = operand + prev;
				prev = operand;
				return temp;
			}
		});
		//[1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765]
		System.out.println(Arrays.toString(is.limit(20).toArray()));

	}
}
