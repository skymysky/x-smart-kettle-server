package org.yaukie.frame.kettle.quartz;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaukie.builder.QuartzManager;
import org.yaukie.core.base.service.BaseService;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XJobService;
import org.yaukie.frame.autocode.service.api.XQuartzService;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.yaukie.xtl.cons.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: yuenbin
 * @Date :2020/11/16
 * @Time :10:03
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/
@Component
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class XQuartHandleService  {

    @Autowired
    private XQuartzService xQuartzService ;

    @Autowired
    private XJobService xJobService ;

    @Autowired
    private XTransService xTransService ;

    /**
     *  @Author: yuenbin
     *  @Date :2020/11/13
     * @Time :15:36
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  添加转换到定时器
     **/
    public void addJobToSche(XJob xJob){
        XQuartzExample xQuartzExample = new XQuartzExample() ;
        xQuartzExample.createCriteria()
                .andTargetIdEqualTo(xJob.getJobId())
                .andIsDelEqualTo("0");
        XQuartz xQuartz = xQuartzService.selectFirstExample(xQuartzExample);
        String cron="";
        if(null != xQuartz){
            cron = xQuartz.getQuartzCron() ;
            Map param = new HashMap();
            param.put("xJob", xJob);
            if(StringUtils.isNotBlank(cron)){
                QuartzManager.addJob(Constant.getQuartzBasic(xJob.getJobName(),
                        xJob.getJobPath()),
                        QuartzJobFactory.class, cron, param);
            }else
            {
                QuartzManager.addOnceJob(Constant.getQuartzBasic(xJob.getJobName(),
                        xJob.getJobPath()),
                        QuartzJobFactory.class,param);
            }

            log.debug("任务={}=加入定时器队列!",xJob.getJobName());
        }else {
            log.debug("任务={}=定时调度尚未开启,要想开启定时调度,请设置IS_DEL=1!",xJob.getJobName());
        }
    }

    /**
     *  @Author: yuenbin
     *  @Date :2020/11/13
     * @Time :16:21
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  从定时任务中移除一个转换定时任务
     **/
    public void removeJobFromSche(XJob xJob){
        Map param = Constant.getQuartzBasic(xJob.getJobName(), xJob.getJobPath());
        QuartzManager.removeJob(
                param.get("jobName")+"",
                param.get("jobGroupName")+"",
                param.get("triggerName")+"",
                param.get("triggerGroupName")+""
        );
    }


    /**
     *  @Author: yuenbin
     *  @Date :2020/11/13
     * @Time :15:36
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  添加转换到定时器
     **/
    public void addTransToSche(XTrans xTrans){
        XQuartzExample xQuartzExample = new XQuartzExample() ;
        xQuartzExample.createCriteria()
                .andTargetIdEqualTo(xTrans.getTransId())
                .andIsDelEqualTo("0");
        XQuartz xQuartz = xQuartzService.selectFirstExample(xQuartzExample);
        String cron="";
        if(null != xQuartz){
            cron = xQuartz.getQuartzCron() ;
            Map param = new HashMap();
            param.put("xTrans", xTrans);
            if(StringUtils.isNotBlank(cron)){
                QuartzManager.addJob(Constant.getQuartzBasic(xTrans.getTransName(),
                        xTrans.getTransPath()),
                        QuartzTransFactory.class, cron, param);
            }else
            {
                QuartzManager.addOnceJob(Constant.getQuartzBasic(xTrans.getTransName(),
                        xTrans.getTransPath()),
                        QuartzTransFactory.class,param);
            }
            log.debug("任务={}=加入定时器队列!",xTrans.getTransName());
        }else {
                log.debug("任务={}=定时调度尚未开启,要想开启定时调度,请设置IS_DEL=1!",xTrans.getTransName());
        }
    }

    /**
     *  @Author: yuenbin
     *  @Date :2020/11/13
     * @Time :16:21
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  从定时任务中移除一个转换定时任务
     **/
    public void removeTransFromSche(XTrans xTrans){
        Map param = Constant.getQuartzBasic(xTrans.getTransName(), xTrans.getTransPath());
        QuartzManager.removeJob(
                param.get("jobName")+"",
                param.get("jobGroupName")+"",
                param.get("triggerName")+"",
                param.get("triggerGroupName")+""
        );
    }

    /**
     *  @Author: yuenbin
     *  @Date :2020/11/17
     * @Time :9:53
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  更新定时任务时间规则
    **/
    public void modifyScheTime(XQuartz xQuartz ){
        String targetId = xQuartz.getTargetId() ;
        String quartzType =xQuartz.getQuartzType() ;
        String cron = xQuartz.getQuartzCron();
        String name="";
        String path="";
        if(quartzType.equals("job")){
            XJobExample xJobExample =new XJobExample();
            xJobExample.createCriteria().andJobIdEqualTo(targetId);
            XJob xJob = xJobService.selectFirstExample(xJobExample) ;
            name = xJob.getJobName();
            path = xJob.getJobPath() ;
        }else if(quartzType.equals("trans")){
            XTransExample xTransExample = new XTransExample();
            xTransExample.createCriteria().andTransIdEqualTo(targetId);
            XTrans xTrans = xTransService.selectFirstExample(xTransExample);
            name = xTrans.getTransName();
            path = xTrans.getTransPath();
        }

        Map param = Constant.getQuartzBasic(name, path);
        QuartzManager.modifyJobTime(param, cron);
    }


}
