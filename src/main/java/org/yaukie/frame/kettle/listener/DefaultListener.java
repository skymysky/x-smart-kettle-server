package org.yaukie.frame.kettle.listener;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobListener;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransListener;
import org.pentaho.di.trans.performance.StepPerformanceSnapShot;
import org.springframework.stereotype.Component;
 import org.yaukie.frame.autocode.model.XMonitor;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.XJobStatus;
import org.yaukie.xtl.cons.XTransStatus;
import org.yaukie.xtl.exceptions.XtlExceptions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @Author: yuenbin
 * @Date :2020/11/18
 * @Time :15:48
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 自定义作业,转换监听器,便于实时检测作业,转换状态
 **/
@Component
@Slf4j
public class DefaultListener  extends  BaseListener implements JobListener, TransListener {

    private void doTransFinished(Trans trans)   {
        if(log.isDebugEnabled()){
            log.debug("转换-{}-运行结束,状态描述-{}...",trans.getObjectName(),trans.getStatus());
        }

        String monitorId ="" ;
        Result result=new Result();
        try {
            //加入作业监控
            XMonitor xMonitor = getTransMonitor(trans);
            if(null != xMonitor) {
                monitorId = xMonitor.getTargetId();
                String msg = trans.getStatus();
                if (!msg.contains("errors")) {
                    if (msg.equalsIgnoreCase("Stopped") || msg.equalsIgnoreCase("Finished")) {
                        //这里记录最后一个步骤的输出
                        Map<String, List<StepPerformanceSnapShot>> snapShots = trans.getStepPerformanceSnapShots();
                        if (null != snapShots && snapShots.size() > 0) {
                            //输出动态监控情况
                            Iterator it = snapShots.entrySet().iterator();
                            int count = 0;
                            while (it.hasNext()) {
                                count++;
                                if (count == snapShots.size()) {
                                    Map.Entry en = (Map.Entry) it.next();
                                    //步骤当前情况
                                    ArrayList snapShotList = (ArrayList) en.getValue();
                                    if (snapShotList != null && snapShotList.size() > 0) {
                                        //输出所有步骤情况
                                        StepPerformanceSnapShot snapShot = (StepPerformanceSnapShot) snapShotList.get(snapShotList.size() - 1);
                                        result.setNrLinesRead(snapShot.getTotalLinesRead());
                                        result.setNrLinesWritten(snapShot.getTotalLinesWritten());
                                        result.setNrLinesInput(snapShot.getTotalLinesInput());
                                        result.setNrLinesOutput(snapShot.getTotalLinesOutput());
                                        result.setNrLinesUpdated(snapShot.getTotalLinesUpdated());
                                        result.setNrLinesRejected(snapShot.getTotalLinesRejected());
                                        break;
                                    }
                                }

                            }
                            updMonitor("trans",KettleUtil.getTransStatus(trans).value(), monitorId, 1, 0, result, KettleUtil.getTransStatus(trans).description());
                        }
                    } else {
                        updMonitor("trans",XTransStatus.UNKNOWN.value(), monitorId, 0, 1, result, msg);
                    }
                }
            }
        }catch (XtlExceptions ex ){
            updMonitor("trans",XTransStatus.UNKNOWN.value(), monitorId,0,1,result,ex.getStackTraceAsString(ex));
        }
    }

    private void doTransStarted(Trans trans) {
        if(log.isDebugEnabled()){
            log.debug("转换-{}-启动完毕,状态描述-{}...",trans.getObjectName(),trans.getStatus());
        }
        String monitorId ="" ;
        try {
            //加入转换监控
            XMonitor xMonitor = getTransMonitor(trans);
            if(null != xMonitor){
                monitorId = xMonitor.getTargetId() ;
                 updMonitor("trans",KettleUtil.getTransStatus(trans).value(), monitorId,0,0,null,KettleUtil.getTransStatus(trans).description());
            }

        }catch (XtlExceptions ex ){
            updMonitor("trans",XTransStatus.UNKNOWN.value(), monitorId,0,0,null,ex.getStackTraceAsString(ex));
        }

    }


    @Override
    public void jobFinished(Job job) throws KettleException {
        if(log.isDebugEnabled()){
            log.debug("作业-{}-运行结束,状态描述-{}...",job.getJobname(),job.getStatus());
        }

        String monitorId ="" ;
        Result result=null;
        try {
            //加入作业监控
            XMonitor xMonitor = getJobMonitor(job) ;
            if(null != xMonitor){
                monitorId = xMonitor.getTargetId() ;

                String msg = job.getStatus() ;
                if(!msg.contains("errors")){
                    updMonitor("job",KettleUtil.getJobStatus(job).value(), monitorId,1,0,null,KettleUtil.getJobStatus(job).description());
                }else {
                    updMonitor("job",XJobStatus.UNKNOWN.value(), monitorId,0,1,null,msg);
                }
            }

        }catch (XtlExceptions ex ){
            updMonitor("job",XJobStatus.UNKNOWN.value(), monitorId,0,1,null,ex.getStackTraceAsString(ex));
        }

    }

    @Override
    public void jobStarted(Job job) throws KettleException {
        if(log.isDebugEnabled()){
            log.debug("作业-{}-启动完毕,状态描述-{}...",job.getJobname(),job.getStatus());
        }
        String monitorId ="" ;
        try {
            //加入作业监控
            XMonitor xMonitor = getJobMonitor(job);
            if(null != xMonitor){
                monitorId = xMonitor.getTargetId() ;
                String msg = job.getStatus() ;
                updMonitor("job",KettleUtil.getJobStatus(job).value(), monitorId,0,0,null,KettleUtil.getJobStatus(job).description());
            }

        }catch (XtlExceptions ex ){
            updMonitor("job",XJobStatus.UNKNOWN.value(), monitorId,0,0,null,ex.getStackTraceAsString(ex));
        }
    }

    @Override
    public void transStarted(Trans trans) throws KettleException {
        doTransStarted(trans);
    }

    @Override
    public void transActive(Trans trans) {
        if(log.isDebugEnabled()){
            log.debug("转换-{}-激活完毕,状态描述-{}...",trans.getObjectName(),trans.getStatus());
        }
        String monitorId ="" ;
        try {
            //加入转换监控
            XMonitor xMonitor = getTransMonitor(trans);
            if(null != xMonitor){
                monitorId = xMonitor.getTargetId() ;
                updMonitor("trans",KettleUtil.getTransStatus(trans).value(), monitorId,0,0,null,KettleUtil.getTransStatus(trans).description());
            }

        }catch (XtlExceptions ex ){
            updMonitor("trans",XTransStatus.UNKNOWN.value(), monitorId,0,0,null,ex.getStackTraceAsString(ex));
        }

    }

    @Override
    public void transFinished(Trans trans) throws KettleException {
        doTransFinished(trans);
    }
}
