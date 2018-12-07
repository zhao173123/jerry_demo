package com.sun.lock;

/**
 * 乐观锁
 * 
 * 场景：有一个对象value，需要被2个线程调用，由于是共享数据，存在脏数据问题 
 * <p>悲观锁可以利用synchronized实现</p> 
 * <p>现在使用乐观锁来解决脏数据问题</p>
 * 
 * 从结果看A修改了value值,B却没有检查出来,利用错误的value值进行了操作.
 * 乐观锁是有一定的不安全性的,B在检查版本的时候A还没有修改,在B检查完版本后更新数据前(例子中的输出语句),A更改了value值,
 * 这时B执行更新数据(例子中的输出语句)就发生了与现存value不一致的现象.
 * 
 * @author jerry
 *
 */
public class OptimistickLock {

	// 多线程共享对象
	public static int value = 0;

	/**
	 * A线程要执行的方法
	 * 
	 * @param AValue
	 * @param i
	 * @throws InterruptedException
	 */
	public static void invoke(int AValue, String i) throws InterruptedException {
		Thread.sleep(1000L);
		if (AValue != value) {// 判断value版本
			System.out.println(AValue + ":" + value + "A版本不一致，不执行！");
			value--;
		} else {
			AValue++;// 对数据操作
			value = AValue;// 对数据操作
			System.out.println(i + ":" + value);
		}
	}

	/**
	 * B线程要执行的方法
	 * 
	 * @param BValue
	 * @param i
	 * @throws InterruptedException
	 */
	public static void invoke2(int BValue, String i) throws InterruptedException {
		Thread.sleep(1000L);
		if (BValue != value) {
			System.out.println(BValue + ":" + value + ",B版本不一致，不执行！");
		} else {
			System.out.println("B利用value运算，value=" + BValue);
		}
	}

	// 测试，期待结果：B执行的时候value总是当前最新的
	public static void main(String[] args) {
		new Thread(new Runnable() {// A线程
			@Override
			public void run() {
				try {
					for (int i = 0; i < 3; i++) {
						int AValue = OptimistickLock.value;
						OptimistickLock.invoke(AValue, "A");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					for (int i = 0; i < 3; i++) {
						int BValue = OptimistickLock.value;
						OptimistickLock.invoke2(BValue, "B");
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();// B线程
	}

}
