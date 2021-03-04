package org.yaukie.frame.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

/**
 *  @Author: yuenbin
 *  @Date :2020/10/27
 * @Time :18:41
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:  配置kettle 多任务异步线程池
 *  配置一下线程池的基础参数
**/
@ConfigurationProperties(prefix = "kettle.pool")
@Data
public class KettleThreadPoolProperties implements Serializable {

    /**
     * 线程池前缀
     */
    private String namePrefix = "kettleThreadPool";

    /**
     * 核心线程数
     */
    private int coreThreads;// = 20;

    /**
     * 最大的线程数
     */
    private int maxThreads;// = 50;

    /**
     * 队列容量
     */
    private int queueCapacity;// = 100;

    /**
     * 空闲x分钟则释放线程
     */
    private long keepAliveTimeMin;// = 5;


}
