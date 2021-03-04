package org.yaukie.frame.autocode.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.frame.autocode.model.XDict;
import org.yaukie.frame.autocode.model.XDictExample;
import org.yaukie.frame.autocode.service.api.XDictService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuenbin
 * @create: 2020/12/18 21/13/801
 **/
@RestController
@RequestMapping(value = "/op/xdict/")
@Api(value = "XDict控制器", description = "XDict管理")
@Slf4j
public class XDictController extends BaseController {

    @Autowired
    private XDictService xDictService;

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
    @EnablePage
    public BaseResult getDictPageList(
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "search", required = false) String search) {
        XDictExample xDictExample = new XDictExample();
//    if(StringUtils.isNotBlank(search)){
//        xDictExample.createCriteria().andUserIdEqualTo(search);
//    }
        List<XDict> xDictList = this.xDictService.selectByExample(xDictExample);
        PageResult pageResult = new PageResult(xDictList);
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT_ROWS, pageResult.getRows());
        result.put(RESULT_TOTLAL, pageResult.getTotal());
        return BaseResult.success(result);
    }

    @GetMapping(value = "/get/{dictId}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictId", value = "dictId", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("获取信息")
    public BaseResult getDict(@PathVariable String dictId) {
        XDictExample xDictExample = new XDictExample();
        xDictExample.createCriteria().andDictIdEqualTo(dictId);
        List<XDict> dicts = xDictService.selectByExample(xDictExample);
        return BaseResult.success(dicts);
    }

    @PostMapping(value = "/add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params" + "", value = "params" + "",
                    required = true, dataTypeClass = Map.class),
    })
    @ApiOperation("新增字典")
    public BaseResult addParams(@RequestBody @Validated Map params) {
        Object datas = params.get("datas");
        JSONArray json = JSONArray.fromObject(datas);
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) JSONArray.toCollection(json, Map.class);

        if (CollectionUtils.isEmpty(dataList)) {
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("字典列表不能为空"));
        }
        dataList.forEach(item -> {
            XDict xDict = new XDict();
            xDict.setDictId(params.get("dictId") + "");
            xDict.setDictValue(item.get("dictValue") + "");
            xDict.setDictName(params.get("dictName") + "");
            xDict.setDictKey(item.get("dictKey") + "");
            xDict.setDictDesc(item.get("dictDesc") + "");
            xDict.setOrderNum(item.get("orderNum") + "");
            xDictService.insertSelective(xDict);
        });
        return BaseResult.success();
    }

    @PostMapping(value = "/update")
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params" + "", value = "params" + "",
                    required = true, dataTypeClass = Map.class),
    })
    public BaseResult updateDict(@RequestBody @Validated Map params, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }

        XDictExample xDictExample = new XDictExample();
        xDictExample.createCriteria().andDictIdEqualTo(params.get("dictId") + "");
        xDictService.deleteByExample(xDictExample);

        Object datas = params.get("datas");
        JSONArray json = JSONArray.fromObject(datas);
        List<Map<String, Object>> dataList = (List<Map<String, Object>>) JSONArray.toCollection(json, Map.class);

        if (CollectionUtils.isEmpty(dataList)) {
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("字典列表不能为空"));
        }
        dataList.forEach(item -> {
            XDict xDict = new XDict();
            xDict.setDictId(params.get("dictId") + "");
            xDict.setDictValue(item.get("dictValue") + "");
            xDict.setDictName(params.get("dictName") + "");
            xDict.setDictKey(item.get("dictKey") + "");
            xDict.setDictDesc(item.get("dictDesc") + "");
            xDict.setOrderNum(item.get("orderNum") + "");
            xDictService.insertSelective(xDict);
        });

        return BaseResult.success();
    }

    @GetMapping(value = "/delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "dictId", value = "dictId", required = true, dataType = "string"),
            @ApiImplicitParam(name = "dictKey", value = "dictKey", required = true, dataType = "string"),
    })
    public BaseResult deleteDict(@RequestParam String dictId,
                                 @RequestParam String dictKey) {
        XDictExample xDictExample = new XDictExample();
        xDictExample.createCriteria().andDictIdEqualTo(dictId)
                .andDictKeyEqualTo(dictKey) ;
        this.xDictService.deleteByExample(xDictExample);
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
