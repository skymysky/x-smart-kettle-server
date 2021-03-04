package org.yaukie.frame.autocode.controller;

import com.atomikos.util.DateHelper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.httpclient.util.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.frame.autocode.dao.mapper.ExtendMapper;
import org.yaukie.frame.autocode.service.api.XLogWarningService;
import org.yaukie.frame.autocode.model.XLogWarning;
import org.yaukie.frame.autocode.model.XLogWarningExample;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
 import io.swagger.annotations.Api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* @author: yuenbin
* @create: 2021/01/18 15/58/594
**/
@RestController
@RequestMapping(value = "/op/xlogwarning/")
@Api(value = "XLogWarning控制器", description = "XLogWarning管理")
@Slf4j
public class XLogWarningController  extends BaseController {

    @Autowired
    private XLogWarningService xLogWarningService;

    @Autowired
    private ExtendMapper extendMapper ;

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
   @EnablePage
     public BaseResult getLogPageList(
                                        @RequestParam(value = "offset",required = false)String offset,
                                        @RequestParam(value = "limit",required = false)String limit,
                                        @RequestParam(value = "jobName",required = false)String jobName,
                                         @RequestParam(value = "createDateBegin",required = false)String createDateBegin,
                                        @RequestParam(value = "createDateEnd",required = false)String createDateEnd) {
        Map params = new HashMap() ;
        if(StringUtils.isNotEmpty(jobName)){
            params.put("jobName",jobName );
        }

        if(StringUtils.isNotEmpty(createDateBegin)){

                params.put("startTime", createDateBegin);

        }else {
            params.put("startTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        }
        if(StringUtils.isNotEmpty(createDateEnd)){

                params.put("stopTime", createDateEnd);

        }else {
            params.put("stopTime", DateUtil.formatDate(new Date(), "yyyy-MM-dd"));
        }
     List<Map> xLogWarningList =  extendMapper.qryLogWarning(params) ;
               PageResult pageResult = new PageResult(xLogWarningList);
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
                XLogWarning xLogWarning = this.xLogWarningService.selectByPrimaryKey(Integer.parseInt(id));
                    return BaseResult.success(xLogWarning);
                    }

                    @PostMapping(value = "/add")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "xLogWarning"+"", value = "xLogWarning"+"",
                    required = true,dataTypeClass =XLogWarning.class),
                    })
                    @ApiOperation("新增")
                    public BaseResult addLog(@RequestBody @Validated XLogWarning xLogWarning, BindingResult BindingResult) {
                        if (BindingResult.hasErrors()) {
                        return this.getErrorMessage(BindingResult);
                        }
                        this.xLogWarningService.insertSelective(xLogWarning);
                        return BaseResult.success();
                        }

                        @PostMapping(value = "/update")
                        @ApiOperation("更新")
                        @ApiImplicitParams({
                        @ApiImplicitParam(name = "xLogWarning"+"", value = "xLogWarning"+"",
                            required = true,dataTypeClass =XLogWarning.class),
                        })
                        public BaseResult updateLog(@RequestBody @Validated XLogWarning xLogWarning, BindingResult BindingResult) {
                            if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                            }

                            this.xLogWarningService.updateByPrimaryKey(xLogWarning);
                            return BaseResult.success();
                            }

                            @GetMapping(value = "/delete")
                            @ApiOperation("删除")
                              @ApiImplicitParams({
                            @ApiImplicitParam(name = "ids", value = "ids", required = true, dataType = "string" ),
                            })
                            public BaseResult deleteLog(@RequestParam String ids) {
        String[] logIds = ids.split(",");
                                XLogWarningExample xLogWarningExample = new  XLogWarningExample();
                                XLogWarningExample.Criteria criteria = xLogWarningExample.createCriteria();
                                List<String> list = Arrays.asList(logIds) ;
                                criteria.andLogIdIn(list);
                                this.xLogWarningService.deleteByExample(xLogWarningExample);
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
