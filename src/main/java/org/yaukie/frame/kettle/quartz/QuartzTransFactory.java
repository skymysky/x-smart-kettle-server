package org.yaukie.frame.kettle.quartz;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.yaukie.core.util.SpringContextUtil;
import org.yaukie.frame.autocode.model.XTrans;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 *  @Author: yuenbin
 *  @Date :2020/11/16
 * @Time :9:45
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:  定义转换定时任务具体实现
**/
@Component
@Slf4j
public class QuartzTransFactory extends DefaultBaseJob {

    @Override
    protected void executeInternal(JobExecutionContext context) {
        DefaultExeXTrans defaultExeXTrans = SpringContextUtil.getBean("defaultExeXTrans", DefaultExeXTrans.class);
         XTrans xTrans = (XTrans) context.getMergedJobDataMap().get("xTrans");
        try {
            defaultExeXTrans.doExeTrans(xTrans);
        } catch (Exception e) {
            StringWriter str = new StringWriter();
            e.printStackTrace(new PrintWriter(str));
            log.error("定时任务执行转换任务失败,原因为: {}",str.toString().substring(0,800) );
        }
    }
}
