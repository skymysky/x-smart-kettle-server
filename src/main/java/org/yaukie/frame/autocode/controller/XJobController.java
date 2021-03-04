package org.yaukie.frame.autocode.controller;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.annotations.JobEntry;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.eval.JobEntryEval;
import org.pentaho.di.job.entries.shell.JobEntryShell;
import org.pentaho.di.job.entries.sql.JobEntrySQL;
import org.pentaho.di.job.entry.JobEntryCopy;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.annotation.LogAround;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.BaseResultConstant;
import org.yaukie.core.constant.PageResult;
import org.yaukie.core.exception.UserDefinedException;
import org.yaukie.frame.autocode.model.*;
import org.yaukie.frame.autocode.service.api.XDatabaseService;
import org.yaukie.frame.autocode.service.api.XJobService;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.yaukie.frame.autocode.service.api.XTemplateService;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.Constant;
import org.yaukie.xtl.exceptions.XtlExceptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuenbin
 * @create: 2020/11/16 10/36/359
 **/
@RestController
@RequestMapping(value = "/op/xjob/")
@Api(value = "XJob控制器", description = "XJob管理")
@Slf4j
public class XJobController extends BaseController {

    @Autowired
    private XJobService xJobService;

    @Autowired
    private XRepositoryService xRepositoryService;

    @Autowired
    private XTemplateService xTemplateService ;

    @Autowired
    private XDatabaseService xDatabaseService ;

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
    @EnablePage
    public BaseResult getJobPageList(
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "search", required = false) String search) {
        XJobExample xJobExample = new XJobExample();
//    if(StringUtils.isNotBlank(search)){
//        xJobExample.createCriteria().andUserIdEqualTo(search);
//    }
        List<XJob> xJobList = this.xJobService.selectByExample(xJobExample);
        PageResult pageResult = new PageResult(xJobList);
        Map<String, Object> result = new HashMap<>();
        result.put(RESULT_ROWS, pageResult.getRows());
        result.put(RESULT_TOTLAL, pageResult.getTotal());
        return BaseResult.success(result);
    }

    @GetMapping(value = "/get")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "string", paramType = "header")
    })
    @ApiOperation("获取信息")
    public BaseResult getJob(@RequestParam String jobId) {
        XJobExample xJobExample = new XJobExample();
        xJobExample.createCriteria().andJobIdEqualTo(jobId);
        XJob xJob = this.xJobService.selectFirstExample(xJobExample);
        String tplKey = xJob.getTplKey() ;
        Map dataMap =  JSON.parseObject(JSON.toJSONString(xJob),Map.class);
        if(StringUtils.isNotEmpty(tplKey)){
            XTemplateExample xTemplateExample = new XTemplateExample();
            xTemplateExample.createCriteria().andTemplateKeyEqualTo(tplKey) ;
            XTemplate xTemplate = xTemplateService.selectFirstExample(xTemplateExample) ;
            String tplName = xTemplate.getTemplateName() ;
            dataMap.put("tplName", tplName);
            dataMap.put("tplKey", tplKey) ;
            JobMeta jobMeta = null;
            try {
                jobMeta = KettleUtil.
                        loadJob(xJob.getJobName(),
                                xJob.getJobPath(), KettleUtil.holder.get(Constant.DEFAULT_REPO_ID));
            } catch (KettleException e) {
               return new UniformReponseHandler().sendErrorResponse_System(e) ;
            }
            if(tplKey.equals("sqlTpl")){
                JobEntrySQL jobEntrySQL = (JobEntrySQL) jobMeta.findJobEntry("sql").getEntry();
                String sql = jobEntrySQL.getSQL() ;
                dataMap.put("content", sql);
                DatabaseMeta databaseMeta =  jobEntrySQL.getDatabase() ;
                dataMap.put("database",databaseMeta.getName());
            }else if(tplKey.equals("shellTpl")){
                JobEntryShell jobEntryShell = (JobEntryShell) jobMeta.findJobEntry("shell").getEntry();
                dataMap.put("content",jobEntryShell.getScript() ) ;
            }else if(tplKey.equals("javascriptTpl")){
                JobEntryEval jobEntryEval = (JobEntryEval) jobMeta.findJobEntry("javascript").getEntry();
              dataMap.put("content", jobEntryEval.getScript());
            }

        }

        return BaseResult.success(dataMap);
    }


    @PostMapping(value = "/copy")
    @ApiOperation("复制作业")
    @LogAround("复制作业")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "map" + "", value = "map" + "",
                    required = true, dataTypeClass = Map.class),
    })
    public BaseResult copyJob(@RequestBody @Validated Map param, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }
        if (CollectionUtils.isEmpty(param)) {
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("作业参数为空"));
        }

        String copyNum = param.get("copyNum") + "";
        if (Integer.parseInt(copyNum) > 10) {
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("复制上限为10个!"));
        }
        //源作业信息
        String jobId = param.get("jobId") + "";
        XJobExample xJobExample = new XJobExample();
        xJobExample.createCriteria().andJobIdEqualTo(jobId);
        XJob xJob = xJobService.selectFirstExample(xJobExample);
        if (xJob == null) {
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("没有此作业信息!"));
        }

        //来源资源库
        String fromRepoId = xJob.getJobRepositoryId();
        if (StringUtils.isEmpty(fromRepoId)) {
            return new UniformReponseHandler().sendErrorResponse_System(new Exception("没有此资源库信息!"));
        }
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(fromRepoId);
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        //目标资源库
        String toRepoId = param.get("jobRepositoryId") + "";
        xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(toRepoId);
        XRepository xRepositoryTo = xRepositoryService.selectFirstExample(xRepositoryExample);
        String jobName = xJob.getJobName();
        String jobPath = xJob.getJobPath();
        String repoType = xJob.getJobType();
        Repository fromRepository = null;
        Repository toRepository = null;
        try {
            if (repoType.equals("file")) {
                fromRepository = KettleUtil.conFileRep(xRepository.getRepoId(), xRepository.getRepoName(), xRepository.getBaseDir());
            } else {
                fromRepository = KettleUtil.conByNative(xRepository.getRepoId(), xRepository.getRepoName(),
                        xRepository.getRepoName(), xRepository.getRepoType(),
                        xRepository.getDbHost(), xRepository.getDbPort(), xRepository.getDbName(),
                        xRepository.getDbUsername(), xRepository.getDbPassword(), xRepository.getRepoUsername(),
                        xRepository.getRepoPassword());
            }

            if (toRepoId.equals(fromRepoId)) {
                toRepository = fromRepository;
            } else {
                String type = xRepositoryTo.getType();
                if (type.equals("file")) {
                    toRepository = KettleUtil.conFileRep(xRepositoryTo.getRepoId(), xRepositoryTo.getRepoName(),
                            xRepositoryTo.getBaseDir());
                } else {
                    toRepository = KettleUtil.conByNative(xRepositoryTo.getRepoId(), xRepositoryTo.getRepoName(),
                            xRepositoryTo.getRepoName(), xRepositoryTo.getRepoType(),
                            xRepositoryTo.getDbHost(), xRepositoryTo.getDbPort(), xRepositoryTo.getDbName(),
                            xRepositoryTo.getDbUsername(), xRepositoryTo.getDbPassword(), xRepositoryTo.getRepoUsername(),
                            xRepositoryTo.getRepoPassword());
                }

            }
            log.debug("总共需要复制{}个作业,...", copyNum);
            if (Integer.parseInt(copyNum) > 1) {
                String tmpNewName = param.get("newName") + "";
                String tmpJobPath = param.get("jobPath") + "";
                for (int i = 1; i <= Integer.parseInt(copyNum); i++) {
                    param.put("repoId", xRepositoryTo.getRepoId());
                    param.put("newName", tmpNewName + "_" + i + "_cp");
                    param.put("newPath", tmpJobPath);
                    param.put("jobName", xJob.getJobName());
                    param.put("jobPath", xJob.getJobPath());
                    KettleUtil.jobCopy(param, fromRepository, toRepository);
                    JobMeta jobMeta = KettleUtil.loadJob(toRepoId, param.get("newName") + "", param.get("newPath") + "");
                    String newJobId = jobMeta.getObjectId().getId();
                    XJob target = new XJob();
                    target.setJobId(newJobId);
                    target.setJobName(param.get("newName") + "");
                    target.setJobRepositoryId(toRepoId);
                    target.setJobDescription(param.get("jobDescription") + "");
                    String isMonitorEnabled = param.get("isMonitorEnabled") + "";
                    if (isMonitorEnabled.equals("true")) {
                        target.setIsMonitorEnabled("1");
                    } else {
                        target.setIsMonitorEnabled("0");
                    }

                    if (StringUtils.isEmpty(param.get("jobLogLevel") + "") ||
                            "null".equals(param.get("jobLogLevel") + "")) {
                        target.setJobLogLevel(LogLevel.DEBUG.getCode());
                    } else {
                        target.setJobLogLevel(param.get("jobLogLevel") + "");
                    }

                    target.setIsDel(param.get("isDel") + "");
                    target.setJobPath(param.get("newPath") + "");
                    target.setJobType(xRepositoryTo.getType());
                    xJobService.insertSelective(target);
                    log.debug("第{}个作业复制成功!", i + 1);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                {
                    param.put("repoId", xRepositoryTo.getRepoId());
                    param.put("newPath", param.get("jobPath"));
                    param.put("jobName", xJob.getJobName());
                    param.put("jobPath", xJob.getJobPath());
                    KettleUtil.jobCopy(param, fromRepository, toRepository);
                    JobMeta jobMeta = KettleUtil.loadJob(toRepoId, param.get("newName") + "", param.get("newPath") + "");
                    String newJobId = jobMeta.getObjectId().getId();
                    XJob target = new XJob();
                    target.setJobId(newJobId);
                    target.setJobName(param.get("newName") + "");
                    target.setJobRepositoryId(toRepoId);
                    target.setJobDescription(param.get("jobDescription") + "");
                    String isMonitorEnabled = param.get("isMonitorEnabled") + "";
                    if (isMonitorEnabled.equals("true")) {
                        target.setIsMonitorEnabled("1");
                    } else {
                        target.setIsMonitorEnabled("0");
                    }

                    if (StringUtils.isEmpty(param.get("jobLogLevel") + "") ||
                            "null".equals(param.get("jobLogLevel") + "")) {
                        target.setJobLogLevel(LogLevel.DEBUG.getCode());
                    } else {
                        target.setJobLogLevel(param.get("jobLogLevel") + "");
                    }

                    target.setIsDel(param.get("isDel") + "");
                    target.setJobPath(param.get("newPath") + "");
                    target.setJobType(xRepositoryTo.getType());
                    xJobService.insertSelective(target);
                    //如果是文件库到文件库的复制,需要将源作业删除
                    if (xJob.getJobType().equals("file") && target.getJobType().equals("file")) {
                        String tmpJobId = xJob.getJobId();
                        XJobExample xJobExample1 = new XJobExample();
                        xJobExample1.createCriteria().andJobIdEqualTo(tmpJobId);
                        xJobService.deleteByExample(xJobExample1);
                    }
                    log.debug("作业复制成功!");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (KettleException ex) {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }

        return BaseResult.success();
    }


    @PostMapping(value = "/addJobByTpl")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params" + "", value = "params" + "",
                    required = true, dataTypeClass = Map.class),
    })
    @ApiOperation("按照模板新增")
    public BaseResult addJobByTpl(@RequestBody @Validated Map params, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }
        XJob xJob = new XJob();
        if (StringUtils.isEmpty(params.get("jobLogLevel")+"")) {
            xJob.setJobLogLevel(LogLevel.DEBUG.getCode());
        }
        String jobId = "" ;
        String tplKey =params.get("tplKey")+"" ;
        XTemplateExample xTemplateExample = new XTemplateExample() ;
        xTemplateExample.createCriteria().andTemplateKeyEqualTo(tplKey) ;
        XTemplate xTemplate = xTemplateService.selectFirstExample(xTemplateExample) ;
        if(null == xTemplate ){
            return BaseResult.fail() ;
        }
        String templatePath = xTemplate.getTemplatePath() ;
        String rootPath =Thread.currentThread().getContextClassLoader()
                .getResource("").getPath();
        rootPath=rootPath.substring(1);
        try {
            JobMeta jobMeta = new JobMeta(rootPath+templatePath,KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID));
            jobMeta.setName(params.get("jobName")+"");
            String directory = params.get("jobPath")+"";
            RepositoryDirectoryInterface dir = KettleUtil.getOrMakeDirectory(
                    Constant.DEFAULT_REPO_ID,
                    "/",
                    directory);
               jobMeta.setRepositoryDirectory(dir);
               jobMeta.setDescription(params.get("jobDescription")+"");
             if(tplKey.equals("sqlTpl")){
                JobEntrySQL jobEntrySQL = (JobEntrySQL) jobMeta.findJobEntry("sql").getEntry();
                String dbKey = params.get("database")+"" ;
                XDatabaseExample xDatabaseExample = new XDatabaseExample() ;
                 xDatabaseExample.createCriteria().andNameEqualTo(dbKey) ;
                XDatabase xDatabase = xDatabaseService.selectFirstExample(xDatabaseExample) ;
                 DatabaseMeta databaseMeta = KettleUtil.createDatabaseMeta(xDatabase.getName(),
                         xDatabase.getDatabaseType(),
                         xDatabase.getDatabaseContype(),
                         xDatabase.getHostName(),
                         xDatabase.getDatabaseName(),
                         xDatabase.getPort()+"",
                         xDatabase.getUsername(),
                         xDatabase.getPassword(),
                         false,
                         KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID)) ;
                jobEntrySQL.setDatabase(databaseMeta);
                jobEntrySQL.setSQL(params.get("content")+"");

             }else if(tplKey.equals("shellTpl")){
                JobEntryShell jobEntryShell = (JobEntryShell) jobMeta.findJobEntry("shell").getEntry();
                jobEntryShell.setWorkDirectory("/test");
                 jobEntryShell.setScript(params.get("content")+"");

             }else if(tplKey.equals("javascriptTpl")){
                JobEntryEval jobEntryEval = (JobEntryEval) jobMeta.findJobEntry("javascript").getEntry();
                jobEntryEval.setScript(params.get("content")+"");
            }

            try {
                KettleUtil.saveJob(Constant.DEFAULT_REPO_ID, jobMeta);
            } catch (KettleException e) {
                return new UniformReponseHandler().sendErrorResponse_System(e) ;
            }

            JobMeta jobMetaTmp = null;
            try {
                jobMetaTmp = KettleUtil.loadJob(jobMeta.getName(), params.get("jobPath")+"",
                        KettleUtil.holder.get(Constant.DEFAULT_REPO_ID));
            } catch (KettleException e) {
               return new UniformReponseHandler().sendErrorResponse_System(e) ;
            }
            jobId = jobMetaTmp.getObjectId().getId() ;
            String isMonitorEnabled = params.get("isMonitorEnabled")+"";
            if (isMonitorEnabled.equals("true")) {
                xJob.setIsMonitorEnabled("1");
            } else {
                xJob.setIsMonitorEnabled("0");
            }
            xJob.setJobId(jobId);
            xJob.setJobName(params.get("jobName")+"");
            xJob.setJobDescription(params.get("jobDescription")+"");
            xJob.setJobPath(params.get("jobPath")+"");
            xJob.setJobDescription(params.get("jobDescription")+"");
            xJob.setTplKey(tplKey);
             xJob.setJobType("db");
            xJob.setJobRepositoryId(Constant.DEFAULT_REPO_ID);
            XJobExample xJobExample = new XJobExample();
            xJobExample.createCriteria().andJobIdEqualTo(jobId);
            XJob tmp = xJobService.selectFirstExample(xJobExample);
            if (tmp != null) {
                return BaseResult.fail("已经存在该作业!");
            }

             int affect = this.xJobService.insertSelective(xJob);
            if (affect > 0) {
                log.debug("作业{}新增成功", xJob.getJobName());
            }
        } catch (KettleXMLException e) {
            return new UniformReponseHandler().sendErrorResponse_System(e) ;
        } catch (KettleException e) {
            return new UniformReponseHandler().sendErrorResponse_System(e) ;
        }

        return BaseResult.success();
    }


    @PostMapping(value = "/add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "xJob" + "", value = "xJob" + "",
                    required = true, dataTypeClass = XJob.class),
    })
    @ApiOperation("新增")
    public BaseResult addJob(@RequestBody @Validated XJob xJob, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }
        if (StringUtils.isEmpty(xJob.getJobLogLevel())) {
            xJob.setJobLogLevel(LogLevel.DEBUG.getCode());
        }
        String jobId = xJob.getJobId();
        jobId = jobId.split("@")[1];
        xJob.setJobId(jobId);
        String isMonitorEnabled = xJob.getIsMonitorEnabled();
        if (isMonitorEnabled.equals("true")) {
            xJob.setIsMonitorEnabled("1");
        } else {
            xJob.setIsMonitorEnabled("0");
        }
        XJobExample xJobExample = new XJobExample();
        xJobExample.createCriteria().andJobIdEqualTo(jobId);
        XJob tmp = xJobService.selectFirstExample(xJobExample);
        if (tmp != null) {
            return BaseResult.fail("已经存在该作业!");
        }
        int affect = this.xJobService.insertSelective(xJob);
        if (affect > 0) {
            log.debug("作业{}新增成功", xJob.getJobName());
        }
        return BaseResult.success();
    }

    @PostMapping(value = "/update")
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "params" + "", value = "params" + "",
                    required = true, dataTypeClass = Map.class),
    })
    public BaseResult updateJob(@RequestBody @Validated Map params, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }

        XJob xJob = new XJob() ;
        String jobLogLevel = params.get("jobLogLevel") +"";

        if (StringUtils.isEmpty(jobLogLevel)) {
            xJob.setJobLogLevel(LogLevel.DEBUG.getCode());
        }
        String isMonitorEnabled = params.get("isMonitorEnabled")+"";//xJob.getIsMonitorEnabled();
        if (isMonitorEnabled.equals("true")) {
            xJob.setIsMonitorEnabled("1");
        } else {
            xJob.setIsMonitorEnabled("0");
        }

        // 去对应资源库修改作业
        String repositoryId = params.get("jobRepositoryId")+"";//Constant.DEFAULT_REPO_ID;//xJob.getJobRepositoryId();
        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
        XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
        if (xRepository == null) {
            return new UniformReponseHandler().sendErrorResponse_System(new XtlExceptions("请定义资源库!"));
        }

        try {
            Repository repository;
            String jobId = params.get("jobId")+"" ;
            String jobType=params.get("jobType")+"";
            if (jobType.equals("db")) {
                repository = KettleUtil.conByNative(xRepository.getRepoId(), xRepository.getRepoName(),
                        xRepository.getRepoName(), xRepository.getRepoType(),
                        xRepository.getDbHost(), xRepository.getDbPort(), xRepository.getDbName(),
                        xRepository.getDbUsername(), xRepository.getDbPassword(), xRepository.getRepoUsername(),
                        xRepository.getRepoPassword());
                RepositoryDirectoryInterface repositoryDirectoryInterface = KettleUtil
                        .getOrMakeDirectory(repositoryId, "/", params.get("jobPath")+"");
                if (repositoryDirectoryInterface != null) {
                    ObjectId objectId = repository.renameJob(new ObjectId() {
                        @Override
                        public String getId() {
                            return jobId;
                        }
                    }, repositoryDirectoryInterface,params.get("jobName")+"");

                    KettleDatabaseRepository kettleDatabaseRepository = (KettleDatabaseRepository) repository;
                    kettleDatabaseRepository.commit();

                    if (objectId != null) {
                        log.debug("数据库资源库{}信息更新成功", xRepository.getRepoName());
                    }
                }

                // 将sql shell JavaScript模板的内容修改
                String tplKey = params.get("tplKey")+"" ;
                if(!tplKey.equals("null") && StringUtils.isNotEmpty(tplKey)){

                    JobMeta jobMeta = KettleUtil.loadJob(Constant.DEFAULT_REPO_ID,params.get("jobName")+"",params.get("jobPath")+"");
                     if(tplKey.equals("sqlTpl")){
                        JobEntrySQL jobEntrySQL = (JobEntrySQL) jobMeta.findJobEntry("sql").getEntry();
                        String dbKey = params.get("database")+"" ;
                        XDatabaseExample xDatabaseExample = new XDatabaseExample() ;
                        xDatabaseExample.createCriteria().andNameEqualTo(dbKey) ;
                        XDatabase xDatabase = xDatabaseService.selectFirstExample(xDatabaseExample) ;
                        DatabaseMeta databaseMeta = KettleUtil.createDatabaseMeta(xDatabase.getName(),
                                xDatabase.getDatabaseType(),
                                xDatabase.getDatabaseContype(),
                                xDatabase.getHostName(),
                                xDatabase.getDatabaseName(),
                                xDatabase.getPort()+"",
                                xDatabase.getUsername(),
                                xDatabase.getPassword(),
                                false,
                                KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID)) ;
                        jobEntrySQL.setDatabase(databaseMeta);
                        jobEntrySQL.setSQL(params.get("content")+"");

                    }else if(tplKey.equals("shellTpl")){
                        JobEntryShell jobEntryShell = (JobEntryShell) jobMeta.findJobEntry("shell").getEntry();
                        jobEntryShell.setWorkDirectory("/test");
                        jobEntryShell.setScript(params.get("content")+"");

                    }else if(tplKey.equals("javascriptTpl")){
                        JobEntryEval jobEntryEval = (JobEntryEval) jobMeta.findJobEntry("javascript").getEntry();
                        jobEntryEval.setScript(params.get("content")+"");
                    }

                }


            } else {
                repository = KettleUtil.conFileRep(repositoryId,
                        xRepository.getRepoName(), xRepository.getBaseDir());
                KettleFileRepository kettleFileRepository = (KettleFileRepository) repository;
                RepositoryDirectoryInterface dir = KettleUtil.getOrMakeDirectory(repositoryId,
                        "", params.get("jobPath")+"");
                ObjectId objectId = kettleFileRepository.renameJob(new ObjectId() {
                    @Override
                    public String getId() {
                        return jobId;
                    }
                }, dir, params.get("jobName")+"");
                if (objectId != null) {
                    log.debug("文件库资源库{}信息更新成功", xRepository.getRepoName());
                }
            }

        } catch (KettleException ex) {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }

        XJobExample xJobExample = new XJobExample();
        String jobType = params.get("jobType")+"" ;
        if (jobType.equals("file")) {
            xJobExample.createCriteria().andIdEqualTo(Integer.parseInt(params.get("id")+""));
            xJob.setJobId(params.get("jobPath") + "/" + params.get("jobName") + ".kjb");
        } else {
            xJobExample.createCriteria().andJobIdEqualTo(params.get("jobId")+"");
        }

        xJob.setJobName(params.get("jobName")+"");
        xJob.setJobDescription(params.get("jobDescription")+"");
        xJob.setJobPath(params.get("jobPath")+"");
        int affect = this.xJobService.updateByExampleSelective(xJob, xJobExample);
        if (affect > 0) {
            log.debug("作业{}更新成功!", xJob.getJobName());
        }
        return BaseResult.success();
    }


    @PostMapping(value = "/setIsDel/{jobId}")
    @ApiOperation("设置作业状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "string"),
    })
    public BaseResult setIsDel(@PathVariable String jobId) {
        try {

            XJobExample xJobExample = new XJobExample();
            xJobExample.createCriteria().andJobIdEqualTo(jobId);
            XJob xJob = xJobService.selectFirstExample(xJobExample);
            String isDel = xJob.getIsDel();
            //1 表示该作业已被逻辑删除
            xJob.setIsDel("1");
            if (isDel.equals("1")) {
                xJob.setIsDel("0");
            }
            int affect = xJobService.updateByExampleSelective(xJob, xJobExample);
            if (affect > 0) {
                log.debug("作业{}状态设置成功!!", jobId);
            }
        } catch (Exception ex) {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }

        return BaseResult.success();
    }


    @PostMapping(value = "/setIsMonitored")
    @ApiOperation("设置作业监控状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "string"),
            @ApiImplicitParam(name = "isMonitored", value = "isMonitored", required = true, dataType = "isMonitored")
    })
    public BaseResult setIsMonitored(@RequestBody Map params) {
        try {

            XJobExample xJobExample = new XJobExample();
            xJobExample.createCriteria().andJobIdEqualTo(params.get("jobId") + "");
            XJob xJob = new XJob();
            xJob.setIsMonitorEnabled(params.get("isMonitored") + "");
            int affect = xJobService.updateByExampleSelective(xJob, xJobExample);
            if (affect > 0) {
                log.debug("作业{}监控状态切换成功!!", params.get("jobId"));
            }
        } catch (Exception ex) {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }

        return BaseResult.success();
    }

    @GetMapping(value = "/delFromLocal")
    @ApiOperation("从本地库删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "string"),
    })
    public BaseResult delFromLocal(@RequestParam String jobId) {
        try {
            XJobExample xJobExample = new XJobExample();
            xJobExample.createCriteria().andJobIdEqualTo(jobId);
            int affect = xJobService.deleteByExample(xJobExample);
            if (affect > 0) {
                log.debug("作业{}删除成功!", jobId);
            }
        } catch (Exception ex) {
            return new UniformReponseHandler().sendErrorResponse_System(ex);
        }
        return BaseResult.success();
    }

    @GetMapping(value = "/delFromRepo")
    @ApiOperation("从资源库删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "jobId", value = "jobId", required = true, dataType = "string")
    })
    public BaseResult delFromRepo(
            @RequestParam String jobId) {
        XJobExample xJobExample = new XJobExample();
        xJobExample.createCriteria().andJobIdEqualTo(jobId);

        try {
            XJob xJob = xJobService.selectFirstExample(xJobExample);
            String repositoryId = xJob.getJobRepositoryId();
            XRepositoryExample xRepositoryExample = new XRepositoryExample();
            xRepositoryExample.createCriteria().andRepoIdEqualTo(repositoryId + "");
            XRepository xRepository = xRepositoryService.selectFirstExample(xRepositoryExample);
            if (xRepository == null) {
                return new UniformReponseHandler().sendErrorResponse_System(new XtlExceptions("请定义资源库!"));
            }
            Repository repository = null;
            if (xJob.getJobType().equals("db")) {
                repository = KettleUtil.conByNative(xRepository.getRepoId(), xRepository.getRepoName(),
                        xRepository.getRepoName(), xRepository.getRepoType(),
                        xRepository.getDbHost(), xRepository.getDbPort(), xRepository.getDbName(),
                        xRepository.getDbUsername(), xRepository.getDbPassword(), xRepository.getRepoUsername(),
                        xRepository.getRepoPassword());
                KettleUtil.delJob(Long.valueOf(jobId), repository);
                log.debug("作业{}已成功从数据库资源库删除!", jobId);
            } else {
                repository = KettleUtil.conFileRep(xRepository.getRepoId(), xRepository.getRepoName(),
                        xRepository.getBaseDir());
                KettleFileRepository kettleFileRepository = (KettleFileRepository) repository;
                KettleUtil.delFileJob(repositoryId, xJob.getJobId());
                log.debug("作业{}已成功从文件库资源库删除!", xJob.getJobName());
            }

            int affect = xJobService.deleteByExample(xJobExample);
            if (affect > 0) {
                log.debug("作业{}成功从本地库删除!", jobId);
            }
        } catch (KettleException e) {
            throw new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION, e.getMessage());
        }
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
