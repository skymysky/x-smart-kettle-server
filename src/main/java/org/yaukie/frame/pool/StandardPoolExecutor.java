package org.yaukie.frame.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: yuenbin
 * @Date :2020/10/27
 * @Time :19:10
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:  构建多线程处理器,新增任务放入线程池处理
 **/
@Slf4j
public class StandardPoolExecutor  extends ThreadPoolExecutor {

    /**
     * 当前任务中,正在跑的任务数
     */
    private AtomicInteger currentTaskCounts  ;
    /**
     *  队列中可以支持的最大任务数
     */
    private int  maxQueueSize ;

    private StandardTaskQueue queue ;


    public StandardPoolExecutor(
            int corePoolSize, //核心线程数
            int maxPoolSize, //最大可支持的线程数
            long keepAliveTime,
            TimeUnit unit,
            int queueCapacity, //队列当前的容量
            ThreadFactory threadFactory ,
            RejectedExecutionHandler handler) {
        super(corePoolSize,maxPoolSize,keepAliveTime,unit,new StandardTaskQueue(),threadFactory,handler);
           queue = (StandardTaskQueue) this.getQueue();
            queue.setExecutor(this);
        // linkedTransferQueue能够支持的最大线程数=队列容量+maxPoolSize
        //linkedTransferQueue是无界阻塞队列
        maxQueueSize = queueCapacity + maxPoolSize ;
        currentTaskCounts = new AtomicInteger(0);
    }


    @Override
    public void execute(Runnable command) {
        if(currentTaskCounts.incrementAndGet() > maxQueueSize){
            currentTaskCounts.decrementAndGet();
            this.getRejectedExecutionHandler().rejectedExecution(command, this);
        }

        try {
            super.execute(command);
        }catch (RejectedExecutionException e){
            if(!queue.force(command)){
                currentTaskCounts.decrementAndGet();
               this.getRejectedExecutionHandler().rejectedExecution(command,this );
            }
        }

    }

    /**
     *  队列能支持的最大任务数
     * @return
     */
    public int getMaxQueueSize(){
        return this.maxQueueSize ;
    }

    /**
     *  当前正在运行的任务数
     * @return
     */
    public int getCurrentTaskCounts(){
        return currentTaskCounts.get() ;
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {
            currentTaskCounts.decrementAndGet() ;
    }



}
