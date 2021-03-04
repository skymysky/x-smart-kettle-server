package org.yaukie.frame.autocode.controller;

import com.atomikos.util.DateHelper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.httpclient.util.DateUtil;
import org.quartz.*;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.yaukie.builder.QuartzManager;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.BaseResultConstant;
import org.yaukie.core.constant.PageResult;
import org.yaukie.core.exception.UserDefinedException;
import org.yaukie.frame.autocode.dao.mapper.ExtendMapper;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XJobService;
import org.yaukie.frame.autocode.service.api.XQuartzService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
 import io.swagger.annotations.Api;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.yaukie.frame.kettle.quartz.XQuartHandleService;
import org.yaukie.xtl.cons.Constant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author: yuenbin
* @create: 2020/11/13 15/46/284
**/
@RestController
@RequestMapping(value = "/op/xquartz/")
@Api(value = "XQuartz控制器", description = "XQuartz管理")
@Slf4j
public class XQuartzController  extends BaseController {

    @Autowired
    private XQuartzService xQuartzService;

    @Autowired
    private ExtendMapper extendMapper ;

    @Autowired
    private XJobService xJobService ;

    @Autowired
    private XTransService xTransService ;

    @Autowired
    private XQuartHandleService xQuartHandleService ;

    @GetMapping(value = "/checkScheduler")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId" + "", value = "jobId" + "", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "cron" + "", value = "cron" + "", required = true, dataTypeClass = String.class),
            @ApiImplicitParam(name = "description" + "", value = "description" + "", required = true, dataTypeClass = String.class)
    })
    @ApiOperation("监测当前任务是否已加入定时队列")
    public BaseResult checkScheduler(
            @RequestParam(name = "targetId", required = true) String targetId,
             @RequestParam(name = "targetType", required = true) String targetType) {

        String name = "";
        String path="" ;
        if(targetType.equals("job")){
            XJobExample xJobExample = new XJobExample();
            xJobExample.createCriteria().andJobIdEqualTo(targetId);
            XJob xJob = xJobService.selectFirstExample(xJobExample);
            if (xJob == null) {
                return BaseResult.fail("作业不存在!");
            }
            name = xJob.getJobName();
            path = xJob.getJobPath() ;
        }else {
            XTransExample xTransExample = new XTransExample();
            xTransExample.createCriteria().andTransIdEqualTo(targetId);
            XTrans  xTrans = xTransService.selectFirstExample(xTransExample);
            if (xTrans == null) {
                return BaseResult.fail("作业不存在!");
            }
            name = xTrans.getTransName() ;
            path = xTrans.getTransPath() ;
        }

        //查看当前定时队列中是否有此定时任务
        SchedulerFactory schedulerFactory = QuartzManager.schedulerFactory ;
        try {
            Scheduler scheduler = schedulerFactory.getScheduler() ;
            Map param = Constant.getQuartzBasic(name, path);
            JobKey jobKey = JobKey.jobKey(param.get("jobName")+"",
                    param.get("jobGroupName")+"");
            boolean exists =  scheduler.checkExists(jobKey);
            if(exists){
                return  new BaseResult(11002, "定时队列中已存在该任务,任务名称为["+jobKey.getName()+"]", null);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return BaseResult.success() ;
    }

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
   @EnablePage
     public BaseResult getQuartzPageList(
                                        @RequestParam(value = "offset",required = false)String offset,
                                        @RequestParam(value = "limit",required = false)String limit,
                                        @RequestParam(value = "name",required = false)String name,
                                        @RequestParam(value = "targetResult",required = false)String targetResult,
                                        @RequestParam(value = "createDateBegin",required = false)String createDateBegin,
                                        @RequestParam(value = "createDateEnd",required = false)String createDateEnd) {
        Map params = new HashMap() ;
        if(org.apache.commons.lang.StringUtils.isNotEmpty(name)){
            params.put("name",name );
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(targetResult)){
            params.put("targetResult",targetResult );
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(createDateBegin)){
            try {
                params.put("startTime", DateHelper.format(new SimpleDateFormat("yyyy.MM.dd").parse(createDateBegin.replaceAll("\\-", "."))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            params.put("startTime", DateUtil.formatDate(new Date(), "yyyy.MM.dd") );
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(createDateEnd)){
            try {
                params.put("stopTime", DateHelper.format(new SimpleDateFormat("yyyy.MM.dd").parse(createDateEnd.replaceAll("\\-", "."))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            params.put("stopTime", DateUtil.formatDate(new Date(), "yyyy.MM.dd"));
        }

        List<Map> dataList = extendMapper.qrySchedulerInfo(params);
            if(!CollectionUtils.isEmpty(dataList)){
                dataList.forEach(item ->{
                    String cron = item.get("quartzCron")+"";
                    String isDel=item.get("isDel")+"";
                    item.put("preTime", getPreCron(cron));
                    if(isDel.equals("1")){
                        item.put("preTime", "");
                    }

                });
            }
               PageResult pageResult = new PageResult(dataList);
                Map<String, Object> result = new HashMap<>();
                result.put(RESULT_ROWS, pageResult.getRows());
                result.put(RESULT_TOTLAL, pageResult.getTotal());
                return BaseResult.success( result);
                }

                @GetMapping(value = "/get")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string",paramType = "header"),
                            @ApiImplicitParam(name = "type", value = "type", required = true, dataType = "string",paramType = "header")
                    })
               @ApiOperation("获取信息")
                public BaseResult getQuartz(@RequestParam String id,
                                            @RequestParam String type) {
        XQuartzExample xQuartzExample = new XQuartzExample() ;
                    xQuartzExample.createCriteria().andQuartzTypeEqualTo(type)
                            .andTargetIdEqualTo(id) ;
                XQuartz xQuartz = this.xQuartzService.selectFirstExample(xQuartzExample) ;
                    return BaseResult.success(xQuartz);
                    }

                    @PostMapping(value = "/add")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "xQuartz"+"", value = "xQuartz"+"",
                    required = true,dataTypeClass =XQuartz.class),
                    })
                    @ApiOperation("新增")
                    public BaseResult addQuartz(@RequestBody @Validated XQuartz xQuartz, BindingResult BindingResult) {
                        if (BindingResult.hasErrors()) {
                        return this.getErrorMessage(BindingResult);
                        }
                        this.xQuartzService.insertSelective(xQuartz);
                        return BaseResult.success();
                        }

                        @PostMapping(value = "/update")
                        @ApiOperation("更新")
                        @ApiImplicitParams({
                        @ApiImplicitParam(name = "xQuartz"+"", value = "xQuartz"+"",
                            required = true,dataTypeClass =XQuartz.class),
                        })
                        public BaseResult updateQuartz(@RequestBody @Validated XQuartz xQuartz, BindingResult BindingResult) {
                            if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                            }

                            this.xQuartzService.updateByPrimaryKey(xQuartz);
                            return BaseResult.success();
                            }


                    @GetMapping(value = "/updScheStatus")
                    @ApiOperation("切换定时器开关状态")
                    @ApiImplicitParams({
                            @ApiImplicitParam(name = "quartzId"+"", value = "quartzId"+"",
                                    required = true,dataTypeClass =XQuartz.class),
                            @ApiImplicitParam(name = "isDel"+"", value = "isDel"+"",
                                    required = true,dataTypeClass =XQuartz.class),
                    })
                    public BaseResult updateQuartz(@RequestParam String quartzId,
                                                   @RequestParam String isDel ) {
                        XQuartzExample xQuartzExample = new XQuartzExample() ;
                        xQuartzExample.createCriteria().andQuartzIdEqualTo(quartzId);
                        XQuartz tmpQuartz = xQuartzService.selectFirstExample(xQuartzExample);
                        String quartzType = tmpQuartz.getQuartzType() ;
                        String targetId = tmpQuartz.getTargetId() ;
                        XQuartz xQuartz = new XQuartz() ;
                        xQuartz.setIsDel(isDel);
                        int affect = xQuartzService.updateByExampleSelective(xQuartz, xQuartzExample);
                        if(affect > 0 ){
                            log.debug("定时任务开关切换成功");
                        }

                        if(isDel.equals("0")){
                            // 加入定时任务
                            if(quartzType.equals("job")){
                                XJobExample xJobExample = new XJobExample() ;
                                xJobExample.createCriteria().andJobIdEqualTo(targetId);
                                XJob xJob = xJobService.selectFirstExample(xJobExample);
                                if(xJob ==null ){
                                    throw new UserDefinedException(BaseResultConstant.METHOD_EXCEPTION);
                                }
                                xQuartHandleService.addJobToSche(xJob);
                            }else {
                                XTransExample xTransExample = new XTransExample() ;
                                xTransExample.createCriteria().andTransIdEqualTo(targetId);
                                XTrans xTrans = xTransService.selectFirstExample(xTransExample);
                                if(xTrans ==null ){
                                    throw new UserDefinedException(BaseResultConstant.METHOD_EXCEPTION);
                                }
                                xQuartHandleService.addTransToSche(xTrans);
                            }
                        }else {
                            //准备从定时器中移除任务,防止下次再运行
                            if(quartzType.equals("job")){
                                XJobExample xJobExample = new XJobExample() ;
                                xJobExample.createCriteria().andJobIdEqualTo(targetId);
                                XJob xJob = xJobService.selectFirstExample(xJobExample);
                                if(xJob ==null ){
                                    throw new UserDefinedException(BaseResultConstant.METHOD_EXCEPTION);
                                }
                                xQuartHandleService.removeJobFromSche(xJob);
                            }else {
                                XTransExample xTransExample = new XTransExample() ;
                                xTransExample.createCriteria().andTransIdEqualTo(targetId);
                                XTrans xTrans = xTransService.selectFirstExample(xTransExample);
                                if(xTrans ==null ){
                                    throw new UserDefinedException(BaseResultConstant.METHOD_EXCEPTION);
                                }
                                xQuartHandleService.removeTransFromSche(xTrans);
                            }
                        }



                        return BaseResult.success() ;

                    }

                            @GetMapping(value = "/delete")
                            @ApiOperation("删除")
                              @ApiImplicitParams({
                            @ApiImplicitParam(name = "targetId", value = "targetId", required = true, dataType = "string" ),
                            @ApiImplicitParam(name = "targetType", value = "targetType", required = true, dataType = "string" )
                            })
                            public BaseResult deleteQuartz(@RequestParam String targetId,
                                                           @RequestParam String targetType) {

                                if(targetType.equals("job")){
                                    XJobExample xJobExample = new XJobExample();
                                    xJobExample.createCriteria().andJobIdEqualTo(targetId);
                                    XJob xJob = xJobService.selectFirstExample(xJobExample);
                                    if (xJob == null) {
                                        // 直接删除掉
                                        XQuartzExample xQuartzExample = new  XQuartzExample();
                                        xQuartzExample.createCriteria().andTargetIdEqualTo(targetId)
                                                .andQuartzTypeEqualTo(targetType);
                                        this.xQuartzService.deleteByExample(xQuartzExample);
                                        return BaseResult.success();
                                    }else {
                                        try {
                                            xQuartHandleService.removeJobFromSche(xJob);
                                        }catch (Exception ex )
                                        {
                                            return new UniformReponseHandler().sendErrorResponse_System(ex);
                                        }
                                    }

                                }else {
                                    XTransExample xTransExample = new XTransExample();
                                    xTransExample.createCriteria().andTransIdEqualTo(targetId);
                                    XTrans  xTrans = xTransService.selectFirstExample(xTransExample);
                                    if (xTrans == null) {
                                        //直接删除掉
                                        XQuartzExample xQuartzExample = new  XQuartzExample();
                                        xQuartzExample.createCriteria().andTargetIdEqualTo(targetId)
                                                .andQuartzTypeEqualTo(targetType);
                                        this.xQuartzService.deleteByExample(xQuartzExample);
                                        return BaseResult.success();
                                    }else {
                                        try {
                                            xQuartHandleService.removeTransFromSche(xTrans);
                                        }catch (Exception ex )
                                        {
                                            return new UniformReponseHandler().sendErrorResponse_System(ex);
                                        }
                                    }

                                }

                                XQuartzExample xQuartzExample = new  XQuartzExample();
                                xQuartzExample.createCriteria().andTargetIdEqualTo(targetId)
                                        .andQuartzTypeEqualTo(targetType);
                                 this.xQuartzService.deleteByExample(xQuartzExample);
                                return BaseResult.success();

                                }

                                public BaseResult getErrorMessage(BindingResult BindingResult){
                                    String errorMessage = "";
                                    for (ObjectError objectError : BindingResult.getAllErrors()) {
                                    errorMessage += objectError.getDefaultMessage();
                                    }
                                    return BaseResult.fail(errorMessage);
                                    }

                                    /**
                                     *  @Author: yuenbin
                                     *  @Date :2021/1/17
                                     * @Time :10:58
                                     * @Motto: It is better to be clear than to be clever !
                                     * @Destrib:  下一次调度时间
                                    **/
                                    private String getPreCron(String cron){

                                        try {
                                            CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
                                            cronTriggerImpl.setCronExpression(cron);
                                            // 这个是重点，一行代码搞定
                                            List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 1);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
                                            return  dateFormat.format(dates.get(0)) ;
                                        } catch (ParseException e) {
                                            log.debug("解析定时规则出错{}",e);
                                        }
                                        return "" ;
                                    }
        }
