package book.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * @auth zhaochen@mgzf.com
 * @date 2018/8/8 上午10:42
 */
public class MultiThread{

    public static void main(String[] args){
        ThreadMXBean tmb = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = tmb.dumpAllThreads(false, false);
        for(ThreadInfo threadInfo : threadInfos){
            System.out.println("[" + threadInfo.getThreadId() + "]" + threadInfo.getThreadName());
        }
    }
}
