package org.yaukie.frame.kettle.quartz;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleException;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.yaukie.core.util.SpringContextUtil;
import org.yaukie.frame.autocode.model.XJob;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  @Author: yuenbin
 *  @Date :2020/11/16
 * @Time :9:45
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:  定义作业定时任务具体实现
**/
@Component
@Slf4j
public class QuartzJobFactory extends DefaultBaseJob {

    @Override
    protected void executeInternal(JobExecutionContext context)  {

        DefaultExeXJob defaultExeXJob = SpringContextUtil.getBean("defaultExeXJob", DefaultExeXJob.class);
         XJob xJob = (XJob) context.getMergedJobDataMap().get("xJob");
        try {
            defaultExeXJob.doExeJob(xJob);
        } catch (KettleException e) {
            StringWriter str = new StringWriter();
            e.printStackTrace(new PrintWriter(str));
            log.error("定时任务执行作业任务失败,原因为: {}",str.toString().substring(0,800) );
        }
    }
}
