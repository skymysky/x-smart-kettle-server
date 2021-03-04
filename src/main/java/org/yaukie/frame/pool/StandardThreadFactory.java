package org.yaukie.frame.pool;

import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yuenbin
 * @Date :2020/10/28
 * @Time :9:04
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 构建线程管理工厂
 **/
public class StandardThreadFactory implements ThreadFactory {

    private String namePrefix ;

    private String jobName ;

    private ThreadGroup group ;

    //存储当前工作线程(任务)的数量
    private AtomicInteger threadCounts =new AtomicInteger(1);

    //存储当前线程池中线程的数量
    private AtomicInteger poolCounts = new AtomicInteger(1);

   private final Set<Thread> threadsContainer;

    public StandardThreadFactory(String namePrefix,Set<Thread> threadsContainer){
        this.namePrefix=namePrefix+"-"+poolCounts.getAndIncrement() ;
        SecurityManager securityManager  = System.getSecurityManager() ;
        group = securityManager ==null?Thread.currentThread().getThreadGroup():securityManager.getThreadGroup();
        this.threadsContainer = threadsContainer ;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(group,r,namePrefix+"-thread-"+threadCounts.getAndIncrement()+"-"+jobName) ;
        if(t.isDaemon()){
            t.setDaemon(false);
        }
        if(t.getPriority() != Thread.NORM_PRIORITY){
            t.setPriority(Thread.NORM_PRIORITY);
        }
        threadsContainer.add(t) ;
        return t;
    }

    public void setJobName(String jobName){
        this.jobName = jobName ;
    }

    public String getJobName(){
        return this.jobName ;
    }

}
