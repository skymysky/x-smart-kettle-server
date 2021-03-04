package org.yaukie.frame;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.KettleClientEnvironment;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.ProgressNullMonitorListener;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobEntryResult;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.filerep.KettleFileRepositoryMeta;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.performance.StepPerformanceSnapShot;
import org.pentaho.di.www.Carte;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.di.www.SlaveServerConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author: yuenbin
 * @Date :2020/10/27
 * @Time :10:11
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 练练手
 **/
@Slf4j
public class Test {
    public static ArrayList<String> getPreDate(String curDate,int intervals )  {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = 0; i <intervals; i++) {
            try {
                pastDaysList.add(getPastDate(curDate,i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return pastDaysList;
    }

    /**
     * 获取过去第几天的日期
     *
     * @param past
     * @return
     */
    public static String getPastDate(String curDate,int past) throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(curDate));
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        return result;
    }
    public static void main(String[] args) throws KettleException  {
        List list =getPreDate("2021-02-27",10) ;
        for (Object o : list) {
            System.out.println("o.toString() = " + o.toString());
        }
    }

    private static void runJob(KettleDatabaseRepository kettleDatabaseRepository,RepositoryDirectoryInterface directory,String jobName){

        try {
            JobMeta jobMeta = kettleDatabaseRepository.loadJob(jobName,
                    directory, new ProgressNullMonitorListener(),null );
        Job job =new Job(kettleDatabaseRepository, jobMeta) ;
        job.setDaemon(false);
        job.setLogLevel(LogLevel.DEBUG);
        job.run();
        job.waitUntilFinished();
        if(job.isFinished()){
             List<JobEntryResult> list = job.getJobEntryResults();
             list.forEach(jobEntryResult -> {
                System.out.println("===={"+jobEntryResult.getJobEntryName()+"\t"+
                        jobEntryResult.getJobEntryNr()+"\t"+jobEntryResult.getLogChannelId()+"\t"
                        +jobEntryResult.getResult()+"" +
                        "]===");
            });
//            String logChannelId = job.getLogChannelId();
//            System.out.println("xxxxx"+logChannelId);
//           LoggingBuffer loggingBuffer =  KettleLogStore.getAppender() ;
//           String logText = loggingBuffer.getBuffer(logChannelId,true ).toString();
//            System.out.println(logText);
        }else {
            System.err.println("执行失败!");
        }
        } catch (KettleException e) {
            e.printStackTrace();
        }

    }


    private static void runTrans(KettleDatabaseRepository kettleDatabaseRepository,RepositoryDirectoryInterface directory,String transName){

        try {
            TransMeta transMeta = kettleDatabaseRepository.loadTransformation(transName,directory,new ProgressNullMonitorListener(),true,null);
            transMeta.setCapturingStepPerformanceSnapShots(true);
            Trans trans =new Trans(transMeta) ;
            trans.setLogLevel(LogLevel.DEBUG);
            trans.setMonitored(true);
            trans.setInitializing(true);
            trans.setPreparing(true);
            trans.setRunning(true);
            trans.setSafeModeEnabled(true);
            trans.execute(null);
            trans.waitUntilFinished();
            if(trans.isFinished()){
                doSth(trans);
                String logChannelId = trans.getLogChannelId();
                LoggingBuffer loggingBuffer =  KettleLogStore.getAppender() ;
                String logText = loggingBuffer.getBuffer(logChannelId,true ).toString();
//                System.out.println("ccc\t"+logText);
            }else {
                System.err.println("执行失败!");
            }
        } catch (KettleException e) {
            e.printStackTrace();
        }

    }

    private static  void doSth(Trans trans){
        //得到所有步骤
        Map<String, List<StepPerformanceSnapShot>> SnapShots = trans.getStepPerformanceSnapShots();
        System.out.println("步骤总共有:"+SnapShots.size());
        //输出动态监控情况
        Iterator it = SnapShots.entrySet().iterator();
        String oneTimeOneStepInfo = "";
        String ontTimeAllStepInfo = "";
        while(it.hasNext())
        {
            Map.Entry en = (Map.Entry)it.next();
            //步骤当前情况
            ArrayList SnapShotList = (ArrayList) en.getValue();
            System.out.println("SnapShotList 的长度"+SnapShotList.size());
            if (SnapShotList != null && SnapShotList.size() > 0) {
                //输出所有步骤情况
                StepPerformanceSnapShot SnapShot = (StepPerformanceSnapShot) SnapShotList.get(SnapShotList.size() - 1);

                 oneTimeOneStepInfo = (
                        "StepName:"+SnapShot.getStepName()+";"
                                +"Errors: " + SnapShot.getErrors() + ";"
                                + "InputBufferSize: "+ SnapShot.getInputBufferSize()+ ";"
                                + "LinesInput: " + SnapShot.getLinesInput()+ ";"
                                + "LinesOutput: "+ SnapShot.getLinesOutput() + ";"
                                + "LinesRead: "+ SnapShot.getLinesRead()+ ";"
                                + "LinesRejected: "+ SnapShot.getLinesRejected()+ ";"
                                + "LinesUpdated: "+ SnapShot.getLinesUpdated()+ ";"
                                + "LinesWritten: "+ SnapShot.getLinesWritten()+ ";"
                                + "OutputBufferSize: "+ SnapShot.getOutputBufferSize()+ ";"
                                + "StepCopy: " + SnapShot.getStepCopy()+ ";"
                                + "TimeDifference: "+ SnapShot.getTimeDifference()+ ";"
                                + "TotalErrors: "+ SnapShot.getTotalErrors()+ ";"
                                + "TotalLinesInput: "+ SnapShot.getTotalLinesInput()+ ";"
                                + "TotalLinesOutput: "+ SnapShot.getTotalLinesOutput()+ ";"
                                + "TotalLinesRead: "+ SnapShot.getTotalLinesRead()+ ";"
                                + "TotalLinesRejected: "+ SnapShot.getTotalLinesRejected()+ ";"
                                + "TotalLinesUpdated: "+ SnapShot.getTotalLinesUpdated()+ ";"
                                + "TotalLinesWritten: "+ SnapShot.getTotalLinesWritten()+ ";"
                                + "Date:"+ SnapShot.getDate() + "\n");

                ontTimeAllStepInfo+=oneTimeOneStepInfo;

            }

        }
        System.out.println(ontTimeAllStepInfo+"\n\n\n\n");
    }

}
