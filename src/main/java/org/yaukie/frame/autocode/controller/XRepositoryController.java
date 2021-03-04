package org.yaukie.frame.autocode.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.core.util.GenCodeUtil;
import org.yaukie.frame.autocode.model.XRepository;
import org.yaukie.frame.autocode.model.XRepositoryExample;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.yaukie.xtl.KettleUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author: yuenbin
* @create: 2020/11/23 19/21/670
**/
@RestController
@RequestMapping(value = "/op/xrepository/")
@Api(value = "XRepository控制器", description = "XRepository管理")
@Slf4j
public class XRepositoryController  extends BaseController {

    @Autowired
    private XRepositoryService xRepositoryService;

    @GetMapping(value = "/delRepoDir")
    @ApiOperation("删除资源库目录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "path", value = "path", required = true, dataType = "string" ),
            @ApiImplicitParam(name = "repoId", value = "repoId", required = true, dataType = "string" ),
    })
    public BaseResult delRepoDir(@RequestParam String path,
                                 @RequestParam String repoId) {
        if(StringUtils.isEmpty(repoId)){
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("请指定资源库"));
        }
        XRepositoryExample xRepositoryExample = new XRepositoryExample() ;
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repoId) ;
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample) ;
        Repository  repository = null ;
        try {
            if(xRepository.getType().equals("db")){
                repository =  KettleUtil.conByNative(xRepository.getRepoId(),xRepository.getRepoName(),
                        xRepository.getRepoName(),xRepository.getRepoType(),
                        xRepository.getDbHost(),xRepository.getDbPort(),xRepository.getDbName(),
                        xRepository.getDbUsername(),xRepository.getDbPassword(),xRepository.getRepoUsername(),
                        xRepository.getRepoPassword()) ;

                repository.deleteRepositoryDirectory(repository.findDirectory(path));
                 log.debug("目录{}已成功从数据库资源库删除!", path);
            }else {
                repository = KettleUtil.conFileRep(xRepository.getRepoId(), xRepository.getRepoName(),
                        xRepository.getBaseDir());
                KettleFileRepository kettleFileRepository = (KettleFileRepository) repository;
                kettleFileRepository.deleteRepositoryDirectory(kettleFileRepository.findDirectory(path));
                log.debug("目录{}已成功从文件库资源库删除!", path);
            }
        }catch (KettleException ex )
        {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }
        return BaseResult.success() ;
    }

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
   @EnablePage
     public BaseResult getRepositoryPageList(
                                        @RequestParam(value = "offset",required = false)String offset,
                                        @RequestParam(value = "limit",required = false)String limit,
                                         @RequestParam(value = "search",required = false)String search) {
XRepositoryExample xRepositoryExample = new XRepositoryExample();

     List<XRepository> xRepositoryList = this.xRepositoryService.selectByExample(xRepositoryExample);
               PageResult pageResult = new PageResult(xRepositoryList);
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
                public BaseResult getRepository(@PathVariable String id) {
                XRepositoryExample xRepositoryExample = new XRepositoryExample();
                    xRepositoryExample.createCriteria().andRepoIdEqualTo(id) ;
                    XRepository xRepository = this.xRepositoryService.selectFirstExample(xRepositoryExample);
                    return BaseResult.success(xRepository);
                    }

                    @PostMapping(value = "/add")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "xRepository"+"", value = "xRepository"+"",
                    required = true,dataTypeClass =XRepository.class),
                    })
                    @ApiOperation("新增")
                    public BaseResult addRepository(@RequestBody @Validated XRepository xRepository,BindingResult BindingResult) {
                        if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                        }
                        String repoId = GenCodeUtil.nextId() ;
                        xRepository.setRepoId(repoId);
                        int affect = this.xRepositoryService.insertSelective(xRepository);
                        if(affect > 0 ){
                            //去kettle中创建资源库
                            log.debug("资源库创建成功!");

                        }
                        return BaseResult.success();
                        }

                        @PostMapping(value = "/update")
                        @ApiOperation("更新")
                        @ApiImplicitParams({
                        @ApiImplicitParam(name = "xRepository"+"", value = "xRepository"+"",
                            required = true,dataTypeClass =XRepository.class),
                        })
                        public BaseResult updateRepository(@RequestBody @Validated XRepository xRepository, BindingResult BindingResult) {
                            if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                            }
                            XRepositoryExample xRepositoryExample = new XRepositoryExample() ;
                            xRepositoryExample.createCriteria().andRepoIdEqualTo(xRepository.getRepoId()) ;
                            int affect = xRepositoryService.updateByExampleSelective(xRepository, xRepositoryExample);
                            return BaseResult.success();
                            }

                            @GetMapping(value = "/delete/{id}")
                            @ApiOperation("删除")
                              @ApiImplicitParams({
                            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string" ),
                            })
                            public BaseResult deleteRepository(@PathVariable String id) {
                                XRepositoryExample xRepositoryExample = new  XRepositoryExample();
                                 this.xRepositoryService.deleteByExample(xRepositoryExample);
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
