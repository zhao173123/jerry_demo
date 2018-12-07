package com.sun.java8.concurrent.atomic.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.sun.json.vo.Person;

/**
 * Tests for Atomic
 * 
 * @author jerry
 *
 */
public class AtomicTest {

	@Test
	public void testAtomicBoolean(){
		AtomicBoolean ab = new AtomicBoolean();
		assertFalse(ab.get());//可以看出AtomicBoolean默认构造函数的值是false
		ab = new AtomicBoolean(true);
		assertTrue(ab.get());
		ab.set(false);//提供了set方法
		assertFalse(ab.get());
		
		boolean casBoolean = ab.compareAndSet(false, true);//CAS方式修改值
		assertTrue(casBoolean);
		
		ab.getAndSet(false);
		assertFalse(ab.get());
		
	}
	
	@Test
	public void testAtomicInteger() throws InterruptedException{
		AtomicInteger ai = new AtomicInteger();
		assertEquals(0, ai.get());
		ai.incrementAndGet();
		assertEquals(1, ai.get());
		ai.addAndGet(1);
		assertEquals(2, ai.get());
		ai.compareAndSet(2, 3);
		assertEquals(3, ai.get());
		int ai1 = ai.getAndIncrement();
		assertEquals(3, ai1);
		assertEquals(4, ai.get());
		
		ai.accumulateAndGet(1, new IntBinaryOperator(){
			@Override
			public int applyAsInt(int left, int right) {
				return left + right;
			}});
		assertEquals(5, ai.get());
		
		int ai2 = ai.getAndAccumulate(1, (left, right) -> left + right);
		assertEquals(5, ai2);//prev
		assertEquals(6, ai.get());//current
		
		int ai3 = ai.getAndUpdate(operand -> operand + 1);
		assertEquals(6, ai3);//prev
		assertEquals(7, ai.get());
		
		int ai4 = ai.updateAndGet(operand -> operand + 1);
		assertEquals(8, ai4);
		assertEquals(8, ai.get());
		
		final AtomicInteger value = new AtomicInteger(1);
		Thread[] ts = new Thread[5];
		for(int i = 0; i < ts.length; i++){
			ts[i] = new Thread(value::incrementAndGet);
		}
		for(Thread t : ts){
			t.start();
		}
		for(Thread t : ts){
			t.join(); //等待所有ts执行结束
		}
		assertEquals(6, value.get());
	}
	
	@Test
	public void testAtomicReference(){
		AtomicReference<Person> ar = new AtomicReference<Person>();
		assertEquals(null, ar.get());
		Person person = new Person("Sugar", 18);
		ar.set(person);
		assertEquals("Sugar", ar.get().getName());
		Person nowPerson1 = ar.accumulateAndGet(new Person(){
			private static final long serialVersionUID = 1L;
			{
				setName("Jerry");
				setAge(3);
			}
		}, (p1, p2) -> new Person("Parent", p1.getAge() + p2.getAge()));
		assertEquals("Parent", nowPerson1.getName());
		assertEquals(new Integer(21), nowPerson1.getAge());
	}	
	
	/**
	 * 源码分析：
	 * 在存储对象时，存放第一个元素的内存地址相对于数组对象起始地址的内存偏移量，base = 16
	 * base = unsafe.arrayBaseOffset(int[].class);
	 * scale:数组元素的大小，占用多少个字节
	 * shift:用来定位数组中的内存位置
	 * 根据scale，base定位到任意一个下标的地址：
	 * 	scale=4，1个int类型在java中占用4个字节，一个字节8位，这样int类型数据占用32位；
	 *  Integer.numberOfLeadingZeros(scale)返回scale高位连续0的个数(29)，
	 *  0000|0000|0000|0000|0000|0000|0000|0100，所以shift = 2；
	 *  每向左移动移位，在不越界的基础上，相当于*2;
	 *  也就是int类型长度为4，第0个位置0，第i个位置是i*4,就是i<<2
	 * int scale = unsafe.arrayIndexScale(int[].class);
	 * shift = 31 - Integer.numberOfLeadingZeros(scale);
	 *
	 * @throws InterruptedException 
	 */
	@Test
	public void testAtomicIntegerArray() throws InterruptedException{
		int[] array = new int[]{1, 3, 5, 7, 9};
		AtomicIntegerArray aia = new AtomicIntegerArray(array);
		assertEquals(1, aia.get(0));
		assertEquals(7, aia.get(3));
		assertEquals(5, aia.length());
		
		aia.compareAndSet(3, 7, 11);
		assertEquals(11, aia.get(3));
		
		int aia1 = aia.getAndSet(3, 13);
		assertEquals(11, aia1);
		assertEquals(13, aia.get(3));
		
		aia.getAndAdd(3, 1);
		assertEquals(14, aia.get(3));
		
		/** 验证原子性 **/
		AtomicIntegerArray aiaAtomic = new AtomicIntegerArray(5);
		Thread addThread = new Thread(new Runnable(){
			@Override
			public void run() {
				for(int i = 0; i < 10; i++){//相当于数组中每个下标的数据累加2次
					aiaAtomic.getAndIncrement(i % aiaAtomic.length());
				}
			}
		});
		//启动10个线程，最终结果数组的每个下标数据为20
		Thread[] ts = new Thread[10];
		for(int j = 0; j < 10; j++){
			ts[j] = new Thread(addThread);
		}
		for(int k = 0; k < 10; k++)
			ts[k].start();
		for(int k = 0; k < 10; k++)
			ts[k].join();
		//[20, 20, 20, 20, 20]
		System.out.println(aiaAtomic);
	}
	
	/**
	 * AtomicIntegerFieldUpdater:基于反射的原子更新字段的值
	 * 使用条件：
	 * 1.实体类中的字段必须是volatile类型的，在线程之间共享变量时保证立即可见
	 * 2.字段的描述类型(private|public|protected|default)与调用者和操作对象字段的关系一致，
	 * 	  即调用者可以直接操作对象字段，那么就可以反射进行原子操作,父类字段除外
	 * 3.只能是实例变量，不能是类变量，也就是不能加static
	 * 4.不能加final
	 * 5.只能修改int|long类型，不能修改包装类型Integer|Long
	 * 	 如果要修改包装类型使用AtomicReferenceFieldUpdater
	 * 
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void testAtomicIntegerFieldUpdater() {
		AtomicData ad = new AtomicData();
		AtomicIntegerFieldUpdater<AtomicData> aifu = 
				(AtomicIntegerFieldUpdater<AtomicData>) getAtomicIntegerFieldUpdater(ad.getClass(), "value1");
		aifu.compareAndSet(ad,1,11);
		assertEquals(11, ad.value1);
		
		/** 下面的反射private|protected volatile会异常 **/
//		aifu = (AtomicIntegerFieldUpdater<AtomicData>) getAtomicIntegerFieldUpdater(ad.getClass(), "value2");
//		aifu.compareAndSet(ad, 2, 12);
//		assertEquals(12, ad.value2);
//		aifu = (AtomicIntegerFieldUpdater<AtomicData>) getAtomicIntegerFieldUpdater(ad.getClass(), "value3");
//		aifu.compareAndSet(ad, 3, 13);
//		assertEquals(13, ad.value3);
		
//		aifu = (AtomicIntegerFieldUpdater<AtomicData>) getAtomicIntegerFieldUpdater(ad.getClass(), "value4");
//		aifu.compareAndSet(ad, 4, 14);
//		assertEquals(14, ad.value4);//must be volatile type
		
//		aifu = (AtomicIntegerFieldUpdater<AtomicData>) getAtomicIntegerFieldUpdater(ad.getClass(), "value5");
//		aifu.compareAndSet(ad, 5, 15);
//		assertEquals(Integer.valueOf(15), ad.value5);//must be integer type
	}
	
	private static AtomicIntegerFieldUpdater<?> getAtomicIntegerFieldUpdater(Class<?> tclass, String name){
		return AtomicIntegerFieldUpdater.newUpdater(tclass, name);
	}
	
	public static class AtomicData{
		public volatile int value1 = 1;
		@SuppressWarnings("unused")
		private volatile int value2 = 2;
		protected volatile int value3 = 3;
		public static int value4 = 4;
		public volatile Integer value5 = 5;
	}
	
	/**
	 * AtomicStampedReference是用来解决ABA问题，
	 * 内部不仅维护了对象值，还维护了一个整形的时间戳。
	 * 当对象改变时，除了数据本身的更新外还要更新时间戳。
	 * 当设置值对象时，对象值和时间戳都必须满足期望才能写入。
	 * 因此，即使对象值被反复写入，写回原值，只要时间戳发生了改变，
	 * 就能防止不恰当的写入。
	 * 
	 * 测试代码参照ABA.java
	 * 
	 * AtomicMarkableReference并不能解决ABA问题，通过一个布尔值来标记是否更改。
	 * 本质就是true和false两种版本之间切换。
	 * 
	 */
	@Test
	public void testAtomicStampedReference(){
		Person person = new Person("Jerry", 30);
		int timestamp = 1;
		AtomicStampedReference<Person> asr = new AtomicStampedReference<Person>(person, timestamp);
		assertEquals(person, asr.getReference());
		assertEquals("Jerry", asr.getReference().getName());
		assertEquals(1, asr.getStamp());
	}

	/**
	 * Striped64： 
	 * Striped64就是把逻辑上连续的数据分为多个片段（通过内部的分散计算来避免竞争），
	 * 使这一序列的段存储在不同的物理设备上。
	 * 通过把段分散到多个设备上可以增加访问的并发性，从而提高整体的吞吐量。
	 * 
	 * 维护一个延迟加载初始化的、原子更新值的表Cell[](又叫hash表)和一个base字段维护数的初始值。
	 * Cell:表的类目,通过@sun.misc.Contended实现自动缓存行填充。
	 * 本质上是一个填充了AtomicLong的且提供CAS更新的volatile变量。
	 * 通过cellsBusy来控制resizing和创建Cell。
	 * 
	 * 在没有竞争的情况下，要累加的数通过CAS累加到base上；
	 * 如果有竞争的话，会将要累加的数累加到Cells数组中的某个cell元素里面，
	 * 所以整个Striped64的值为sum=base+∑[0~n]cells
	 * 
	 * 	三个重要的成员变量
	 * 	1.存放Cell的hash表，大小是2的幂
	 * 	transient volatile Cell[] cells;
	 * 	2.基础值 transient volatile long base;
	 * 	 2.1 在没有竞争的时候会更新这个值 
	 *	 2.2 在cell的初始化过程中，cells处于不可用状态，这时候也会尝试通过CAS操作值累加到base
	 *  3.自旋锁
     *  transient volatile int cellsBusy;
     *  在resizing和创建Cell时使用,用于保护创建或者扩展Cell表
	 *  当要修改cell[]数组时加锁，防止多线程同时修改cells数组，0为无锁，1为加锁。
	 *  加锁的状况有3种：
	 *  cells数组初始化|
	 *  cells数组扩容时|
	 *  如果cells数组种某个元素为null，给这个位置创建新的Cell对象的时候

	 * 				
	 * 为了提高性能使用注解@sun.misc.Contented来避免伪共享
	 * @sun.misc.Contented static final class Cell{
	 * 	volatile long value;//用来保存累加的值
	 *  Cell(long x){value = x;}
	 *  final boolean cas(long cmp, long val){//使用UNSAFE类的CAS来更新val值
	 *  	return UNSAFE.compareAndSwapLong(this,valueOffset,cmp,val);
	 *  }
	 *  private static final sun.misc.Unsafe.UNSAFE;
	 *  private static final long valueOffset;//value在Cell类中存储位置的偏移量
	 *  static{//用于获取偏移量
	 *  	try{
	 *  		UNSAFE = sun.misc.Unsafe.getUnsafe();
	 *  		Class<?> ak = Cell.class;
	 *  		valueOffset = UNSAFE.objectFieldOffset(ak.getDeclareField("value"));
	 *  	}catch(Exception e){
	 *  		throw new Error(e);
	 *  	}
	 *  }
	 * }
	 * 缓存行cache line：cpu的缓存系统中是以缓存行为单位来存储的，缓存行是2的整数幂个连续字节，一般为32～256字节。
	 * 最常见的缓存行大小是64字节。
	 * cache line是cpu和memory之间传输数据的最小单元。
	 * 但是cache又是cpu独有的，不同cpu的cache内容会存在不一致的问题。
	 * 为了解决这个问题，jdk1.8加入了@sun.misc.Contented。
	 * 
     *  LongAdder code:
     *  public void add(long x){
     *      Cell[] as, long b,v;int cm;Cell a;
     *      //如果以下2种情况之一则继续执行if内语句
     *      //1.cells数组不为null（不存在争用的时候cells数组一定为null，一旦对cells数组的cas操作失败，才会初始化cells）
     *      //2.cells为null且casBase执行成功则直接返回，casBase执行失败（第一次争用冲突，需要对cells进行初始化）进入if
     *      //casBase就是通过UNSAFE类的CAS设置变量base的值为base+x（要累加的值）
     *      //casBase执行成功的前提是无竞争，这时候cells数组还没有用到为null，类似于AtomicInteger的处理，cas做累加
     *      if((as = cells) != null || !casBase(b = base, b + x)){
     *          //当前线程要做cas累加操作的某个元素是否存在争用，cas失败则存在争用。false为存在争用，true不存在
     *          boolean uncontended = true;
     *          //如果进入if执行longAccumulate方法，有三种情况：
     *          //1.as == null cells数组没有初始化，直接进入if执行cell初始化
     *          //2.(m = as.length - 1) < 0 cells数组长度为0
     *          //1和2都表示cells没有初始化成功，初始化成功的cells数组长度为2
     *          //3.(a = as[getProbe() & m] == null 如果cells被初始化，且它的长度不为0，
     *          //通过getProbe()获取当前线程的threadLocalRandomProbe值，初始为0 ，
     *          //然后执行threadLocalRandomProbe&as.length-1,相当于m%cells.length,如果m%cells.length为null说明这个位置从来
     *          //没有线程做过累加，在这个位置新建一个Cell对象
     *          //4.!(uncontended = a.cas(v = a.value, v + x)) 尝试对cells[threadLocalRandomProbe%cells.length]位置的Cell
     *          //对象中的value值做累加操作，并返回操作结果，如果失败则进入if重新计算一个threadLocalRandomProbe
     *          if(as == null || (m = as.length - 1) < 0 ||
     *              (a = as[getProbe() & m]) == null ||
     *              !(uncontended = a.cas(v = a.value, v + x))){
     *                  longAccumulate(x, null, uncontented);
     *          }
     *      }
     *  }
     *  //x:要累加的值
     *  //wasUncontended:表示调用方法之前的add方法是否未发生竞争
     *  final void longAccumulate(long x, LongBinaryOperator fn, boolean wasUncontended){
     *      //hash是用来定位当前线程应该将值累加到cells数组哪个位置上的。
     *      //java thread中有一个变量@sun.misc.Contended("tlr") int threadLocalRandomProbe，就是用来定位cells数组的，初始为0
     *      //Striped64通过getProbe()获取threadLocalRandomProbe（在Thread类里面的偏移量）
     *      //线程对LongAdder的累加，在没有进入longAccumulate之前，threadLocalRandomProbe一直都是0，当争用产生后才会进入longAccumulate，
     *      //进入该方法的第一件事就是判断threadLocalRandomProbe是否是0，如果为0则将其设置为0x9e3779b9，并将未竞争标志wasUnceontended设为true
     *      int h;
     *      if((h = getProbe()) == 0){
     *          ThreadLocalRandom.current();
     *          h = getProbe();
     *          wasUncontended = true;
     *      }
     *      //CAS冲突标志，表示当前线程hash到的Cells数组的位置做cas累加时和其他线程发生了冲突，cas失败；
     *      //collide=true表示有冲突，collide=false表示无冲突
     *      boolean collide = false;
     *      for(;;){
     *          Cell[] as;Cell a;int n;long v;
     *          //这里有3个主分支，
     *          //1.主分支一：处理cells数组已经初始化的情况
     *          //2.主分支二：处理cells数组没有初始化或者长度为0的情况，初始化cell数组
     *          //3.主分支三：处理cells数组没有初始化，且其他线程正在执行对cells数组的初始化操作，尝试将累加值加到base上
     *          //表已经初始化
     *          if((as = cells) != null && (n = as.length) > 0){
     *              //内部分支一：
     *              //处理add条件3，如果hash到的位置为null，说明没有线程在这个位置设置过值，没有竞争可以直接使用。
     *              //用x值创建一个新的Cell，对cells数组使用cellsBusy锁，然后将这个对象放到cells[m%cells.length]位置上
     *              if((a = as[(n-1)&h]) == null){ //线程映射到槽是空的
     *                  if(cellsBusy == 0){//当前没有线程对cells数组做修改
     *                      Cell r = new Cell(x);
     *                      if(cellsBusy == 0 && casCellsBusy()){
     *                          boolean created = false;//标记Cell是否创建成功并放入到cells数组被hash的位置上
     *                          try{
     *                              Cell[] rs;int m,j;
     *                              //再次检查cells数组不为null，且长度不为0，且hash到的位置的Cell为null
     *                              if((rs = cells) != null &&
     *                                  (m = rs.length) > 0 &&
     *                                  rs[j = (m -1) & h] == null){
     *                                      rs[j] = r;//关联到槽
     *                                      created = true;
     *                              }
     *                          }finally{
     *                              cellsBusy = 0;
     *                          }
     *                         if(created){
     *                             break;//成功创建cell并关联到槽
     *                         }
     *                         continue;//槽现在不为空了
     *                      }
     *                  }
     *                  //代表cellsBusy=1，有的线程正在更改cells，产生了冲突（锁被占用，重试）
     *                  collide = false;
     *              }
     *              //内部分支二：
     *              //如果add方法中的条件4，cas设置cells[m%cells.length]位置的Cell对象中的value失败，说明发生竞争（槽被占用）；
     *              else if(!wasUncontended){//已经知道CAS失败
     *                  //设置未竞争标志为为true，继续执行，后面会算一个新的probe值，然后重新执行循环（重散列后继续）
     *                  wasUncontended = true;
     *              }
     *              //内部分支三：
     *              //新的线程参与争用情况，处理刚进入当前方法时threadLocalRandomProbe=0情况，
     *              //也就是当前线程第一次参与cell争用cas失败，这里尝试将x加到cells[m%cells.length]的value，成功退出
     *              //槽不为空且之前的cas没有失败，这里重新在此槽的cell上尝试更新
     *              else if(a.cas(v = a.value, (fn == null) ? v + x : fn.applyAsLong(v, x))){
     *                  break;
     *              }
     *              //内部分支四：
     *              //如果cells数组的长度已经达到了最大（大于等于cpu），或者当前cells已经扩容，
     *              //表达到上限后就不会尝试后面的分支，只会重新计算prob值，尝试其他槽
     *              else if(n >= NCPU || cells != as){
     *                  collide = false;
     *              }
     *              //内部分支五：
     *              //如果不存在冲突，则设置存在冲突；重新计算hash值并重试
     *              else if(!collide){
     *                  collide = true;
     *              }
     *              //内部分支六：
     *              //扩容cells数组，新参与cell争用的线程两次均失败，且符合扩容条件（成功获取到锁，则扩容）
     *              else if(cellsBusy == 0 && casCellsBusy()){
     *                  try{
     *                      if(cells == as){
     *                          Cell[] rs = new Cell[n << 1];
     *                          for(int i = 0; i < n; i++){
     *                              rs[i] = as[i];
     *                          }
     *                          cells = rs;
     *                      }
     *                  }finally{
     *                      cellsBusy = 0;
     *                  }
     *                  collide = false;
     *                  continue;//扩容后的表上重试
     *              }
     *              //没法获取锁，重散列，尝试其他槽
     *              h = advanceProbe(h);
     *          }else if(cellsBusy == 0 && cells == as && casCellsBusy()){//先尝试获取cellsBusy锁
     *              boolean init = false;
     *              try{    //初始化cells数组，初始容量为2，并将x值通过hash&1，放到0或第一个位置
     *                  if(cells == as){
     *                      Cell[] rs = new Cell[2];
     *                      rs[h & 1] = new Cell(x);
     *                      cells = rs;
     *                      init = true;
     *                  }
     *              }finally{
     *                  cellsBusy = 0;//解锁
     *              }
     *              if(init)//初始化成功，跳出循环
     *                  break;
     *          }else if(casBase(v = base, (fn == null) ? v + x : fn.applyAsLong(v, x))){
     *                 break;//如果以上操作都失败了，则尝试将值累加到base上
     *          }
     *      }
     *
     *  }
	 *
	 * **/
	//LongAdder|DoubleAdder:高并发下计数功能
	@Test
	public void testLongAdder() throws InterruptedException {
        final LongAdder la = new LongAdder();
	    Thread[] ts = new Thread[10];
	    for(int i = 0; i < ts.length; i++){
	        ts[i] = new Thread(la::increment);
//	        ts[i] = new Thread(() -> la.increment());
        }
	    for(Thread t : ts){
	        t.start();
        }
        for(Thread t : ts){
	        t.join();
        }
        assertEquals(10, la.intValue());
        la.add(5);
	    assertEquals(15, la.intValue());
        la.decrement();
	    assertEquals(14, la.intValue());
	    assertEquals(14, la.sum());
    }

    //LongAccumulator是LongAdder.accumulate的增强版，
    //可以传入一个function参数计算
    @Test
    public void testLongAccumulator() throws InterruptedException {
        LongAccumulator lal = new LongAccumulator(Long::max, Long.MIN_VALUE);
        Thread[] ts = new Thread[100];
        for(int i = 0; i < ts.length; i++){
            ts[i] = new Thread(() -> {
                Random random = new Random();
                long value = random.nextInt();
                lal.accumulate(value);//比较value和上一次的比较值Long::max，存储较大值
            });
            ts[i].start();
        }
        for(int i = 0; i < ts.length; i++){
            ts[i].join();
        }
        System.out.println(lal.intValue());
        lal.accumulate(Integer.MAX_VALUE);
        System.out.println(lal.intValue());//2147483647

        LongAccumulator lal2 = new LongAccumulator((p, v) -> p + v, -2);
        lal2.accumulate(10);
        System.out.println(lal2.get());//8 10+(-2)

        LongAccumulator lal3 = new LongAccumulator((x, y) -> (x << 1) + y, 0L);
        IntConsumer ic3 = i -> lal3.accumulate(i);

        //of():从一组对象创建一个stream对象
        //range():替换掉传统的for循环
//        IntStream.range(0, 5).forEach(System.out::print);//01234
        System.out.println();
        //peek:生成一个包含原stream所有元素的stream，同时提供一个消费函数，新stream每个元素被消费都会执行给定的消费函数
        List <Integer> result3 = IntStream.range(0, 10)
                .peek(ic3)
                .boxed()//装箱
                .collect(Collectors.toList());//转为list
        for (Integer re : result3) {
            System.out.print(re + " ");
        }

    }

}


