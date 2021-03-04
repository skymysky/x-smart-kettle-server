package org.yaukie.frame.kettle.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Result;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.springframework.beans.factory.annotation.Autowired;
 import org.yaukie.core.util.GenCodeUtil;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XJobService;
import org.yaukie.frame.autocode.service.api.XMonitorService;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.Constant;
import org.yaukie.xtl.cons.XJobStatus;


import java.util.Date;

/**
 * @Author: yuenbin
 * @Date :2020/11/9
 * @Time :14:43
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
 **/
@Slf4j
public abstract class BaseListener {

    @Autowired
    private XJobService xJobService ;

    @Autowired
    private XTransService xTransService ;

    @Autowired
    private XMonitorService xMonitorService ;

    /**
     *  @Author: yuenbin
     *  @Date :2020/11/9
     * @Time :14:51
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  获取一个作业监控
    **/
    protected XMonitor getJobMonitor(Job job ){
        XMonitorExample xMonitorExample = new XMonitorExample() ;
        xMonitorExample.createCriteria()
                .andTargetIdEqualTo(job.getVariable(Constant.VARIABLE_JOB_MONITOR_ID))
                .andMonitorTypeEqualTo("job");
        XJobExample xJobExample = new XJobExample() ;
        xJobExample.createCriteria().andJobIdEqualTo(job.getVariable(Constant.VARIABLE_JOB_MONITOR_ID));
        XJob xJob = xJobService.selectFirstExample(xJobExample);
        XMonitor xMonitor =null ;
        String isMonitorEnabled = xJob.getIsMonitorEnabled() ;

        if(StringUtils.isNotEmpty(isMonitorEnabled) && isMonitorEnabled.equals("1")){
            xMonitor = xMonitorService.selectFirstExample(xMonitorExample);
            if(null == xMonitor){
                xMonitor = new XMonitor() ;
                xMonitor.setMonitorId(GenCodeUtil.nextId());
                xMonitor.setTargetId(job.getVariable(Constant.VARIABLE_JOB_MONITOR_ID));
                xMonitor.setFailCount(0+"");
                xMonitor.setSuccessCount(0+"");
                xMonitor.setMonitorType("job");
                xMonitor.setTargetStatus(KettleUtil.getJobStatus(job).value());
                xMonitor.setDescription(KettleUtil.getJobStatus(job).description());
                int affect =   xMonitorService.insertSelective(xMonitor);
                if(affect > 0 ){
                    log.debug("新增作业监控成功!");
                }
            }
        }

        return xMonitor ;
    }



    /**
     *  @Author: yuenbin
     *  @Date :2020/11/9
     * @Time :14:54
     * @Motto: It is better to be clear than to be clever !
     * @Destrib: 获取一个转换监控
    **/
    protected XMonitor getTransMonitor(Trans trans ){
        XMonitorExample xMonitorExample = new XMonitorExample() ;
        xMonitorExample.createCriteria()
                .andTargetIdEqualTo(trans.getVariable(Constant.VARIABLE_TRANS_MONITOR_ID))
                .andMonitorTypeEqualTo("trans");
        XTransExample xTransExample = new XTransExample() ;
        xTransExample.createCriteria().andTransIdEqualTo(trans.getVariable(Constant.VARIABLE_TRANS_MONITOR_ID)) ;
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        XMonitor xMonitor =null ;
        String isMonitorEnabled = xTrans.getIsMonitorEnabled() ;
        if(StringUtils.isNotEmpty(isMonitorEnabled) && isMonitorEnabled.equals("1")){
              xMonitor = xMonitorService.selectFirstExample(xMonitorExample);
            if(null == xMonitor){
                xMonitor = new XMonitor() ;
                xMonitor.setMonitorId(GenCodeUtil.nextId());
                xMonitor.setTargetId(trans.getVariable(Constant.VARIABLE_TRANS_MONITOR_ID));
                xMonitor.setFailCount(0+"");
                xMonitor.setSuccessCount(0+"");
                xMonitor.setMonitorType("trans");
                xMonitor.setTargetStatus(KettleUtil.getTransStatus(trans).value());
                xMonitor.setDescription(KettleUtil.getTransStatus(trans).description());
                int affect =   xMonitorService.insertSelective(xMonitor);
                if(affect > 0 ){
                    log.debug("新增转换监控成功!");
                }
            }
        }

        return xMonitor ;
    }

    /**
     *  @Author: yuenbin
     *  @Date :2020/11/9
     * @Time :14:54
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  更新作业或转换监控
    **/
    protected void updMonitor(String type ,String status , String monitorId , int successCount, int failCount,Result result,String msg){
        XMonitorExample xMonitorExample = new XMonitorExample() ;
        xMonitorExample.createCriteria()
                .andTargetIdEqualTo(monitorId)
                .andMonitorTypeEqualTo(type);
        XMonitor  xMonitor = xMonitorService.selectFirstExample(xMonitorExample) ;
        if(null != xMonitor) {
            String success = xMonitor.getSuccessCount();
            if (!StringUtils.isEmpty(success) && !"null".equalsIgnoreCase(success)) {
                successCount += Integer.parseInt(success);
                if (successCount >= 1) {
                    xMonitor.setSuccessCount(successCount + "");
                }
            }
            String fail = xMonitor.getFailCount();
            if (!StringUtils.isEmpty(fail) && !"null".equalsIgnoreCase(fail)) {
                failCount += Integer.parseInt(fail);
                if (failCount >= 1) {
                    xMonitor.setFailCount(failCount + "");
                }
            }
            xMonitor.setTargetStatus(status);
            xMonitor.setDescription(msg);
            if (null != result) {
                xMonitor.setTargetLinesRead(result.getNrLinesRead() + "");
                xMonitor.setTargetLinesWritten(result.getNrLinesWritten() + "");
                xMonitor.setTargetLinesInput(result.getNrLinesInput() + "");
                xMonitor.setTargetLinesOutput(result.getNrLinesOutput() + "");
                xMonitor.setTargetLinesUpdated(result.getNrLinesUpdated() + "");
                xMonitor.setTargetLinesRejected(result.getNrLinesRejected() + "");
            }
            xMonitor.setUpdateTime(new Date());
            int affect = xMonitorService.updateByExampleSelective(xMonitor, xMonitorExample);
            if (affect > 0) {
                log.debug("监控更新成功!");
            }
        }

    }

}
