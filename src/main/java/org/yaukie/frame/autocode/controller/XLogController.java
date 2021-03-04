package org.yaukie.frame.autocode.controller;

import com.atomikos.util.DateHelper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Value;
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
import org.yaukie.frame.autocode.service.api.XLogService;
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
import org.yaukie.frame.kettle.service.JobService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
* @author: yuenbin
* @create: 2020/11/11 14/28/274
**/
@RestController
@RequestMapping(value = "/op/xlog/")
@Api(value = "XLog控制器", description = "XLog管理")
@Slf4j
public class XLogController  extends BaseController {

    @Autowired
    private XLogService xLogService;

    @Autowired
    private XJobService xJobService;

    @Autowired
    private XTransService xTransService;

    @Resource
    private HttpServletResponse response;

    @Value("${kettle.log.file.path}")
    private String logFilePath ;

    @Autowired
    private ExtendMapper extendMapper ;

    @RequestMapping(value = "/downLog/{logId}",  produces = "application/json;charset=UTF-8")
    @ResponseBody
    public BaseResult downLog(@PathVariable String logId)  {
        //优先从硬盘找文件,如果找不到则从数据库中取日志文件
        Map params = new HashMap() ;
        params.put("logId",logId );
        Map dataMap = extendMapper.qryLog(params);
        String logType =dataMap.get("logType")+"";
        String targetId = dataMap.get("targetId")+"" ;
        String msg=dataMap.get("logText")+"" ;
        String name ="未知任务" ;
        if(logType.equals("job")){
            XJobExample xJobExample = new XJobExample() ;
            xJobExample.createCriteria().andJobIdEqualTo(targetId);
            XJob xJob = xJobService.selectFirstExample(xJobExample);
            if(xJob ==null ){
                throw new UserDefinedException(BaseResultConstant.METHOD_EXCEPTION);
            }
            name = xJob.getJobName() ;
         }else {
            XTransExample xTransExample = new XTransExample() ;
            xTransExample.createCriteria().andTransIdEqualTo(targetId);
            XTrans xTrans = xTransService.selectFirstExample(xTransExample);
            if(xTrans ==null ){
                throw new UserDefinedException(BaseResultConstant.METHOD_EXCEPTION);
            }
          name = xTrans.getTransName() ;
         }
        //日志文件绝对路径
        String absPath = dataMap.get("logFilePath")+"";
        Date date = null ;
        try {
            date = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").parse(dataMap.get("startTime")+"") ;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String filename = (logType.equalsIgnoreCase("job")?"作业-":"转换-" )+name+"-"+new SimpleDateFormat("yyyyMMddHHmmss").format(date)+ "-.log";
        // 配置文件下载
        response.setHeader("content-type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        // 下载文件能正常显示中文
        try {
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        File file = new File(absPath);
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os =null ;
        // 实现文件下载
        byte[] buffer = new byte[1024];
            // 如果文件存在，则进行下载
            if (file.exists()) {
                log.info("日志从硬盘下载....");
                try {

                    fis = new FileInputStream(file);
                    bis = new BufferedInputStream(fis);
                      os = response.getOutputStream();
                    int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }

                } catch (Exception e) {
                    return new UniformReponseHandler().sendErrorResponse_System(e) ;
                 } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                log.info("日志从数据库下载....");
                try {
                      os = response.getOutputStream();
                     //整个临时文件
                    String dir = logFilePath + File.separator;
                     File fDir = new File(dir);
                    if (!fDir.exists()) {
                         fDir.mkdirs();
                     }
                    File tmpFile = new File(dir,new SimpleDateFormat("yyyyMMddHHmmss").format(date)+".txt");
                    FileUtils.writeStringToFile(tmpFile, msg,"UTF-8" );
                    Thread.sleep(200);
                    fis = new FileInputStream(tmpFile);
                    bis = new BufferedInputStream(fis);
                     int i = bis.read(buffer);
                    while (i != -1) {
                        os.write(buffer, 0, i);
                        i = bis.read(buffer);
                    }
                    os.flush();
                    os.close();
                    //删除临时文件
                    FileUtils.forceDelete(tmpFile);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (bis != null) {
                        try {
                            bis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (fis != null) {
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }

        return  BaseResult.success("下载成功!") ;
    }
    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
   @EnablePage
     public BaseResult getLogPageList(
                                        @RequestParam(value = "offset",required = false)String offset,
                                        @RequestParam(value = "limit",required = false)String limit,
                                        @RequestParam(value = "jobName",required = false)String jobName,
                                        @RequestParam(value = "targetResult",required = false)String targetResult,
                                         @RequestParam(value = "createDateBegin",required = false)String createDateBegin,
                                        @RequestParam(value = "createDateEnd",required = false)String createDateEnd) {
        Map params = new HashMap() ;
                                  if(StringUtils.isNotEmpty(jobName)){
                                      params.put("jobName",jobName );
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
     List<Map> xLogList =extendMapper.qryLogInfo(params) ;
               PageResult pageResult = new PageResult(xLogList);
                Map<String, Object> result = new HashMap<>();
                result.put(RESULT_ROWS, pageResult.getRows());
                result.put(RESULT_TOTLAL, pageResult.getTotal());
                return BaseResult.success( result);
                }

                @GetMapping(value = "/get/{id}")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string",paramType = "header")
                    })
               @ApiOperation("获取信息")
                public BaseResult getLog(@PathVariable String id) {
                XLog xLog = this.xLogService.selectByPrimaryKey(Integer.parseInt(id));
                    return BaseResult.success(xLog);
                    }

                    @PostMapping(value = "/add")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "xLog"+"", value = "xLog"+"",
                    required = true,dataTypeClass =XLog.class),
                    })
                    @ApiOperation("新增")
                    public BaseResult addLog(@RequestBody @Validated XLog xLog, BindingResult BindingResult) {
                        if (BindingResult.hasErrors()) {
                        return this.getErrorMessage(BindingResult);
                        }
                        this.xLogService.insertSelective(xLog);
                        return BaseResult.success();
                        }

                        @PostMapping(value = "/update")
                        @ApiOperation("更新")
                        @ApiImplicitParams({
                        @ApiImplicitParam(name = "xLog"+"", value = "xLog"+"",
                            required = true,dataTypeClass =XLog.class),
                        })
                        public BaseResult updateLog(@RequestBody @Validated XLog xLog, BindingResult BindingResult) {
                            if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                            }

                            this.xLogService.updateByPrimaryKey(xLog);
                            return BaseResult.success();
                            }

                            @GetMapping(value = "/delete")
                            @ApiOperation("删除")
                              @ApiImplicitParams({
                            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "string" ),
                            })
                            public BaseResult deleteLog(@RequestParam String ids) {
                                String[] logIds = ids.split(",");
                                XLogExample xLogExample = new  XLogExample();
                                XLogExample.Criteria criteria = xLogExample.createCriteria();
                                List<String> list = Arrays.asList(logIds) ;
                                criteria.andLogIdIn(list);
                                this.xLogService.deleteByExample(xLogExample);
                                return BaseResult.success();
                                }

                                public BaseResult getErrorMessage(BindingResult BindingResult){
                                    String errorMessage = "";
                                    for (ObjectError objectError : BindingResult.getAllErrors()) {
                                    errorMessage += objectError.getDefaultMessage();
                                    }
                                    return BaseResult.fail(errorMessage);
                                    }
        }
