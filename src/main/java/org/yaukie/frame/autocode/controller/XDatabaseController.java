package org.yaukie.frame.autocode.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.yaukie.core.annotation.EnablePage;
import org.yaukie.core.annotation.LogAround;
import org.yaukie.core.base.controller.BaseController;
import org.yaukie.core.config.UniformReponseHandler;
import org.yaukie.core.constant.BaseResult;
import org.yaukie.core.constant.PageResult;
import org.yaukie.frame.autocode.service.api.XDatabaseService;
import org.yaukie.frame.autocode.model.XDatabase;
import org.yaukie.frame.autocode.model.XDatabaseExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.yaukie.xtl.KettleUtil;
import org.yaukie.xtl.cons.Constant;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: yuenbin
 * @create: 2021/01/29 19/28/716
 **/
@RestController
@RequestMapping(value = "/op/xdatabase/")
@Api(value = "XDatabase控制器", description = "XDatabase管理")
@Slf4j
public class XDatabaseController extends BaseController {

    @Autowired
    private XDatabaseService xDatabaseService;


    @RequestMapping(value = "/testConnection", method = RequestMethod.POST)
    @ApiOperation("测试数据库连接")
    @LogAround("测试数据连接")
    public BaseResult testConnection(
            @ApiParam(name = "ddDatasourceDto", value = "数据源对象", required = true)
            @RequestBody XDatabase xDatabase) {

        String msg;
        // JDBC
        Connection connection = null;
        String url = "";
        String jdbcDriverClass = "";
        if (xDatabase.getDatabaseType().equalsIgnoreCase("mysql")) {
            url = "jdbc:mysql://" + xDatabase.getHostName().trim() + ":" + xDatabase.getPort() + "/" + xDatabase.getDatabaseName().trim();
            jdbcDriverClass = "com.mysql.jdbc.Driver";
        } else if (xDatabase.getDatabaseType().equalsIgnoreCase("oracle")) {
            url = "jdbc:oracle:thin:@//" + xDatabase.getHostName() + ":" + xDatabase.getPort() + "/" + xDatabase.getDatabaseName();
            jdbcDriverClass = "oracle.jdbc.driver.OracleDriver";
        }

        // 组装 JDBC 连接的 url

        log.info("\nurl: {}\njdbcDriver: {}", url, jdbcDriverClass);
        try {
            String pswd = new String(Base64.getDecoder().decode(xDatabase.getPassword()), StandardCharsets.UTF_8);
            Class.forName(jdbcDriverClass);
            connection = DriverManager.getConnection(url, xDatabase.getUsername(), pswd);
            if (connection != null) {
                msg = "连接数据库成功！";
                log.info("\n" + msg);
                return BaseResult.success(msg, null);
            }

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
        return BaseResult.success();
    }

    @GetMapping(value = "/listPage")
    @ApiOperation("获取列表")
    @EnablePage
    public BaseResult getDatabasePageList(
            @RequestParam(value = "offset", required = false) String offset,
            @RequestParam(value = "limit", required = false) String limit,
            @RequestParam(value = "search", required = false) String search) {
        XDatabaseExample xDatabaseExample = new XDatabaseExample();
//    if(StringUtils.isNotBlank(search)){
//        xDatabaseExample.createCriteria().andUserIdEqualTo(search);
//    }
        List<XDatabase> xDatabaseList = this.xDatabaseService.selectByExample(xDatabaseExample);
        PageResult pageResult = new PageResult(xDatabaseList);
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
    public BaseResult getDatabase(@PathVariable String id) {
        XDatabase xDatabase = this.xDatabaseService.selectByPrimaryKey(Integer.parseInt(id));
        return BaseResult.success(xDatabase);
    }

    @PostMapping(value = "/add")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "xDatabase" + "", value = "xDatabase" + "",
                    required = true, dataTypeClass = XDatabase.class),
    })
    @ApiOperation("新增")
    public BaseResult addDatabase(@RequestBody @Validated XDatabase xDatabase, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }

        int affect = this.xDatabaseService.insertSelective(xDatabase);
        try {
            if (affect > 0) {
                DatabaseMeta databaseMeta = KettleUtil.createDatabaseMeta(xDatabase.getName(),
                        xDatabase.getDatabaseType(),xDatabase.getDatabaseContype() ,
                        xDatabase.getHostName(),xDatabase.getDatabaseName() ,xDatabase.getPort()+"" ,
                        xDatabase.getUsername(), xDatabase.getPassword(),
                        null,KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID) );
                if(databaseMeta != null ){
                    log.info("数据元创建成功!");
                }
            }
        }catch (Exception e )
        {
            return new UniformReponseHandler().sendErrorResponse_System(e) ;
        }

        return BaseResult.success();
    }

    @PostMapping(value = "/update")
    @ApiOperation("更新")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "xDatabase" + "", value = "xDatabase" + "",
                    required = true, dataTypeClass = XDatabase.class),
    })
    public BaseResult updateDatabase(@RequestBody @Validated XDatabase xDatabase, BindingResult BindingResult) {
        if (BindingResult.hasErrors()) {
            return this.getErrorMessage(BindingResult);
        }

        XDatabaseExample xDatabaseExample = new XDatabaseExample();
        xDatabaseExample.createCriteria().andIdEqualTo(xDatabase.getId()) ;
        XDatabase tmp  = xDatabaseService.selectFirstExample(xDatabaseExample) ;

        try {
             KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID).deleteDatabaseMeta(tmp.getName());
             int affect =  this.xDatabaseService.updateByPrimaryKey(xDatabase);
             if(affect >0){
                 DatabaseMeta databaseMeta = KettleUtil.createDatabaseMeta(xDatabase.getName(),
                         xDatabase.getDatabaseType(),xDatabase.getDatabaseContype() ,
                         xDatabase.getHostName(),xDatabase.getDatabaseName() ,xDatabase.getPort()+"" ,
                         xDatabase.getUsername(), xDatabase.getPassword(),
                         null,KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID) );
                 if(databaseMeta != null ){
                     log.info("数据元创建成功!");
                 }
             }

        }catch (Exception e )
        {
            return new UniformReponseHandler().sendErrorResponse_System(e) ;
        }

        return BaseResult.success();
    }

    @GetMapping(value = "/delete/{id}")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "id", required = true, dataType = "string"),
    })
    public BaseResult deleteDatabase(@PathVariable String id) {
        XDatabaseExample xDatabaseExample = new XDatabaseExample();
         xDatabaseExample.createCriteria().andIdEqualTo(Integer.parseInt(id)) ;
         XDatabase xDatabase  = xDatabaseService.selectFirstExample(xDatabaseExample) ;
       int affect =  this.xDatabaseService.deleteByExample(xDatabaseExample);
       if(affect > 0 ){
           try {
               KettleUtil.getHolder().get(Constant.DEFAULT_REPO_ID).deleteDatabaseMeta(xDatabase.getName());
           } catch (KettleException e) {
              return new UniformReponseHandler().sendErrorResponse_System(e) ;
           }
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
