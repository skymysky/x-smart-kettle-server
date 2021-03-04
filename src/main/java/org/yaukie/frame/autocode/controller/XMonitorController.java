package org.yaukie.frame.autocode.controller;

import lombok.extern.slf4j.Slf4j;

 import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.frame.autocode.service.api.XMonitorService;
import org.yaukie.frame.autocode.model.XMonitor;
import org.yaukie.frame.autocode.model.XMonitorExample;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
 import io.swagger.annotations.Api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author: yuenbin
* @create: 2021/01/20 14/27/503
**/
@RestController
@RequestMapping(value = "/op/xmonitor/")
@Api(value = "XMonitor控制器", description = "XMonitor管理")
@Slf4j
public class XMonitorController  extends BaseController {

    @Autowired
    private XMonitorService xMonitorService;

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
   @EnablePage
     public BaseResult getMonitorPageList(
                                        @RequestParam(value = "offset",required = false)String offset,
                                        @RequestParam(value = "limit",required = false)String limit,
                                         @RequestParam(value = "search",required = false)String search) {
XMonitorExample xMonitorExample = new XMonitorExample();
//    if(StringUtils.isNotBlank(search)){
//        xMonitorExample.createCriteria().andUserIdEqualTo(search);
//    }
     List<XMonitor> xMonitorList = this.xMonitorService.selectByExample(xMonitorExample);
               PageResult pageResult = new PageResult(xMonitorList);
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
                public BaseResult getMonitor(@PathVariable String id) {
                XMonitor xMonitor = this.xMonitorService.selectByPrimaryKey(Integer.parseInt(id));
                    return BaseResult.success(xMonitor);
                    }

                    @PostMapping(value = "/add")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "xMonitor"+"", value = "xMonitor"+"",
                    required = true,dataTypeClass =XMonitor.class),
                    })
                    @ApiOperation("新增")
                    public BaseResult addMonitor(@RequestBody @Validated XMonitor xMonitor, BindingResult BindingResult) {
                        if (BindingResult.hasErrors()) {
                        return this.getErrorMessage(BindingResult);
                        }
                        this.xMonitorService.insertSelective(xMonitor);
                        return BaseResult.success();
                        }

                        @PostMapping(value = "/update")
                        @ApiOperation("更新")
                        @ApiImplicitParams({
                        @ApiImplicitParam(name = "xMonitor"+"", value = "xMonitor"+"",
                            required = true,dataTypeClass =XMonitor.class),
                        })
                        public BaseResult updateMonitor(@RequestBody @Validated XMonitor xMonitor, BindingResult BindingResult) {
                            if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                            }

                            this.xMonitorService.updateByPrimaryKey(xMonitor);
                            return BaseResult.success();
                            }

                            @GetMapping(value = "/delete/{id}")
                            @ApiOperation("删除")
                              @ApiImplicitParams({
                            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string" ),
                            })
                            public BaseResult deleteMonitor(@PathVariable String id) {
                                XMonitorExample xMonitorExample = new  XMonitorExample();
                               // xMonitorExample.createCriteria().andIdEqualsTo(id);
                                this.xMonitorService.deleteByExample(xMonitorExample);
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
