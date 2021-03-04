package org.yaukie.frame.kettle.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.repository.filerep.KettleFileRepository;
import org.pentaho.di.repository.kdr.KettleDatabaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.yaukie.core.annotation.LogAround;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.BaseResultConstant;
import org.yaukie.core.exception.UserDefinedException;
import org.yaukie.frame.autocode.model.XDict;
import org.yaukie.frame.autocode.model.XDictExample;
import org.yaukie.frame.autocode.model.XRepository;
import org.yaukie.frame.autocode.model.XRepositoryExample;
import org.yaukie.frame.autocode.service.api.XDictService;
import org.yaukie.frame.autocode.service.api.XRepositoryService;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.repo.RepositoryTree;
import org.yaukie.xtl.repo.XRepoManager;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
* @author: yuenbin
* @create: 2020/11/09 11/28/955
**/
@RestController
@RequestMapping(value = "/api/xrepo/")
@Api(value = "资源库接口控制器", description = "资源库接口控制器")
@Slf4j
public class XRepoApiController extends BaseController {


    @Autowired
    private XRepositoryService xRepositoryService ;

    @Autowired
    private XDictService xDictService ;


    @GetMapping(value = "/getLogLevelDicts")
    @ApiOperation("查询日志级别")
    public BaseResult getLogLevelDicts() {
        XDictExample xDictExample = new XDictExample() ;
        xDictExample.createCriteria().andDictIdEqualTo("supported_log_level");
        List<XDict> xDictxList = xDictService.selectByExample(xDictExample);
        return new UniformReponseHandler<>().sendSuccessResponse(xDictxList);
    }

    @GetMapping(value = "/getTransStatusDicts")
    @ApiOperation("查询转换运行状态字典")
    public BaseResult getTransStatusDicts() {
        XDictExample xDictExample = new XDictExample() ;
        xDictExample.createCriteria().andDictIdEqualTo("supported_trans_status");
        List<XDict> xDictxList = xDictService.selectByExample(xDictExample);
        return new UniformReponseHandler<>().sendSuccessResponse(xDictxList);
    }

    @GetMapping(value = "/getJobStatusDicts")
    @ApiOperation("查询作业运行状态字典")
    public BaseResult getJobStatusDicts() {
        XDictExample xDictExample = new XDictExample() ;
        xDictExample.createCriteria().andDictIdEqualTo("supported_job_status");
        List<XDict> xDictxList = xDictService.selectByExample(xDictExample);
        return new UniformReponseHandler<>().sendSuccessResponse(xDictxList);
    }

    @GetMapping(value = "/getRepoTypeDicts")
    @ApiOperation("查询支持的数据表")
    public BaseResult getRepoTypeDicts() {
        XDictExample xDictExample = new XDictExample() ;
        xDictExample.createCriteria().andDictIdEqualTo("supported_type_database");
        List<XDict> xDictxList = xDictService.selectByExample(xDictExample);
        return new UniformReponseHandler<>().sendSuccessResponse(xDictxList);
    }

    @GetMapping(value = "/qryRepoRootTree")
    @ApiOperation("查询资源库结构树")
    public BaseResult qryRepoRootTree() {

        XRepositoryExample xRepositoryExample = new XRepositoryExample() ;
        List<XRepository> xRepositoryList  = xRepositoryService.selectByExample(xRepositoryExample) ;
        List<RepositoryTree> repositoryTrees = new ArrayList<>() ;
        RepositoryTree root = new RepositoryTree();
        root.setParent("-1");
        root.setId("99");
        root.setText("资源库结构树");
        root.setLasted(false);
        root.setType("root");
        root.setPath("root");
        repositoryTrees.add(root) ;
        return new UniformReponseHandler<>().sendSuccessResponse(repositoryTrees);
    }

    @GetMapping(value = "/qryRepoSubTree")
    @ApiOperation("查询资源库结构树")
    public BaseResult qryRepoSubTree(
            @RequestParam(name = "pId", required = true) String pId,
            @RequestParam(name = "type", required = false) String type) {
        List<RepositoryTree> repositoryTrees = getRepoTress();
        List<RepositoryTree> subTrees = new ArrayList<>() ;
        repositoryTrees.forEach(item ->{
            if(item.getParent().equals(pId)){
                if(item.isLasted()){
                    if(!StringUtils.isEmpty(type)){
                        if (item.getType().indexOf(type) !=-1)
                        {
                            subTrees.add(item) ;
                        }
                    }else {
                        subTrees.add(item) ;
                    }

                }else {
                    subTrees.add(item) ;
                }
            }
        });

        return new UniformReponseHandler()
                .sendSuccessResponse(subTrees) ;
    }

     @RequestMapping(value = "/testConnection", method = RequestMethod.POST)
    @ApiOperation("测试数据库连接")
     @LogAround("测试数据连接")
     public BaseResult  testConnection(
            @ApiParam(name = "ddDatasourceDto", value = "数据源对象", required = true)
            @RequestBody XRepository xRepository) {

        String msg;
        // JDBC
        Connection connection = null;

        String url =  "jdbc:mysql://" + xRepository.getDbHost().trim() + ":" + xRepository.getDbPort().trim() + "/" + xRepository.getDbName().trim();
        String jdbcDriverClass="com.mysql.jdbc.Driver";

        // 组装 JDBC 连接的 url

        log.debug("\nurl: {}\njdbcDriver: {}", url, jdbcDriverClass);
        try {
            String pswd = new String(Base64.getDecoder().decode(xRepository.getDbPassword()), StandardCharsets.UTF_8);
            Class.forName(jdbcDriverClass);
            connection = DriverManager.getConnection(url, xRepository.getDbUsername(), pswd);
            msg = "连接数据库成功！";
            log.debug("\n" + msg);
            return BaseResult.success(msg,null);
        } catch (Exception e) {
            msg = "连接数据库失败!";
            log.error(msg, e);
            return new UniformReponseHandler().sendErrorResponse_System(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.error("关闭数据库连接出现异常：", e);
                }
            }
        }
    }


    private List<RepositoryTree> getRepoTress() {
        List<RepositoryTree> repositoryTrees = new ArrayList<>();

        XRepositoryExample xRepositoryExample = new XRepositoryExample();
        List<XRepository> xRepositoryList = xRepositoryService.selectByExample(xRepositoryExample);

            if (!CollectionUtils.isEmpty(xRepositoryList)) {
                xRepositoryList.forEach(item -> {
                    List<RepositoryTree> tmpRepositoryList = new ArrayList<>() ;
                    String type = item.getType() ;

                    if (type.equals("file")) {
                        // 文件库
                        String baseDir = item.getBaseDir();

                        try {
                            KettleFileRepository repository = (KettleFileRepository) KettleUtil.
                                    conFileRep(item.getRepoId(), item.getRepoName(), baseDir);
                            XRepoManager.getAllDirectoryTreeList(item.getRepoId(),repository, "/", tmpRepositoryList);
                            if(tmpRepositoryList.size() >0 ){
                                RepositoryDirectoryInterface rDirectory = repository.loadRepositoryDirectoryTree().findDirectory("/");
                                RepositoryTree repositoryTree = new RepositoryTree();
                                repositoryTree.setParent(item.getRepoId());
                                repositoryTree.setId(item.getRepoId()+"@"+rDirectory.getObjectId().toString());
                                repositoryTree.setText(rDirectory.getName().equals("\\/") ? "基础路径" : rDirectory.getName());
                                repositoryTree.setLasted(false);
                                repositoryTree.setType("tree");
                                repositoryTree.setPath("file");
                                tmpRepositoryList.add(repositoryTree);
                            }

                        } catch (KettleException e) {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            throw new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION, sw.toString().substring(0, 800));
                        }
                    } else {
                        //数据库
                        try {
                            KettleDatabaseRepository repository = (KettleDatabaseRepository) KettleUtil.conByNative(item.getRepoId(),
                                    item.getRepoName(), item.getDbName(), "MYSQL",
                                    item.getDbHost(), item.getDbPort(), item.getDbName(), item.getDbUsername(),
                                    item.getDbPassword(), item.getRepoUsername(), item.getRepoPassword());
                            XRepoManager.getAllDirectoryTreeList(item.getRepoId(),repository, "/", tmpRepositoryList);
                            if(tmpRepositoryList.size() > 0 ){

                                RepositoryDirectoryInterface rDirectory = repository.loadRepositoryDirectoryTree().findDirectory("/");
                                RepositoryTree repositoryTree = new RepositoryTree();
                                repositoryTree.setParent(item.getRepoId());
                                repositoryTree.setId(item.getRepoId()+"@"+rDirectory.getObjectId().toString());
                                repositoryTree.setText(rDirectory.getName().equals("\\/") ? "基础路径" : rDirectory.getName());
                                repositoryTree.setType("tree");
                                repositoryTree.setPath("db");
                                repositoryTree.setLasted(false);
                                tmpRepositoryList.add(repositoryTree);
                            }

                        } catch (KettleException e) {
                            StringWriter sw = new StringWriter();
                            e.printStackTrace(new PrintWriter(sw));
                            throw new UserDefinedException(BaseResultConstant.UNKNOW_EXCEPTION, sw.toString().substring(0, 800));
                        }
                    }
                    RepositoryTree repositoryTree;
                    repositoryTree = new RepositoryTree();
                    repositoryTree.setParent("99");
                    repositoryTree.setId(item.getRepoId());
                    repositoryTree.setText(item.getRepoName());
                    repositoryTree.setLasted(false);
                    repositoryTree.setType(type);
                    repositoryTree.setPath("repo");
                    tmpRepositoryList.add(repositoryTree) ;
                    repositoryTrees.addAll(tmpRepositoryList) ;
                });
            }

         return repositoryTrees ;
    }

}
