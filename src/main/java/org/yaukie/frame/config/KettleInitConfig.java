package org.yaukie.frame.config;

import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepositoryMeta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaukie.frame.kettle.listener.XLogListener;
import org.yaukie.frame.pool.StandardPoolExecutor;
import org.yaukie.frame.pool.StandardThreadFactory;
import org.yaukie.xtl.config.KettleInit;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: yuenbin
 * @Date :2020/10/27
 * @Time :18:31
 * @Motto: It is better to be clear than to be clever !
 * @Destrib: kettle环境初始化入口工具
 **/
@Configuration
@EnableConfigurationProperties(KettleThreadPoolProperties.class)
@Slf4j
public class KettleInitConfig {


    @Value("${kettle.log.file.path}")
    private String logFilePath ;

    @Value("${kettle.log.file.size}")
    private String logFileSize;


    @Value("${kettle.scheduler.enabled}")
    private String enabled;

    @PostConstruct
    private void init(){
        try {
            log.debug("kettle 环境准备初始化,,");
            log.debug("kettle日志文件最大可支持{}MB",logFileSize );
            log.debug("kettle日志默认存放路径为{}",new File(logFilePath).getAbsoluteFile());
            KettleInit.init();
            KettleEnvironment.init();
            /**默认开启日志监听*/
            KettleLogStore.getAppender().addLoggingEventListener(new XLogListener());
            log.debug("kettle 环境初始化完成,,");
        } catch (KettleException e) {
            log.error("kettle初始化出现异常,{}",e);
        }
    }


    /**
     * 配置kettle任务运行线程池
     * 为了提高转换或作业执行效率
      * @return StandardThreadExecutor
     */
    @Bean
    public StandardPoolExecutor executor(KettleThreadPoolProperties properties) {
        log.debug("线程池准备初始化..");
        Set<Thread> threadSet = new HashSet<>();
        return new StandardPoolExecutor(
                properties.getCoreThreads(),
                properties.getMaxThreads(),
                properties.getKeepAliveTimeMin(),
                TimeUnit.SECONDS,
                properties.getQueueCapacity(),
                new StandardThreadFactory(properties.getNamePrefix(),threadSet),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }


//    @Bean
//    public Carte carte(){
//        log.debug("准备启动 carte 子服务器,,,");
//        KettleClientEnvironment.getInstance().setClient(KettleClientEnvironment.ClientType.CARTE);
//        SlaveServerConfig config = new SlaveServerConfig();
//        SlaveServer slaveServer = new SlaveServer("localhost:8010","localhost","8010","admin","admin");
//        slaveServer.setMaster(false);
//        KettleLogStore.init(config.getMaxLogLines(), config.getMaxLogTimeoutMinutes());
//        config.setJoining(true);
//        config.setSlaveServer(slaveServer);
//        Carte carte = null;
//        try {
//            carte = new Carte(config, false);
//            CarteSingleton.setCarte(carte);
//            carte.getWebServer().join();
//        } catch (Exception e) {
//            log.debug("carte 子服务器启动出现异常,{},,,",e);
//        }
//         return carte;
//    }

}
