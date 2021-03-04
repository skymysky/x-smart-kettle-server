package org.yaukie.frame.listener;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.yaukie.core.constant.BaseResultConstant;
import org.yaukie.core.exception.UserDefinedException;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XJobService;
import org.yaukie.frame.autocode.service.api.XQuartzService;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.yaukie.frame.autocode.service.impl.XJobServiceImpl;
import org.yaukie.frame.autocode.service.impl.XQuartzServiceImpl;
import org.yaukie.frame.autocode.service.impl.XTransServiceImpl;
import org.yaukie.frame.kettle.quartz.XQuartHandleService;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.Constant;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: yuenbin
 * @Date :2021/1/17
 * @Time :17:08
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: 创建一个监听器,监听应用启动之后要干的事情
 **/
@Component
@Slf4j
public class ApplicationStartUp implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${kettle.scheduler.enabled}")
    private String enabled ;

    @Value("${kettle.repo.name}")
    private String name ;

    @Value("${kettle.repo.hostName}")
    private String hostName ;

    @Value("${kettle.repo.dbName}")
    private String dbName ;

    @Value("${kettle.repo.dbPort}")
    private String dbPort ;

    @Value("${kettle.repo.userName}")
    private String userName ;

    @Value("${kettle.repo.passWord}")
    private String passWord ;

    @Value("${kettle.repo.repoLoginName}")
    private String repoLoginName ;

    @Value("${kettle.repo.repoLoginPass}")
    private String repoLoginPass ;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        //初始化默认资源库
        initKettleRepo();
        log.info("定时调度策略开关 {}",enabled);
        if(!StringUtils.isEmpty(enabled) && enabled.equalsIgnoreCase("true")){
            log.info("定时调度策略开关已开启{}",enabled);
            if(event.getApplicationContext().getParent() == null ){
                ApplicationContext context = event.getApplicationContext() ;
                XQuartzService xQuartzService = context.getBean(XQuartzServiceImpl.class);
                XQuartzExample xQuartzExample = new XQuartzExample() ;
                xQuartzExample.createCriteria().andIsDelEqualTo("0");
                List<XQuartz>  quartzList = xQuartzService.selectByExample(xQuartzExample) ;

                XQuartHandleService xQuartHandleService = context.getBean(XQuartHandleService.class);
                XJobService xJobService = context.getBean(XJobServiceImpl.class);
                XTransService xTransService = context.getBean(XTransServiceImpl.class);
                if(!CollectionUtils.isEmpty(quartzList)){
                    log.info("共扫描到{}调定时任务,准备执行ing....",quartzList.size());
                    for (XQuartz item : quartzList) {

                        String quartzType = item.getQuartzType() ;
                        String targetId = item.getTargetId() ;
                        // 加入定时任务
                        if(quartzType.equals("job")){
                            XJobExample xJobExample = new XJobExample() ;
                            xJobExample.createCriteria().andJobIdEqualTo(targetId);
                            XJob xJob = xJobService.selectFirstExample(xJobExample);
                            if(xJob ==null ){
                               continue;
                            }
                            xQuartHandleService.addJobToSche(xJob);
                        }else {
                            XTransExample xTransExample = new XTransExample() ;
                            xTransExample.createCriteria().andTransIdEqualTo(targetId);
                            XTrans xTrans = xTransService.selectFirstExample(xTransExample);
                            if(xTrans ==null ){
                                continue;
                            }
                            xQuartHandleService.addTransToSche(xTrans);
                        }

                    }

                }
            }
        }

    }

    /**
     *  @Author: yuenbin
     *  @Date :2021/2/1
     * @Time :16:35
     * @Motto: It is better to be clear than to be clever !
     * @Destrib:  初始化资源库
    **/
    private void initKettleRepo(){

        try {
            Repository repository =  KettleUtil.conByNative(
                    Constant.DEFAULT_REPO_ID,
                    name,
                    name,
                    "MYSQL",
                    hostName,
                    dbPort,
                    dbName,
                    userName,
                    passWord,
                    repoLoginName,
                    repoLoginPass);
            if(repository.isConnected()){
                log.info("资源库初始化成功,地址{}!",hostName+":"+dbPort+"/"+dbName);
            }else{
                log.info("资源库初始化失败,请检查!");
            }
        } catch (KettleException e) {
            StringWriter stringWriter = new StringWriter() ;
            e.printStackTrace(new PrintWriter(stringWriter));
            log.error("资源库{}初始化异常,原因为{}",hostName+":"+dbPort+"/"+dbName,stringWriter.toString().substring(0, 150));
        }

    }

}
