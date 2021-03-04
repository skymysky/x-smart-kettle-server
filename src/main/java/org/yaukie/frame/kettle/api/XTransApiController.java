package org.yaukie.frame.kettle.api;

import com.atomikos.util.DateHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.TransMeta;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.yaukie.builder.QuartzManager;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.annotation.LogAround;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.BaseResultConstant;
import org.yaukie.core.constant.PageResult;
import org.yaukie.core.exception.UserDefinedException;
import org.yaukie.core.util.GenCodeUtil;
import org.yaukie.frame.autocode.dao.mapper.ExtendMapper;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XQuartzService;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.yaukie.frame.kettle.core.XTransSubmit;
import org.yaukie.frame.kettle.quartz.XQuartHandleService;
import org.yaukie.frame.kettle.service.LogService;
import org.yaukie.frame.kettle.service.TransService;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.Constant;
import org.yaukie.xtl.cons.XTransStatus;
import org.yaukie.xtl.exceptions.XtlExceptions;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author: yuenbin
* @create: 2020/11/09 11/28/955
**/
@RestController
@RequestMapping(value = "/api/xtrans/")
@Api(value = "转换调度接口控制器", description = "转换调度接口控制器")
@Slf4j
public class XTransApiController extends BaseController {

    @Autowired
    private XTransService xTransService;

    @Autowired
    private XTransSubmit xTransSubmit ;

    @Autowired
    private LogService logService;

    @Autowired
    private TransService transService;

@Autowired
    private XQuartzService xQuartzService ;

    @Autowired
    private XQuartHandleService xQuartHandleService;

    @Autowired
    private ExtendMapper extendMapper ;

    @Autowired
    private XRepositoryService xRepositoryService ;

    @Resource
    private HttpServletResponse response;


    @GetMapping(value = "/qryTransPageInfo")
    @ApiOperation("获取转换调度情况")
    @EnablePage
    @LogAround("获取转换调度情况")
    public BaseResult qryTransPageInfo(
            @RequestParam(value = "offset",required = false)String offset,
            @RequestParam(value = "limit",required = false)String limit,
            @RequestParam(value = "transName",required = false)String transName,
            @RequestParam(value = "targetResult",required = false)String targetResult,
            @RequestParam(value = "createDateBegin",required = false)String createDateBegin,
            @RequestParam(value = "createDateEnd",required = false)String createDateEnd) {

        Map params =new HashMap() ;
        if(StringUtils.isNotEmpty(transName)){
            params.put("transName",transName );
        }
        if(StringUtils.isNotEmpty(targetResult)){
            params.put("targetResult",targetResult );
        }
        if(StringUtils.isNotEmpty(createDateBegin)){
            try {
                params.put("startTime", DateHelper.format(new SimpleDateFormat("yyyy.MM.dd").parse(createDateBegin.replaceAll("\\-", "."))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            params.put("startTime",DateUtil.formatDate(new Date(), "yyyy.MM.dd") );
        }
        if(StringUtils.isNotEmpty(createDateEnd)){
            try {
                params.put("stopTime", DateHelper.format(new SimpleDateFormat("yyyy.MM.dd").parse(createDateEnd.replaceAll("\\-", "."))));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            params.put("stopTime", DateUtil.formatDate(new Date(), "yyyy.MM.dd"));
        }

        List<Map> list = extendMapper.qryTransPageInfo(params) ;

        PageResult pageResult = new PageResult(list);
        Map<String, Object> result = new HashMap<>();
        List<Map> dataList = (List) pageResult.getRows();
        if(!CollectionUtils.isEmpty(dataList)){
            dataList.forEach(item -> {
                if(!item.containsKey("quartz")){
                    item.put("quartz","不需要定时" );
                }else {
                    String quartz = item.get("quartz") +"";
                    if(StringUtils.isBlank(quartz)){
                        item.put("quartz","不需要定时" );
                    }
                }
            });
        }
        result.put(RESULT_ROWS,dataList);
        result.put(RESULT_TOTLAL, pageResult.getTotal());

        return BaseResult.success( result);

    }


    @GetMapping(value = "/getTransImage",produces = "application/json")
    @ApiOperation("获取转换调度图")
    @LogAround("获取转换调度图")
    public BaseResult getJobImage(
            @RequestParam(value = "transId",required = true)String transId
    )   {
        if(StringUtils.isEmpty(transId)){
            return BaseResult.fail();
        }
        XTransExample xTransExample = new XTransExample() ;
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans  xTrans = xTransService.selectFirstExample(xTransExample);
        String transType = xTrans.getTransType() ;
        // 去对应资源库修改转换
        String repositoryId = xTrans.getTransRepositoryId();
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        if (xRepository == null) {
            return  new UniformReponseHandler().sendErrorResponse_System(new XtlExceptions("请定义资源库!"));
        }
        OutputStream os =null ;
        try {

            Repository repository ;
            if(xTrans.getTransType().equals("db")){
                repository =  KettleUtil.conByNative(xRepository.getRepoId(),xRepository.getRepoName(),
                        xRepository.getRepoName(),xRepository.getRepoType(),
                        xRepository.getDbHost(),xRepository.getDbPort(),xRepository.getDbName(),
                        xRepository.getDbUsername(),xRepository.getDbPassword(),xRepository.getRepoUsername(),
                        xRepository.getRepoPassword()) ;
            }else {
                repository = KettleUtil.conFileRep(xTrans.getTransRepositoryId(),
                        xRepository.getRepoName(),xRepository.getBaseDir() );
            }

            TransMeta transMeta = KettleUtil.loadTrans(xTrans.getTransName(), xTrans.getTransPath(), repository);

            BufferedImage bufferedImage = KettleUtil.generateTransformationImage(transMeta);

            response.setContentType("image/png");
            os = response.getOutputStream() ;
            if(bufferedImage !=null ){
                ImageIO.write(bufferedImage, "png",os );
            }
        }catch (KettleException e1 )
        {
            return new UniformReponseHandler().sendErrorResponse_System(e1) ;
        }catch (Exception e2) {
            return new UniformReponseHandler().sendErrorResponse_System(e2) ;
        } finally {
            if(os !=null ){
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return BaseResult.success() ;
    }



    @GetMapping(value = "/getLogText",produces = "application/json")
    @ApiOperation("获取调度日志")
    @LogAround("获取调度日志")
    public BaseResult getLogText(
            @RequestParam(value = "transId",required = true)String transId
    )   {
        if(StringUtils.isEmpty(transId)){
            return BaseResult.fail();
        }

        Map param = new HashMap() ;
        param.put("transId", transId) ;
        Map result = extendMapper.qryTransLogText(param) ;
        return BaseResult.success(result==null?"暂无运行数据":result.get("logText")) ;
    }


    @RequestMapping(value = "/pauseTrans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("暂停一个转换")
    public BaseResult pauseTrans(
            @RequestParam(name = "transId",required = true) String transId) {
        if(StringUtils.isEmpty(transId)){
            throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
        }

        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }

        XTransStatus xTransStatus = null ;
        try {
            xTransStatus =  transService.doPauseTrans(xTrans);
        } catch (XtlExceptions e) {
            String msg = e.getMessage() ;
            return BaseResult.fail(msg) ;
        }

        return new UniformReponseHandler<>()
                .sendSuccessResponse("转换暂停成功,状态:"+xTransStatus.description());

    }

    @RequestMapping(value = "/resumeTrans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("恢复运行转换")
    public BaseResult resumeTrans(
            @RequestParam(name = "transId",required = true) String transId) {
        if(StringUtils.isEmpty(transId)){
            throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
        }

        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }

        XTransStatus xTransStatus = null ;
        try {
            xTransStatus =  transService.doResumeTrans(xTrans);
        } catch (XtlExceptions e) {
            String msg = e.getMessage() ;
            return BaseResult.fail(msg) ;
        }

        return new UniformReponseHandler<>()
                .sendSuccessResponse("转换恢复运行成功,状态:"+xTransStatus.description());

    }

        @RequestMapping(value = "/startTrans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("远程启动转换")
    public BaseResult startTrans(
            @RequestParam(name = "transId",required = true) String transId) {

        if(StringUtils.isEmpty(transId)){
            throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
        }

        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }

        // 已提交的任务,禁止提交,等执行完毕才能提交
//        boolean isExists = xTransSubmit.isExistTask(xTrans) ;
//        if(isExists){
//            return new UniformReponseHandler<>().sendErrorResponse_System(new XtlExceptions("该任务已经在运行了,请勿重复提交!"));
//        }
                //将start作为任务提交到线程池队列中,.并将status状态设为pending
                Map param = new HashMap() ;
        int currentTasks=0;
        try
            {
                param.put("transId", xTrans.getTransId());
                param.put("monitorType", "trans");
                String isMonitoredEnabled =  xTrans.getIsMonitorEnabled();
                if(StringUtils.isNotEmpty(isMonitoredEnabled) && isMonitoredEnabled.equals("1")){
                    logService.doAddMonitor(param);
                }
                xTransSubmit.submit(param,xTrans);
                  currentTasks = xTransSubmit.getCurrentTaskCounts() ;
                log.debug("当前任务数有{}个",currentTasks);
            }catch (UserDefinedException ex ){
                throw  new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION,ex.getMessage());
            }


        return new UniformReponseHandler<>().sendSuccessResponse("任务提交成功,队列中总共有"+currentTasks+"个任务");

    }

    @RequestMapping(value = "/addTrans2Sche")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("添加转换定时调度")
    public BaseResult addTrans2Sche(
            @RequestParam(name = "transId",required = true) String transId,
            @RequestParam(name = "cron", required = true) String cron,
            @RequestParam(name = "description", required = true) String description) {

        if (StringUtils.isEmpty(transId)) {
            throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
        }

        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }

        //查看当前定时队列中是否有此定时任务
        SchedulerFactory schedulerFactory = QuartzManager.schedulerFactory ;
        try {
            Scheduler scheduler = schedulerFactory.getScheduler() ;
            Map param = Constant.getQuartzBasic(xTrans.getTransName(), xTrans.getTransPath());
            JobKey jobKey = JobKey.jobKey(param.get("jobName")+"",
                    param.get("jobGroupName")+"");
            boolean exists =  scheduler.checkExists(jobKey);
            if(exists){
                return  new BaseResult(11002, "定时队列中已存在该任务,任务名称为["+jobKey.getName()+"]", null);
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        XQuartzExample xQuartzExample = new XQuartzExample();
        xQuartzExample.createCriteria().andTargetIdEqualTo(transId)
                .andQuartzTypeEqualTo("trans");
        XQuartz xQuartz = xQuartzService.selectFirstExample(xQuartzExample) ;
        if(xQuartz !=null ){
            xQuartz.setQuartzCron(cron);
            xQuartz.setQuartzDescription(description);
            xQuartz.setIsDel("0");
            xQuartzService.updateByExampleSelective(xQuartz, xQuartzExample);
        }else {
            xQuartz = new XQuartz() ;
            xQuartz.setQuartzId(GenCodeUtil.nextId());
            xQuartz.setTargetId(transId);
            xQuartz.setQuartzType("trans");
            xQuartz.setQuartzCron(cron);
            xQuartz.setQuartzDescription(description);
            xQuartz.setIsDel("0");
            xQuartzService.insertSelective(xQuartz) ;
            log.debug("定时规则更新成功!");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        try
        {
            xQuartHandleService.addTransToSche(xTrans);

        }catch (UserDefinedException ex )
        {
            throw  new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION);
        }

        return new UniformReponseHandler<>()
                .sendSuccessResponse("任务添加定时队列成功!");

    }

    @RequestMapping(value = "/removeTransFromSche")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("移除转换定时任务")
    public BaseResult removeTransFromSche(
            @RequestParam(name = "transId", required = true) String transId) {
        if (StringUtils.isEmpty(transId)) {
            throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
        }

        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }

        try {
            xQuartHandleService.removeTransFromSche(xTrans);
        }catch (UserDefinedException ex ){
            throw new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION,"定时任务移除失败");
        }

        return new UniformReponseHandler<>()
                .sendSuccessResponse("转换任务已经从定时器队列中移除!");

    }


    @RequestMapping(value = "/stopTrans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("强行停止转换")
    public BaseResult stopTrans(
            @RequestParam(name = "transId", required = true) String transId) {
        if (StringUtils.isEmpty(transId)) {
        throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
    }

        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }

        XTransStatus xTransStatus =null ;
        try {
            xTransStatus =  transService.doStopTrans(xTrans);
        }catch (XtlExceptions e) {
            String msg = e.getMessage() ;
            return BaseResult.fail(msg) ;
        }

        return new UniformReponseHandler<>()
                .sendSuccessResponse("转换停止成功,状态:"+xTransStatus.description());
    }

    @RequestMapping(value = "/killTrans")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId" + "", value = "transId" + "", required = true, dataTypeClass = String.class),
    })
    @ApiOperation("强行终止转换")
    public BaseResult killTrans(
            @RequestParam(name = "transId", required = true) String transId) {
        if (StringUtils.isEmpty(transId)) {
            throw new UserDefinedException(BaseResultConstant.PARAMETER_EXCEPTION);
        }
        XTransExample xTransExample  = new XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample);
        if(xTrans ==null){
            return BaseResult.fail("转换不存在!");
        }
        XTransStatus xTransStatus = null ;
        try {
            xTransStatus =  transService.doKillTrans(xTrans);
        } catch (XtlExceptions e) {
            String msg = e.getMessage() ;
            return BaseResult.fail(msg) ;
        }

        return new UniformReponseHandler<>()
                .sendSuccessResponse("转换终止成功,状态:"+xTransStatus.description());
    }


}
