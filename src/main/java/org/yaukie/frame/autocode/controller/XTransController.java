package org.yaukie.frame.autocode.controller;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.pentaho.di.trans.TransMeta;
import org.springframework.util.CollectionUtils;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.annotation.LogAround;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.BaseResultConstant;
import org.yaukie.core.constant.PageResult;
import org.yaukie.core.exception.UserDefinedException;
import org.yaukie.frame.autocode.model.XRepository;
import org.yaukie.frame.autocode.model.XRepositoryExample;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.yaukie.frame.autocode.service.api.XTransService;
import org.yaukie.frame.autocode.model.XTrans;
import org.yaukie.frame.autocode.model.XTransExample;
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
import org.yaukie.xtl.exceptions.XtlExceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author: yuenbin
* @create: 2020/11/16 10/39/381
**/
@RestController
@RequestMapping(value = "/op/xtrans/")
@Api(value = "XTrans控制器", description = "XTrans管理")
@Slf4j
public class XTransController  extends BaseController {

    @Autowired
    private XTransService xTransService;
    
    @Autowired
    private XRepositoryService xRepositoryService ;
    

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
   @EnablePage
     public BaseResult getTransPageList(
                                        @RequestParam(value = "offset",required = false)String offset,
                                        @RequestParam(value = "limit",required = false)String limit,
                                         @RequestParam(value = "search",required = false)String search) {
XTransExample xTransExample = new XTransExample();
//    if(StringUtils.isNotBlank(search)){
//        xTransExample.createCriteria().andUserIdEqualTo(search);
//    }
     List<XTrans> xTransList = this.xTransService.selectByExample(xTransExample);
               PageResult pageResult = new PageResult(xTransList);
                Map<String, Object> result = new HashMap<>();
                result.put(RESULT_ROWS, pageResult.getRows());
                result.put(RESULT_TOTLAL, pageResult.getTotal());
                return BaseResult.success( result);
                }



    @PostMapping(value = "/copy")
    @ApiOperation("复制转换")
    @LogAround("复制转换")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map"+"", value = "map"+"",
                    required = true,dataTypeClass =Map.class),
    })
    public BaseResult copytrans(@RequestBody @Validated Map param, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }
        if(CollectionUtils.isEmpty(param)){
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("转换参数为空"));
        }

        String copyNum = param.get("copyNum")+"" ;
        if(Integer.parseInt(copyNum) > 10 ){
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("复制上限为10个!"));
        }
        //源转换信息
        String transId = param.get("transId")+"";
        XTransExample xTransExample = new XTransExample() ;
        xTransExample.createCriteria().andTransIdEqualTo(transId);
        XTrans xTrans = xTransService.selectFirstExample(xTransExample) ;
        if(xTrans == null ){
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("没有此转换信息!"));
        }

        //来源资源库
        String fromRepoId = xTrans.getTransRepositoryId() ;
        if(StringUtils.isEmpty(fromRepoId)){
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("没有此资源库信息!"));
        }
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(fromRepoId) ;
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        //目标资源库
        String toRepoId = param.get("transRepositoryId")+"";
        xRepositoryExample = new XRepositoryExample() ;
        xRepositoryExample.createCriteria().andRepoIdEqualTo(toRepoId) ;
        XRepository xRepositoryTo =  xRepositoryService.selectFirstExample(xRepositoryExample) ;
        String transName = xTrans.getTransName() ;
        String transPath = xTrans.getTransPath() ;
        String repoType = xTrans.getTransType();
        Repository fromRepository = null ;
        Repository toRepository =null ;
        try {
            if(repoType.equals("file")){
                fromRepository = KettleUtil.conFileRep(xRepository.getRepoId(), xRepository.getRepoName(), xRepository.getBaseDir());
            }else {
                fromRepository = KettleUtil.conByNative(xRepository.getRepoId(),xRepository.getRepoName(),
                        xRepository.getRepoName(),xRepository.getRepoType(),
                        xRepository.getDbHost(),xRepository.getDbPort(),xRepository.getDbName(),
                        xRepository.getDbUsername(),xRepository.getDbPassword(),xRepository.getRepoUsername(),
                        xRepository.getRepoPassword()) ;
            }

            if(toRepoId.equals(fromRepoId)){
                toRepository = fromRepository ;
            }else
            {
                String type = xRepositoryTo.getType() ;
                if(type.equals("file")){
                    toRepository = KettleUtil.conFileRep(xRepositoryTo.getRepoId(), xRepositoryTo.getRepoName(),
                            xRepositoryTo.getBaseDir());
                }else {
                    toRepository = KettleUtil.conByNative(xRepositoryTo.getRepoId(),xRepositoryTo.getRepoName(),
                            xRepositoryTo.getRepoName(),xRepositoryTo.getRepoType(),
                            xRepositoryTo.getDbHost(),xRepositoryTo.getDbPort(),xRepositoryTo.getDbName(),
                            xRepositoryTo.getDbUsername(),xRepositoryTo.getDbPassword(),xRepositoryTo.getRepoUsername(),
                            xRepositoryTo.getRepoPassword()) ;
                }

            }

            log.debug("总共需要复制{}个转换,...",copyNum );
            if(Integer.parseInt(copyNum) > 1){
                String tmpNewName = param.get("newName")+"" ;
                String tmptransPath = param.get("transPath")+"" ;
                for (int i=1;i<=Integer.parseInt(copyNum);i++)
                {
                    param.put("repoId", xRepositoryTo.getRepoId());
                    param.put("newName", tmpNewName+"_"+i+"_cp") ;
                    param.put("newPath", tmptransPath);
                    param.put("transName", xTrans.getTransName());
                    param.put("transPath", xTrans.getTransPath());
                    KettleUtil.transCopy(param,fromRepository ,toRepository );
                    TransMeta transMeta =  KettleUtil.loadTrans(toRepoId,param.get("newName")+"",param.get("newPath")+"");
                    String newtransId = transMeta.getObjectId().getId() ;
                    XTrans target = new XTrans() ;
                    target.setTransId(newtransId);
                    target.setTransName(param.get("newName")+"");
                    target.setTransRepositoryId(toRepoId);
                    target.setTransDescription(param.get("transDescription")+"");
                    String isMonitorEnabled = param.get("isMonitorEnabled")+"";
                    if(isMonitorEnabled.equals("true")){
                        target.setIsMonitorEnabled("1");
                    }else {
                        target.setIsMonitorEnabled("0");
                    }

                    if(StringUtils.isEmpty(param.get("transLogLevel")+"") ||
                            "null".equals(param.get("transLogLevel")+"")){
                        target.setTransLogLevel(LogLevel.DEBUG.getCode());
                    }else {
                        target.setTransLogLevel(param.get("transLogLevel")+"");
                    }

                    target.setIsDel(param.get("isDel")+"");
                    target.setTransPath(param.get("newPath")+"");
                    target.setTransType(xRepositoryTo.getType());
                    xTransService.insertSelective(target);
                    log.debug("第{}个转换复制成功!",i+1);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }else {
                {
                    param.put("repoId", xRepositoryTo.getRepoId());
                    param.put("newPath", param.get("transPath"));
                    param.put("transName", xTrans.getTransName());
                    param.put("transPath", xTrans.getTransPath());
                    KettleUtil.transCopy(param,fromRepository ,toRepository );
                    TransMeta transMeta =  KettleUtil.loadTrans(toRepoId,param.get("newName")+"",param.get("newPath")+"");
                    String newtransId = transMeta.getObjectId().getId() ;
                    XTrans target = new XTrans() ;
                    target.setTransId(newtransId);
                    target.setTransName(param.get("newName")+"");
                    target.setTransRepositoryId(toRepoId);
                    target.setTransDescription(param.get("transDescription")+"");
                    String isMonitorEnabled = param.get("isMonitorEnabled")+"";
                    if(isMonitorEnabled.equals("true")){
                        target.setIsMonitorEnabled("1");
                    }else {
                        target.setIsMonitorEnabled("0");
                    }

                    if(StringUtils.isEmpty(param.get("transLogLevel")+"") ||
                            "null".equals(param.get("transLogLevel")+"")){
                        target.setTransLogLevel(LogLevel.DEBUG.getCode());
                    }else {
                        target.setTransLogLevel(param.get("transLogLevel")+"");
                    }

                    target.setIsDel(param.get("isDel")+"");
                    target.setTransPath(param.get("newPath")+"");
                    target.setTransType(xRepositoryTo.getType());
                    xTransService.insertSelective(target);
                    //如果是文件库到文件库的复制,需要将源转换删除
                    if(xTrans.getTransType().equals("file") && target.getTransType().equals("file")){
                        String tmptransId = xTrans.getTransId() ;
                        XTransExample XTransExample1 = new XTransExample();
                        XTransExample1.createCriteria().andTransIdEqualTo(tmptransId) ;
                        xTransService.deleteByExample(XTransExample1);
                    }
                    log.debug("转换复制成功!");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }catch (KettleException ex )
        {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }

        return BaseResult.success();
    }

    @PostMapping(value = "/setIsMonitored")
    @ApiOperation("设置转换监控状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId", value = "transId", required = true, dataType = "string" ),
            @ApiImplicitParam(name = "isMonitored", value = "isMonitored", required = true, dataType = "isMonitored" )
    })
    public BaseResult setIsMonitored(@RequestBody Map params ) {
        try {

            XTransExample xTransExample = new  XTransExample();
            xTransExample.createCriteria().andTransIdEqualTo(params.get("transId")+"");
            XTrans xTrans = new XTrans() ;
            xTrans.setIsMonitorEnabled(params.get("isMonitored")+"");
            int affect = xTransService.updateByExampleSelective(xTrans, xTransExample);
            if(affect > 0 ){
                log.debug("转换{}监控状态切换成功!!",params.get("transId"));
            }
        }catch (Exception ex )
        {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }

        return BaseResult.success();
    }

                @GetMapping(value = "/get")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "transId", value = "transId", required = true, dataType = "string",paramType = "header")
                    })
               @ApiOperation("获取信息")
                public BaseResult getTrans(@RequestParam String transId) {
                    XTransExample xTransExample = new XTransExample();
                    xTransExample.createCriteria().andTransIdEqualTo(transId);
                    XTrans xTrans = this.xTransService.selectFirstExample(xTransExample);
                    return BaseResult.success(xTrans);
                    }

                    @PostMapping(value = "/add")
                    @ApiImplicitParams({
                    @ApiImplicitParam(name = "xTrans"+"", value = "xTrans"+"",
                    required = true,dataTypeClass =XTrans.class),
                    })
                    @ApiOperation("新增")
                    public BaseResult addTrans(@RequestBody @Validated XTrans xTrans, BindingResult BindingResult) {
                        if (BindingResult.hasErrors()) {
                            return this.getErrorMessage(BindingResult);
                        }
                        if(StringUtils.isEmpty(xTrans.getTransLogLevel())){
                            xTrans.setTransLogLevel(LogLevel.DEBUG.getCode());
                        }
                        String transId = xTrans.getTransId();
                        transId =transId.split("@")[1] ;
                        xTrans.setTransId(transId);
                        String isMonitorEnabled = xTrans.getIsMonitorEnabled();
                        if(isMonitorEnabled.equals("true")){
                            xTrans.setIsMonitorEnabled("1");
                        }else {
                            xTrans.setIsMonitorEnabled("0");
                        }
                        XTransExample xTransExample = new XTransExample();
                        xTransExample.createCriteria().andTransIdEqualTo(transId);
                        XTrans tmp = xTransService.selectFirstExample(xTransExample);
                        if(tmp !=null ){
                            return BaseResult.fail("已经存在该转换!");
                        }
                        int affect = this.xTransService.insertSelective(xTrans);
                        if(affect > 0 ){
                            log.debug("转换{}新增成功",xTrans.getTransName());
                        }
                        return BaseResult.success();
                        }

                        @PostMapping(value = "/update")
                        @ApiOperation("更新")
                        @ApiImplicitParams({
                        @ApiImplicitParam(name = "xTrans"+"", value = "xTrans"+"",
                            required = true,dataTypeClass =XTrans.class),
                        })
                        public BaseResult updateTrans(@RequestBody @Validated XTrans xTrans, BindingResult BindingResult) {
                            if (BindingResult.hasErrors()) {
                                return this.getErrorMessage(BindingResult);
                            }
                            if(StringUtils.isEmpty(xTrans.getTransLogLevel())){
                                xTrans.setTransLogLevel(LogLevel.DEBUG.getCode());
                            }
                            String isMonitorEnabled = xTrans.getIsMonitorEnabled();
                            if(isMonitorEnabled.equals("true")){
                                xTrans.setIsMonitorEnabled("1");
                            }else {
                                xTrans.setIsMonitorEnabled("0");
                            }

                            // 去对应资源库修改转换
                            String repositoryId = xTrans.getTransRepositoryId();
                            XRepositoryExample xRepositoryExample = new XRepositoryExample();
                            xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
                            XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
                            if (xRepository == null) {
                                return  new UniformReponseHandler().sendErrorResponse_System(new XtlExceptions("请定义资源库!"));
                            }

                            try {
                                Repository repository ;
                                if(xTrans.getTransType().equals("db")){
                                    repository =  KettleUtil.conByNative(xRepository.getRepoId(),xRepository.getRepoName(),
                                            xRepository.getRepoName(),xRepository.getRepoType(),
                                            xRepository.getDbHost(),xRepository.getDbPort(),xRepository.getDbName(),
                                            xRepository.getDbUsername(),xRepository.getDbPassword(),xRepository.getRepoUsername(),
                                            xRepository.getRepoPassword()) ;
                                    RepositoryDirectoryInterface repositoryDirectoryInterface = KettleUtil
                                            .getOrMakeDirectory(xTrans.getTransRepositoryId(),"/" ,xTrans.getTransPath() );
                                    if(repositoryDirectoryInterface !=null ){
                                        ObjectId objectId =   repository.renameTransformation(new ObjectId() {
                                            @Override
                                            public String getId() {
                                                return xTrans.getTransId();
                                            }
                                        }, repositoryDirectoryInterface, xTrans.getTransName());

                                        KettleDatabaseRepository kettleDatabaseRepository = (KettleDatabaseRepository) repository;
                                        kettleDatabaseRepository.commit();

                                        if(objectId != null ){
                                            log.debug("数据库资源库{}信息更新成功",xRepository.getRepoName());
                                        }
                                    }
                                }else {
                                    repository = KettleUtil.conFileRep(xTrans.getTransRepositoryId(),
                                            xRepository.getRepoName(),xRepository.getBaseDir() );
                                    KettleFileRepository kettleFileRepository = (KettleFileRepository) repository;
                                    RepositoryDirectoryInterface dir = KettleUtil.getOrMakeDirectory(xTrans.getTransRepositoryId(),
                                            "", xTrans.getTransPath());
                                    ObjectId objectId =  kettleFileRepository.renameTransformation(new ObjectId() {
                                        @Override
                                        public String getId() {
                                            return xTrans.getTransId();
                                        }
                                    }, dir, xTrans.getTransName());
                                    if(objectId != null ){
                                        log.debug("文件库资源库{}信息更新成功",xRepository.getRepoName());
                                    }
                                }


                            }catch (KettleException ex )
                            {
                                return new UniformReponseHandler().sendErrorResponse_System(ex) ;
                            }

                            XTransExample xTransExample = new XTransExample() ;

                            if(xTrans.getTransType().equals("file")){
                                xTransExample.createCriteria().andIdEqualTo(xTrans.getId());
                                xTrans.setTransId(xTrans.getTransPath()+"/"+xTrans.getTransName()+".ktr");
                            }else {
                                xTransExample.createCriteria().andTransIdEqualTo(xTrans.getTransId());
                            }

                            int affect = this.xTransService.updateByExampleSelective(xTrans, xTransExample);
                            if(affect > 0 ){
                                log.debug("转换{}更新成功!",xTrans.getTransName());
                            }
                            return BaseResult.success();
                            }


    @GetMapping(value = "/delFromLocal")
    @ApiOperation("从本地库删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId", value = "transId", required = true, dataType = "string" ),
    })
    public BaseResult delFromLocal(@RequestParam String transId) {
        try
        {
            XTransExample xTransExample = new  XTransExample();
            xTransExample.createCriteria().andTransIdEqualTo(transId);
            int affect = xTransService.deleteByExample(xTransExample);
            if(affect > 0 ){
                log.debug("转换{}删除成功!",transId);
            }
        }catch (Exception ex)
        {
            return new UniformReponseHandler().sendErrorResponse_System(ex) ;
        }
        return BaseResult.success();
    }

    @GetMapping(value = "/delFromRepo")
    @ApiOperation("从资源库删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "transId", value = "transId", required = true, dataType = "string" )
    })
    public BaseResult delFromRepo(
            @RequestParam String transId) {
        XTransExample xTransExample = new  XTransExample();
        xTransExample.createCriteria().andTransIdEqualTo(transId);

        try {
            XTrans xTrans = xTransService.selectFirstExample(xTransExample) ;
            String repositoryId = xTrans.getTransRepositoryId();
            XRepositoryExample xRepositoryExample = new XRepositoryExample();
            xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
            XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
            if (xRepository == null) {
                return  new UniformReponseHandler().sendErrorResponse_System(new XtlExceptions("请定义资源库!"));
            }
            Repository repository =null ;
            if(xTrans.getTransType().equals("db")){
                repository =  KettleUtil.conByNative(xRepository.getRepoId(),xRepository.getRepoName(),
                        xRepository.getRepoName(),xRepository.getRepoType(),
                        xRepository.getDbHost(),xRepository.getDbPort(),xRepository.getDbName(),
                        xRepository.getDbUsername(),xRepository.getDbPassword(),xRepository.getRepoUsername(),
                        xRepository.getRepoPassword()) ;
                KettleUtil.delTrans(Long.valueOf(transId),repository);
                log.debug("转换{}已成功从数据库资源库删除!", transId);
            }else {
                repository = KettleUtil.conFileRep(xRepository.getRepoId(), xRepository.getRepoName(),
                        xRepository.getBaseDir());
                KettleFileRepository kettleFileRepository = (KettleFileRepository) repository;
                KettleUtil.delFileJob(repositoryId,xTrans.getTransId()); ;
                log.debug("转换{}已成功从文件库资源库删除!", xTrans.getTransName());
            }

            int affect = xTransService.deleteByExample(xTransExample);
            if(affect > 0 ){
                log.debug("转换{}成功从本地库删除!",transId);
            }
        } catch (KettleException e) {
            throw new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION,e.getMessage());
        }
        return BaseResult.success();
    }


    @GetMapping(value = "/delete/{id}")
                            @ApiOperation("删除")
                              @ApiImplicitParams({
                            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string" ),
                            })
                            public BaseResult deleteTrans(@PathVariable String id) {
                                XTransExample xTransExample = new  XTransExample();
                               // xTransExample.createCriteria().andIdEqualsTo(id);
                                this.xTransService.deleteByExample(xTransExample);
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
