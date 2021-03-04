package org.yaukie.frame.pool;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.RejectedExecutionException;

/**
 * @Author: yuenbin
 * @Date :2020/10/27
 * @Time :19:21
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: LinkedTransferQueue是一个由链表结构组成的无界阻塞TransferQueue队列。
 * 相对于其他阻塞队列，
 * LinkedTransferQueue多了tryTransfer和transfer方法。
 * 性能比Linked高,存储比SynchronousQueue多
 **/
public class StandardTaskQueue extends LinkedTransferQueue<Runnable> {

    private StandardPoolExecutor executor ;

    public StandardTaskQueue(){
        super();
    }

    public void setExecutor(StandardPoolExecutor executor){
        this.executor = executor;
    }

    /**
     *  往队列中添加元素
     * @param runnable
     * @return
     */
    public boolean force(Runnable runnable) {
        if(executor.isShutdown()){
            throw new RejectedExecutionException("Executor not running,can't force offer a coomand into a queue!~");
        }
        return offer(runnable);
    }

    /**
     *  定义队列放置任务策略
     *  往队列中添加任务
     *  1 如果当前线程池中的线程数poolSize 小于等于CoreSize,创建线程,
     *  2 如果当前线程池中的线程数poolSize 大于CoreSize,但小于MaxPoolSize
     * 3 如果当前线程池中的线程数poolSize 大于MaxPoolSize,但运行的任务数小于MaxQueueSize,提交任务到队列中
     * 4 如果当前线程池中的线程数poolSize 大于MaxPoolSize,并且当前运行的任务数等于MaxQueueSize,则拒绝入队列
     * @param runnable
     * @return
     */
    @Override
    public boolean offer(Runnable runnable){
        // 拿到当前线程池中线程总数
        int poolSize = executor.getPoolSize() ;

        // 如果还没到最大线程数,并且队列没满,则提交任务
        if(poolSize >=executor.getMaximumPoolSize() && executor.getCurrentTaskCounts() < executor.getMaxQueueSize()){
            return super.offer(runnable) ;
        }

        // 线程数比任务多,则提交任务
        if(executor.getCurrentTaskCounts() <= poolSize){
            return super.offer(runnable) ;
        }

        //线程没达到上限,创建线程
        if(poolSize < executor.getMaximumPoolSize()){
            return false ;
        }

        return super.offer(runnable) ;

    }

}
