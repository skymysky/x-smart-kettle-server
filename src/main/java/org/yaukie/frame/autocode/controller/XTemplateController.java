package org.yaukie.frame.autocode.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.annotation.LogAround;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.frame.autocode.service.api.XTemplateService;
import org.yaukie.frame.autocode.model.XTemplate;
import org.yaukie.frame.autocode.model.XTemplateExample;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import org.yaukie.xtl.KettleUtil;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuenbin
 * @create: 2021/02/18 16/13/964
 **/
@RestController
@RequestMapping(value = "/op/xtemplate/")
@Api(value = "XTemplate控制器", description = "XTemplate管理")
@Slf4j
public class XTemplateController extends BaseController {

    @Autowired
    private XTemplateService xTemplateService;

    @Resource
    private HttpServletResponse response ;

    @GetMapping(value = "/getModelImage",produces = "application/json")
    @ApiOperation("获取模板调度图")
    @LogAround("获取模板调度图")
    public BaseResult getModelImage(
            @RequestParam(value = "tplKey",required = true)String tplKey
    )   {
        if(StringUtils.isEmpty(tplKey)){
            return BaseResult.fail();
        }
        XTemplateExample xTemplateExample = new XTemplateExample() ;
        xTemplateExample.createCriteria().andTemplateKeyEqualTo(tplKey);
        XTemplate xTemplate = xTemplateService.selectFirstExample(xTemplateExample) ;
        if(null == xTemplate){
            return BaseResult.fail() ;
        }
        OutputStream os =null ;
        try {
            String rootPath =Thread.currentThread().getContextClassLoader()
                    .getResource("").getPath();
            rootPath=rootPath.substring(1);
            JobMeta jobMeta = new JobMeta(rootPath+xTemplate.getTemplatePath(), null) ;

            BufferedImage bufferedImage = KettleUtil.generateJobImage(jobMeta);

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


    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
    @EnablePage
    public BaseResult getTemplatePageList(
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "search", required = false) String search) {
        XTemplateExample xTemplateExample = new XTemplateExample();
//    if(StringUtils.isNotBlank(search)){
//        xTemplateExample.createCriteria().andUserIdEqualTo(search);
//    }
        List<XTemplate> xTemplateList = this.xTemplateService.selectByExample(xTemplateExample);
        PageResult pageResult = new PageResult(xTemplateList);
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
    public BaseResult getTemplate(@PathVariable String id) {
        XTemplate xTemplate = this.xTemplateService.selectByPrimaryKey(Integer.parseInt(id));
        return BaseResult.success(xTemplate);
    }

    @PostMapping(value = "/add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "xTemplate" + "", value = "xTemplate" + "",
                    required = true, dataTypeClass = XTemplate.class),
    })
    @ApiOperation("新增")
    public BaseResult addTemplate(@RequestBody @Validated XTemplate xTemplate, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }
        this.xTemplateService.insertSelective(xTemplate);
        return BaseResult.success();
    }

    @PostMapping(value = "/update")
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "xTemplate" + "", value = "xTemplate" + "",
                    required = true, dataTypeClass = XTemplate.class),
    })
    public BaseResult updateTemplate(@RequestBody @Validated XTemplate xTemplate, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }

        this.xTemplateService.updateByPrimaryKey(xTemplate);
        return BaseResult.success();
    }

    @GetMapping(value = "/delete/{id}")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string"),
    })
    public BaseResult deleteTemplate(@PathVariable String id) {
        XTemplateExample xTemplateExample = new XTemplateExample();
        // xTemplateExample.createCriteria().andIdEqualsTo(id);
        this.xTemplateService.deleteByExample(xTemplateExample);
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
