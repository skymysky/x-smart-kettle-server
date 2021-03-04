package org.yaukie.frame.kettle.core;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yaukie.frame.autocode.model.XJob;
import org.yaukie.frame.autocode.model.XMonitor;
import org.yaukie.frame.autocode.model.XMonitorExample;
import org.yaukie.frame.autocode.service.api.XMonitorService;
import org.yaukie.frame.kettle.service.JobService;
import org.yaukie.frame.pool.StandardPoolExecutor;
import org.yaukie.frame.pool.StandardThreadFactory;
import org.yaukie.xtl.cons.XJobStatus;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @Author: yuenbin
 * @Date :2020/11/9
 * @Time :18:49
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 作业执行提交入口
 **/
@Component
@Slf4j
public class XJobSubmit {

    @Autowired
    private JobService jobService;

    @Autowired
    private StandardPoolExecutor executor ;

    @Autowired
    private XMonitorService xMonitorService ;

    /**通过引用来记录线程池工厂中线程*/
    private Set<Thread> threadsContainer = new HashSet<>();

    private static Map<String,Thread> activeMap = new ConcurrentHashMap();


    public synchronized void submit(XJob xJob ){

        StandardThreadFactory threadFactory = new StandardThreadFactory("kettleThreadPool",threadsContainer);
        threadFactory.setJobName(xJob.getJobName());
        executor.setThreadFactory(threadFactory);
        executor.allowCoreThreadTimeOut(true);
        String monitorId = xJob.getJobId();
            executor.execute(()->{
                String status = "" ;
                String des="";
                try {
                    jobService.startJob(xJob);
                } catch (KettleException ex) {
                    status = XJobStatus.UNKNOWN.value();
                    StringWriter out = new StringWriter();
                     ex.printStackTrace(new PrintWriter(out));
                     des = out.toString().substring(0,400 );
                    XMonitorExample xMonitorExample = new XMonitorExample() ;
                    xMonitorExample.createCriteria().andTargetIdEqualTo(monitorId) ;

                    XMonitor xMonitor =  xMonitorService.selectFirstExample(xMonitorExample) ;
                    if(xMonitor !=null ){
                        String failCount = xMonitor.getFailCount();
                        if(failCount.equals("null") || failCount.equals("")){
                            failCount="0" ;
                        }
                        xMonitor.setTargetStatus(status);
                        xMonitor.setDescription(des);
                        xMonitor.setFailCount((Integer.parseInt(failCount)+1)+"");
                        xMonitorService.updateByExampleSelective(xMonitor,xMonitorExample);
                    }
                }
            });



    }

    public boolean isPoolActive(){
        return executor.getCurrentTaskCounts() > 0 ;
    }


    public boolean isTerminated(){
        return executor.isTerminated();
    }

    /**
     *  获取当前队列中正在跑的任务数
     * @return
     */
    public int getCurrentTaskCounts(){
        int taskCounts = executor.getCurrentTaskCounts() ;
        return taskCounts;
    }


    /**
     *  @Author: yuenbin
     *  @Date :2020/11/10
     * @Time :15:03
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  看看队列中是否有在跑的作业任务
    **/
    public boolean isExistTask(XJob xJob){
        AtomicBoolean isExist = new AtomicBoolean(false);
        //进来之前,先清理一下activeMap中已执行完的线程
        if(activeMap.size() > 0 ){
            activeMap.keySet().forEach(key ->{
                Thread tmp = activeMap.get(key);
                log.debug("====={}",key);
                if(!tmp.isAlive()){
                    activeMap.remove(key);
                }
            });
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //过滤不存活的线程
        if(!threadsContainer.isEmpty()){
                threadsContainer.stream().filter(Thread::isAlive)
                        .forEach(thread -> {
                            String threadName = thread.getName() ;
                            log.debug("当前运行的任务：{} \t\n",threadName);
                            if(activeMap.containsKey(threadName)){
                                isExist.set(true);
                            }
                            activeMap.put(threadName, thread);
                        });
        }

        return  isExist.get();
    }

}
