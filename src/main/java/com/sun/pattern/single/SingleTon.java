package com.sun.pattern.single;

/**
 * 现在来看比较好的单例模式就是
 * getInstanceOk、getInstanceOk2、getInstanceOk3
 * 但是getInstanceOk是饿汉式,
 * getInstanceOk2：适用于对静态字段、实例延迟初始化
 * getInstanceOk3：适用于静态字段延迟初始化
 * 比较倾向于getInstanceOk3的方式，通过类锁控制
 */
public class SingleTon{

    private SingleTon(){
    }

    private static SingleTon instance = null;

    /**
     * 非线程安全，延迟加载
     *
     * @return
     */
    public static SingleTon getInstance0(){
        if(instance == null){
            instance = new SingleTon();
        }
        return instance;
    }

    /**
     * 双重检查锁-延迟加载，懒汉式 但是java是无序写入，不会新建多个对象，但是执行2的时候instance被赋予了地址，但是singleTon1对象
     * 可能还没有构建完成，这时候如果有线程访问了1，此时判断instance不为空，但是方法返回的是一个不完整的对象引用，会引发错误
     *
     * @return
     */
    public static SingleTon getInstance1(){
        if(instance == null){// 1
            synchronized(SingleTon.class){
                if(instance == null){
                    instance = new SingleTon();// 2
                }
            }
        }
        return instance;
    }

    // 以下为正确的构建线程安全单列模式，饿汉式
    private static SingleTon instanceOk = new SingleTon();

    public static SingleTon getInstanceOk(){
        return instanceOk;
    }

    // Double check locking
    private volatile static SingleTon instance2 = null;

    public static SingleTon getInstancek2(){
        if(instance2 == null){
            synchronized(SingleTon.class){
                if(instance2 == null){
                    instance2 = new SingleTon();
                }
            }
        }
        return instance2;
    }

    //静态内置类实现
    //静态内部类虽然保证了多线程下并发的安全性，但是在遇到序列化对象时，默认的方式运行得到的结果就是多列的
    private static class SingleTonHandler{
        private static SingleTon instance = new SingleTon();
    }

    public static SingleTon getInstanceOk3(){
        return SingleTonHandler.instance;
    }
}
