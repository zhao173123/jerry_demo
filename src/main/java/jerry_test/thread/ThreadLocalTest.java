package jerry_test.thread;

/**
 * threadlocal：	本地线程变量，为每个线程创建了一个副本，
 * 那么每个线程可以访问自己内部的副本变量
 * 应用场景：解决数据库连接／session管理／
 * <p>
 * threadlocal用来提供线程内部的局部变量。
 * 这种变量在多线程环境下访问时能保证多个线程里的变量
 * 相对独立于其他线程内的变量。
 *
 * @author jerry
 */
public class ThreadLocalTest {

    ThreadLocal <Long> longLocal = new ThreadLocal <Long>() {
        @Override
        protected Long initialValue() {
            return Thread.currentThread().getId();
        }
    };

    ThreadLocal <String> stringLocal = new ThreadLocal <String>() {
        @Override
        public String initialValue() {
            return Thread.currentThread().getName();
        }
    };

    public void set() {
        longLocal.set(Thread.currentThread().getId());
        stringLocal.set(Thread.currentThread().getName());
    }

    public long getLong() {
        return longLocal.get();
    }

    public String getString() {
        return stringLocal.get();
    }

    public static void main(String[] args) throws Exception {
        final ThreadLocalTest test = new ThreadLocalTest();
//		test.set();
        System.out.println(test.getLong());//1
        System.out.println(test.getString());//main

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
//				test.set();
                System.out.println(test.getLong());//9
                System.out.println(test.getString());//Thread-0
            }
        });

        t1.start();
        t1.join();

        System.out.println(test.getLong());//1
        System.out.println(test.getString());//main

    }
}
