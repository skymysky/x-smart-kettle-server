package org.yaukie.frame.autocode.controller;

import lombok.extern.slf4j.Slf4j;

import net.sf.json.JSONArray;
import org.springframework.util.CollectionUtils;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.frame.autocode.service.api.XParamsService;
import org.yaukie.frame.autocode.model.XParams;
import org.yaukie.frame.autocode.model.XParamsExample;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author: yuenbin
 * @create: 2021/01/21 15/34/412
 **/
@RestController
@RequestMapping(value = "/op/xparams/")
@Api(value = "XParams控制器", description = "XParams管理")
@Slf4j
public class XParamsController extends BaseController {

    @Autowired
    private XParamsService xParamsService;


    @GetMapping(value = "/getParams")
    @ApiOperation("获取参数详情")
    @EnablePage
    public BaseResult getParams(@RequestParam(value = "targetId", required = false) String targetId,
                                @RequestParam(value = "targetType", required = false) String targetType) {
        XParamsExample xParamsExample = new XParamsExample();
        xParamsExample.createCriteria()
                .andTargetIdEqualTo(targetId)
                .andTargetTypeEqualTo(targetType);
        List<XParams> dataList = xParamsService.selectByExample(xParamsExample);
        return BaseResult.success(dataList);

    }

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
    @EnablePage
    public BaseResult getParamsPageList(
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "search", required = false) String search) {
        XParamsExample xParamsExample = new XParamsExample();
//    if(StringUtils.isNotBlank(search)){
//        xParamsExample.createCriteria().andUserIdEqualTo(search);
//    }
        List<XParams> xParamsList = this.xParamsService.selectByExample(xParamsExample);
        PageResult pageResult = new PageResult(xParamsList);
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT_ROWS, pageResult.getRows());
        result.put(RESULT_TOTLAL, pageResult.getTotal());
        return BaseResult.success(result);
    }

    @GetMapping(value = "/get/{id}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("获取信息")
    public BaseResult getParams(@PathVariable String id) {
        XParams xParams = this.xParamsService.selectByPrimaryKey(Integer.parseInt(id));
        return BaseResult.success(xParams);
    }

    @PostMapping(value = "/add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params" + "", value = "params" + "",
                    required = true, dataTypeClass = Map.class),
    })
    @ApiOperation("新增參數")
    public BaseResult addParams(@RequestBody @Validated Map params) {

        String targetId = params.get("targetId") + "";
        String targetType = params.get("targetType") + "";
        //先刪除原有的參數
        XParamsExample xParamsExample = new XParamsExample();
        xParamsExample.createCriteria().andTargetIdEqualTo(targetId)
                .andTargetTypeEqualTo(targetType);
        xParamsService.deleteByExample(xParamsExample) ;

        Object datas = params.get("datas");
        JSONArray json = JSONArray.fromObject(datas);
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) JSONArray.toCollection(json, Map.class);
        //针对list中的map 先做去重处理
        if(CollectionUtils.isEmpty(dataList)){
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("参数列表不能为空")) ;
        }

        List<Map<String,Object>> resultList = new ArrayList<>() ;
        List<Map<String,Object>> tmpList = new ArrayList<>() ;

        for (int i=0;i<dataList.size();i++)
       {
           Map tmpMap1 = dataList.get(i) ;
           String objCode = tmpMap1.get("objCode")+"" ;
           for(int j=0;j<dataList.size();j++){
               Map tmpMap2 = dataList.get(j) ;
               String tmpObjCode = tmpMap2.get("objCode")+"";
               if(i == j){
                   continue;
               }
               if(tmpObjCode.equals(objCode)){
                   dataList.remove(j);
                   j--;

               }
           }
       }

        dataList.forEach(item -> {
            XParams xParams = new XParams();
            xParams.setObjCode(item.get("objCode") + "");
            xParams.setObjName(item.get("objName") + "");
            xParams.setObjVal(item.get("objVal") + "");
            xParams.setObjDes(item.get("objDes") + "");
            xParams.setTargetId(params.get("targetId") + "");
            xParams.setTargetType(params.get("targetType") + "");
            xParams.setIsUse("1");
            xParamsService.insertSelective(xParams);
        });

        return BaseResult.success();

    }

    @PostMapping(value = "/update")
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "xParams" + "", value = "xParams" + "",
                    required = true, dataTypeClass = XParams.class),
    })
    public BaseResult updateParams(@RequestBody @Validated XParams xParams, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }

        this.xParamsService.updateByPrimaryKey(xParams);
        return BaseResult.success();
    }

    @GetMapping(value = "/delete/{id}")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string"),
    })
    public BaseResult deleteParams(@PathVariable String id) {
        XParamsExample xParamsExample = new XParamsExample();
        // xParamsExample.createCriteria().andIdEqualsTo(id);
        this.xParamsService.deleteByExample(xParamsExample);
        return BaseResult.success();
    }

    public BaseResult getErrorMessage(BindingResult BindingResult) {
        String errorMessage = "";
        for (ObjectError objectError : BindingResult.getAllErrors()) {
            errorMessage += objectError.getDefaultMessage();
        }
        return BaseResult.fail(errorMessage);
    }
}
