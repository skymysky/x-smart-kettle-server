package org.yaukie.frame.kettle.listener;

import com.atomikos.util.DateHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.core.logging.LoggingRegistry;
import org.pentaho.di.job.Job;
import org.pentaho.di.trans.Trans;
import org.yaukie.core.util.GenCodeUtil;
import org.yaukie.core.util.SpringContextUtil;
import org.yaukie.frame.kettle.service.LogService;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.log.LoggingEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Author: yuenbin
 * @Date :2020/11/26
 * @Time :15:32
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 增加kettle执行日志完整监听
 **/
@Slf4j
public class XLogListener extends LoggingEventListener {

    private static Object object = new Object() ;

     private  static    String logFilePath ="";

     private  static   String logFileSize="20";

    private String logChannelId ;

    private String threadId ;

    private String  logType;

    /***定义全局的日志ID*/
    private String logId ;

    private StringBuilder logStr = new StringBuilder() ;

    public XLogListener(){
    }

    public XLogListener( OutputStream out , Object obj ){
        this.setOutputStream(out);
        this.setLogId(GenCodeUtil.nextId());
        if(obj instanceof Job){
            Job job = (Job) obj;
            this.setLogType("job");
            this.setLogChannelId(job.getLogChannelId());
            this.setThreadId(job.getObjectId().getId());
        }else {
            Trans  trans = (Trans) obj;
            this.setLogType("trans");
            this.setLogChannelId(trans.getLogChannelId());
            this.setThreadId(trans.getObjectId().getId());
         }

    }

    @Override
    public boolean writeFileLog(KettleLoggingEvent event,Object obj )  {
        try {
            Object messageObject = event.getMessage();
            if (messageObject instanceof LogMessage) {
                boolean logToFile = false;
                XLogListener local = (XLogListener) this.getKettleLogListenerMap().get(obj);
                if (local.getLogChannelId()== null) {
                    logToFile = true;
                } else {
                    LogMessage message = (LogMessage)messageObject;
                    List<String> logChannelChildren = LoggingRegistry.getInstance().getLogChannelChildren(local.getLogChannelId());
                    logToFile = Const.indexOfString(message.getLogChannelId(), logChannelChildren) >= 0;
                }

                if (logToFile) {
                    String logText = this.layout.format(event);
                   local.getOutputStream().write(logText.getBytes());
                    local.getOutputStream().write(Const.CR.getBytes());
                    return true;
                }
            }
        }catch (IOException e) {
            log.error("写入日志出现异常,原因为:",e );
        }
        return false;
    }

    @Override
    public boolean writeDbLog(KettleLoggingEvent event,Object obj ) {
        LogService logService = (LogService) SpringContextUtil.getBean("logService",LogService.class);
        try {
            Object messageObject = event.getMessage();
            if (messageObject instanceof LogMessage) {
                boolean logToDb = false;
                XLogListener local = (XLogListener) this.getKettleLogListenerMap().get(obj);
                if (local.getLogChannelId() == null) {
                    logToDb = true;
                } else {
                    LogMessage message = (LogMessage)messageObject;
                     List<String> logChannelChildren = LoggingRegistry.getInstance().getLogChannelChildren(local.getLogChannelId());
                    logToDb = Const.indexOfString(message.getLogChannelId(), logChannelChildren) >= 0;
                  }

                if (logToDb) {
                    String logText = this.layout.format(event);
                    String type="";
                    //取默认值 正在运行中

                    String recordStatus="" ;
                    String startTime="";
                    String endTime="";
                    String logFile ="";
                    if(obj instanceof  Job ){
                        Job job = (Job) obj;
                        type="job";
                        startTime=startTimeMap.get(local.getThreadId());
                        logFile=this.jobFileMap.get(job).getAbsolutePath() ;
                        recordStatus = KettleUtil.getJobStatus(job).value();
                    }else if(obj instanceof  Trans){
                        Trans trans = (Trans) obj;
                        type="trans";
                        startTime=startTimeMap.get(local.getThreadId());
                        logFile=this.jobFileMap.get(trans).getAbsolutePath() ;
                        recordStatus = KettleUtil.getTransStatus(trans).value();
                    }
                    /**保存日志执行记录到数据库*/
                    String logId = local.getLogId() ;
                     logService.addLog(logId,local.getThreadId(),type,recordStatus,
                             logFile,
                             local.logStr.append(logText).append((char)13).append((char)10).toString(),
                             startTime,endTime);
                    return true;
                }
            }
        }catch (Exception e) {
            log.error("写入日志出现异常,原因为:",e );
        }
        return false;
    }

    @Override
    public void recordWarningLog(KettleLoggingEvent event, Object obj) {
        LogService logService = (LogService) SpringContextUtil.getBean("logService",LogService.class);
        Object object = event.getMessage();
        LogMessage message = (LogMessage)object;
        String joblogStr = message.getMessage();
        pattern = Pattern.compile("(error)");
        Matcher m = pattern.matcher(joblogStr) ;
        if (m.find() || message.getLevel().isError()) {
            String msg = getExceptionMsg(joblogStr, m);
            String logLevel = message.getLevel().getLevel() + "";
            String error = String.valueOf(message.isError());
            String subject = message.getSubject();
            String logChannel = message.getLogChannelId();
            String logFile = this.logFilePath;
            String targetId =threadId ;
            String type="";
            String targetName ="未知线程任务：" + Thread.currentThread().getName();
              if(obj instanceof Job ){
                Job job = (Job) obj;
                logFile = super.jobFileMap.get(job).getAbsolutePath() ;
                 targetId = job.getObjectId().getId() ;
                 targetName = job.getJobMeta().getName();
                 type="job";
             }else  if(obj instanceof Trans ){
                 Trans trans = (Trans) obj;
                 logFile = super.jobFileMap.get(trans).getAbsolutePath() ;
                 targetId = trans.getObjectId().getId() ;
                 targetName = trans.getTransMeta().getName();
                 type="trans";
             }

             logService.doAddLogWarning(logChannel,targetId ,targetName ,logFile ,error , msg, subject, logLevel, type);
              log.debug("异常日志已保存入库!" );
         }
    }

    public  static String  addLogListener(String logPath,Object  obj) throws KettleException {
         log.debug("任务{}日志监听启动了,日志路径{}...",obj,logPath );
         logFilePath=logPath;
         String target ;
         String targetName ;
        if(obj instanceof Job){
            Job job = (Job) obj;
            target="job";
            targetName = job.getJobMeta().getName() ;
            activeThreadMap.put(  job.getObjectId().getId(), job);
            startTimeMap.put(job.getObjectId().getId(), DateHelper.format(new Date()) );
        }else {
            Trans  trans = (Trans) obj;
            target="trans";
            targetName = trans.getTransMeta().getName() ;
            activeThreadMap.put(  trans.getObjectId().getId(), trans);
            startTimeMap.put(trans.getObjectId().getId(), DateHelper.format(new Date()) );
        }
        try
        {
            File file = getLogFile(target, targetName) ;
            if(file == null ){
                throw  new KettleException("必须指定日志文件物理路径!");
            }
            jobFileMap.put(obj, file);
            XLogListener xLogListener = new XLogListener(new FileOutputStream(file, true),obj);
            kettleLogListenerMap.put(obj, xLogListener);
            return xLogListener.getLogId();
         } catch (KettleException e) {
            throw  new KettleException("出现异常,原因:"+e);
        } catch (IOException e) {
            throw new KettleException("出现异常!原因:"+e) ;
        }

    }


    private  static File getLogFile(String target,String targetName ){
        File file = null ;
        synchronized (object){
            /**如果定义了日志存储的物理路径,则将日志写入到磁盘一份*/
            if (StringUtils.isNotBlank(logFilePath)) {
                logFilePath =logFilePath.replaceAll("\\\\","\\/" );
                file = new File(logFilePath+"/"+target+"/"+targetName+"/") ;
                if(!file.exists()){
                    file.mkdirs() ;
                }
                StringBuilder logFilePathString = new StringBuilder();
                logFilePathString
                        .append(file.getAbsolutePath()).append("/")
                        .append(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()))
                        .append(".")
                        .append("txt");
                file = new File(logFilePathString.toString()) ;

                if(!file.exists()){
                    try {
                        file.createNewFile() ;
                    } catch (IOException e) {
                        log.error("创建文件出现异常,原因为:",e );
                    }
                }
            }
        }

           return  file ;
    }

    public static String getLogFilePath() {
        return logFilePath;
    }

    public static void setLogFilePath(String logFilePath) {
        XLogListener.logFilePath = logFilePath;
    }

    public static String getLogFileSize() {
        return logFileSize;
    }

    public static void setLogFileSize(String logFileSize) {
        XLogListener.logFileSize = logFileSize;
    }

    public String getLogChannelId() {
        return logChannelId;
    }

    public void setLogChannelId(String logChannelId) {
        this.logChannelId = logChannelId;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getLogType() {
        return logType;
    }

    public void setLogType(String logType) {
        this.logType = logType;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }
}
