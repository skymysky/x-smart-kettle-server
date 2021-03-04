package org.yaukie.frame.kettle.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.yaukie.core.BaseJob;


/**
 * @Author: yuenbin
 * @Date :2020/11/13
 * @Time :9:53
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 作业定时调度,用户适配springboot启动加载
 **/
@Slf4j
public abstract  class DefaultBaseJob extends BaseJob  {

    public DefaultBaseJob(){

    }

    @Override
    public   void execute()  {
        JobExecutionContext context =null;
        try {
             context = super.getContext() ;
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
            MutablePropertyValues pvs = new MutablePropertyValues();
            pvs.addPropertyValues(context.getScheduler().getContext());
            pvs.addPropertyValues(context.getMergedJobDataMap());
            bw.setPropertyValues(pvs, true);
        } catch (SchedulerException  ex) {
            log.error("出现异常!", ex);
        }

        this.executeInternal(context);

    }

    protected abstract void executeInternal(JobExecutionContext var1) ;

}
