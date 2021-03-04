package org.yaukie.frame.autocode.dao.mapper;

import java.util.List;
import java.util.Map;

/**
 *  @Author: yuenbin
 *  @Date :2020/12/28
 * @Time :20:27
 * @Motto: It is better to be clear than to be clever !
 * @Destrib:
**/
public interface ExtendMapper {


    List<Map> getTransWarningRecords(Map param);
    List<Map> getJobWarningRecords(Map param);
    List<Map> getRunnedInstancesTrend(Map param);
    List<Map> getRunnedErrorTrend(Map param);
    List<Map> getRunnedOkTrend(Map param);
    Map getNormalTransInstances(Map param);
    Map getNormalJobInstances(Map param);
    Map getSpecialJobInstances(Map param);
    Map getSpecialTransInstances(Map param);
    Map getRunnedErrorSum(Map param);
    Map getRunnedOkSum(Map param);
    Map getRunnedInstances(Map param);
    Map getRunningInstances(Map param);
    Map getRunnedErrorInstances(Map param);
    Map getRunnedOkInstances(Map param);
    Map getTotalInstances(Map param);

    /**
     *  取日志详情
     * @param param
     * @return
     */
    Map qryLog(Map param);

    /**
     *  获取作业或转换的预警日志
     * @param param
     * @return
     */
    List<Map> qryLogWarning(Map param) ;

    /**
     *  获取作业或转换的日志详情
     * @param param
     * @return
     */
    List<Map> qryLogInfo(Map param) ;

    /**
     *  获取定时列表详情
     * @param param
     * @return
     */
    List<Map> qrySchedulerInfo(Map param) ;

    Map qryTransLogText(Map param);

    /**
     *  获取作业调度日志
     * @return
     */
    Map qryJobLogText(Map param);

    /**
     *  获取作业调度情况
     * @return
     */
    List<Map> qryJobPageInfo(Map  params);

    /**
     *  获取转换调度情况
     * @return
     */
    List<Map> qryTransPageInfo(Map params);

}