package org.yaukie.frame.kettle.service;

import com.atomikos.util.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.parameters.UnknownParamException;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XLogService;
import org.yaukie.frame.autocode.service.api.XParamsService;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.yaukie.frame.kettle.listener.DefaultListener;
import org.yaukie.frame.kettle.listener.XLogListener;
import org.yaukie.frame.pool.StandardPoolExecutor;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.Constant;
import org.yaukie.xtl.cons.XJobStatus;
import org.yaukie.xtl.exceptions.XtlExceptions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: yuenbin
 * @Date :2020/11/3
 * @Time :19:59
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 作业执行工具类
 * 作业开启
 * 作业停止
 **/
@Component
@Slf4j
public class JobService {
    /**
     * 保存正在运行的作业
     */
    private static Map<String, Job> jobMap = new ConcurrentHashMap();

    private static Repository repository;


    @Autowired
    private XRepositoryService xRepositoryService;

    @Autowired
    private DefaultListener defaultListener;

    @Autowired
    private XLogService xLogService;

    @Autowired
    private XParamsService xParamsService ;

    @Autowired
    private StandardPoolExecutor executor;

    @Value("${kettle.log.file.path}")
    private String logFilePath;

    /**
     * @Author: yuenbin
     * @Date :2020/11/10
     * @Time :18:09
     * @Motto: It is better to be clear than to be clever !
     * @Destrib: 启动一个作业
     **/
    public void startJob(XJob xJob) throws XtlExceptions, KettleException {
        String jobType = xJob.getJobType();
        if (StringUtils.isEmpty(jobType)) {
            try {
                throw new XtlExceptions("请指定作业类型!");
            } catch (XtlExceptions e) {
            }
        }

        /**作业路径,文件绝对路径,或 资源库存储路径*/
        String jobPath = xJob.getJobPath();
        /**日志级别**/
        String logLevel = xJob.getJobLogLevel();
        /**如果是文件库中的作业*/
        if (jobType.equalsIgnoreCase(Constant.JOB_FILE_TYPE)) {
            doRunFileJob(xJob);
            /**如果是资源库中的作业*/
        } else if (jobType.equalsIgnoreCase(Constant.JOB_REPO_TYPE)) {
            doRunRepoJob(xJob);
        }

    }

    /**
     * @Author: yuenbin
     * @Date :2020/11/10
     * @Time :18:08
     * @Motto: It is better to be clear than to be clever !
     * @Destrib: 从资源库获取作业, 并执行start
     **/
    private void doRunRepoJob(XJob xJob) throws XtlExceptions, KettleException {
        String repositoryId = xJob.getJobRepositoryId();
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        if (xRepository == null) {
            throw new XtlExceptions("请定义资源库!");
        }

        repository = KettleUtil.conByNative(xRepository.getRepoId(), xRepository.getRepoName(),
                xRepository.getRepoName(), xRepository.getRepoType(),
                xRepository.getDbHost(), xRepository.getDbPort(), xRepository.getDbName(),
                xRepository.getDbUsername(), xRepository.getDbPassword(), xRepository.getRepoUsername(),
                xRepository.getRepoPassword());
        JobMeta jobMeta = KettleUtil.loadJob(xJob.getJobName(), xJob.getJobPath(), repository);
        XParamsExample xParamsExample = new XParamsExample() ;
        xParamsExample.createCriteria().andTargetTypeEqualTo("job")
                .andTargetIdEqualTo(xJob.getJobId());
        List<XParams> xParams = xParamsService.selectByExample(xParamsExample);
        if(!CollectionUtils.isEmpty(xParams)){
            xParams.forEach(item -> {
                try {
                    jobMeta.setParameterValue(item.getObjCode()+"", item.getObjVal()+"");
                } catch (UnknownParamException e) {
                    throw new RuntimeException(e) ;
                }
            });
        }

        Job job = new Job(repository, jobMeta);
        job.setDaemon(true);
        job.setVariable(Constant.VARIABLE_JOB_MONITOR_ID, xJob.getJobId());
        job.addJobListener(defaultListener);
        /**设置默认日志级别为DEBUG*/
        job.setLogLevel(LogLevel.DEBUG);
        if (StringUtils.isNotEmpty(xJob.getJobLogLevel())) {
            job.setLogLevel(Constant.logger(xJob.getJobLogLevel()));
        }
        String exception = null;
        /**默认运行成功*/
        String recordStatus = XJobStatus.SUCCESS.value();
        String stopTime = "";
        String logText = "";
        try {
            String logId = XLogListener.addLogListener(logFilePath, job);
            job.start();

            jobMap.put(xJob.getJobId(), job);
            job.waitUntilFinished();
            if (job.isFinished()) {
                recordStatus = KettleUtil.getJobStatus(job).value();
                stopTime = DateHelper.format(new Date());
                XLog xLog = new XLog();
                xLog.setTargetResult(recordStatus);
                xLog.setStopTime(stopTime);
                XLogExample xLogExample = new XLogExample();
                xLogExample.createCriteria().andLogIdEqualTo(logId);
                xLogService.updateByExampleSelective(xLog, xLogExample);
            }
        } catch (Exception e) {
            StringWriter str = new StringWriter();
            e.printStackTrace(new PrintWriter(str));
            log.error("任务执行失败,原因为: {}", str.toString().substring(0, 800));
        }

    }

    /**
     * @Author: yuenbin
     * @Date :2020/11/10
     * @Time :18:08
     * @Motto: It is better to be clear than to be clever !
     * @Destrib: 从文件获取作业, 并执行start
     **/
    private void doRunFileJob(XJob xJob) throws XtlExceptions, KettleException {
        String repositoryId = xJob.getJobRepositoryId();
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        if (xRepository == null) {
            throw new XtlExceptions("请定义资源库!");
        }
        repository = KettleUtil.conFileRep(xRepository.getRepoId(), xRepository.getRepoName(),
                xRepository.getBaseDir());
        JobMeta jobMeta = KettleUtil.loadJob(xJob.getJobName(), xJob.getJobPath(), repository);
        org.pentaho.di.job.Job job = new org.pentaho.di.job.Job(null, jobMeta);
        job.setDaemon(true);
        job.setVariable(Constant.VARIABLE_JOB_MONITOR_ID, xJob.getJobId());
        job.addJobListener(defaultListener);
        job.setLogLevel(LogLevel.DEBUG);
        if (StringUtils.isNotEmpty(xJob.getJobLogLevel())) {
            job.setLogLevel(Constant.logger(xJob.getJobLogLevel()));
        }
        String recordStatus = XJobStatus.SUCCESS.value();
        String stopTime = null;
        try {
            job.run();
            /**添加日志监听*/
            String logId = XLogListener.addLogListener(logFilePath, job);
            jobMap.put(xJob.getJobId(), job);
            job.waitUntilFinished();
            if (job.isFinished()) {
                recordStatus = KettleUtil.getJobStatus(job).value();
                stopTime = DateHelper.format(new Date());
                XLog xLog = new XLog();
                xLog.setTargetResult(recordStatus);
                xLog.setStopTime(stopTime);
                XLogExample xLogExample = new XLogExample();
                xLogExample.createCriteria().andLogIdEqualTo(logId);
                xLogService.updateByExampleSelective(xLog, xLogExample);
            }
        } catch (Exception e) {
            StringWriter str = new StringWriter();
            e.printStackTrace(new PrintWriter(str));
            log.error("任务执行失败,原因为: {}", str.toString().substring(0, 800));
        }
    }

    /**
     * @Author: yuenbin
     * @Date :2020/11/24
     * @Time :10:04
     * @Motto: It is better to be clear than to be clever !
     * @Destrib: 强行停止一个作业
     **/
    public XJobStatus doStopJob(XJob xJob) {
        String repoId = xJob.getJobRepositoryId();
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repoId + "");
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        try {

            if (xRepository == null) {
                throw new XtlExceptions("请定义资源库!");
            }
            repository = KettleUtil.holder.get(repoId);
            if (null == repository) {
                throw new XtlExceptions("当前资源库尚未初始化,不能强行停止作业!");
            }
            Job job = jobMap.get(xJob.getJobId());
            if (job == null) {
                return XJobStatus.STOPPED;
            }
            KettleUtil.jobStopAll(job);
            log.debug("作业 {} 成功停止,作业状态{}!", xJob.getJobName(), job.getState());
            return KettleUtil.getJobStatus(job);
        } catch (XtlExceptions ex) {
            throw new XtlExceptions(ex.getMessage());
        }

    }


    /**
     * @Author: yuenbin
     * @Date :2020/11/24
     * @Time :10:04
     * @Motto: It is better to be clear than to be clever !
     * @Destrib: 强行退出一个作业
     **/
    public XJobStatus doKillJob(XJob xJob) throws XtlExceptions {
        String repoId = xJob.getJobRepositoryId();
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repoId + "");
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        try {

            if (xRepository == null) {
                throw new XtlExceptions("请定义资源库!");
            }
            repository = KettleUtil.holder.get(repoId);
            if (null == repository) {
                throw new XtlExceptions("当前资源库尚未初始化,不能强行停止作业!");
            }
            Job job = jobMap.get(xJob.getJobId());
            if (job == null) {
                return XJobStatus.STOPPED;
            }
            KettleUtil.jobKillAll(job);
            log.debug("作业 {} 被强行终止,作业状态{}!", xJob.getJobName(), job.getState());
            return KettleUtil.getJobStatus(job);
        } catch (XtlExceptions e) {
            throw new XtlExceptions(e.getMessage());
        }

    }

}
