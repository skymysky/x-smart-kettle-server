# Smart Kettle
## 企业的痛点
- kettle的Spoon客户端太耗内存，异常卡顿，性能瓶颈明显
- kettle自带web管理工具，极其简陋，异常难用，无法投入生产环境
- kettle客户端工具无法在linux系统使用
- kettle客户端无法做到真正意义上的跨平台，bat及shell脚本切换繁杂
- kettle客户端迁移麻烦，每次都要安装一遍，耗费精力
## 简介
     Smart Kettle是针对上述企业的痛点，对kettle的使用做了一些包装、优化，使其在web端也能
     具备基础的kettle作业、转换的配置、调度、监控，能在很大一定程度上协助企业完成不同业务场景下
     数据的ETL（抽取、转换、加工）的能力。
     注意：本系统并非是对kettle源码的再造，而是借助kettle的API，实现kettle在web端功能华丽的转身
     
### 1. 它是一款超轻量级的kettle web端调度监控平台
- 支持作业、转换的自定义模板设置
- 支持作业、转换的多任务模板复制
- 支持作业、转换的GUI端配置同步到web端
- 支持作业、转换的日志自定路径配置
- 支持作业、转换的日志文件下载管理
- 支持作业、转换的集群调度（远程子服务器调用）
- 支持作业、转换的GUI端配置同步到web端
- 支持作业、转换的调度配置
- 支持作业、转换的监控管理
- 支持作业、转换的本地执行
- 支持作业、转换的远程执行
- 支持作业、转换的定时配置
- 支持作业、转换的实时监控
- 支持kettle的web端资源库管理
- 支持自定义线程池设置、任务的并行处理
- 支持kettle 任务的告警监控、日志管理
- 提供丰富的业务库、字典库自定义设置
- 提供完整的、实时的大盘调度监控
- 提供系统的用户、角色、权限管理
- 提供Druid数据库查询脚本的实时监控能力
- 支持 Kettle 7.0.1+以上 版本

### 2. 它的平台实现充分基于"前后端分离"思想
- 后端架构基于 Springboot实现
- 服务端可发布 REST 服务
- 前端架构基于 VUE，数据组件更加丰富、易于维护
- 客户端通过 AJAX 获取服务端数据并进行界面渲染

### 3. 它的后端实现基于互联网最流行的微服务技术
- 后端架构采用自己搭建的x-common-base框架
- 后端封装了基于kettle的强大接口插件x-kettle-core
- 后端框架基于springboot+Mybatis实现
- 后端框架易于迁移、二次开发、方便维护

### 4. 它的前端实现基于互联网最流行的渐进式VUE框架

- 功能组件化，易于二次开发维护
- 新手容易上手，短时间内入门
- 界面更加美观、组件更加丰富
- 平台功能架构见如下图：  

![平台功能架构](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/struc.png)
![平台功能架构](http://github.com/yaukie/x-smart-kettle-server/raw/master/folder/struc.png)
![平台功能架构](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/struc.png)

## 项目简介
整个工程的目录结构如下：  
后端工程：
- [内部网站：x-smart-kettle-server](https://gitee.com/yaukie/x-smart-kettle-server.git)
- [github.com：x-smart-kettle-server](http://github.com/yaukie/x-smart-kettle-server.git)
- [gitee.com：x-smart-kettle-server](https://gitee.com/yaukie/x-smart-kettle-server.git)

```
├─doc
│  └─database
│      ├─MySql_ds0  --基础数据库设置
│      └─MySql_ds1  --基础数据库设置
├─docker  --容器部署详情
├─folder
├─jenkins  --jenkins持续部署详情
├─settings  --maven默认配置
├─src
│  ├─main
│  │  ├─java
│  │  │  └─org
│  │  │      └─yaukie
│  │  │          └─frame
│  │  │              ├─autocode  --代码机入口
│  │  │              │  ├─controller
│  │  │              │  ├─dao
│  │  │              │  │  └─mapper
│  │  │              │  ├─model
│  │  │              │  └─service
│  │  │              │      ├─api
│  │  │              │      └─impl
│  │  │              ├─config  --系统基础配置入口
│  │  │              ├─kettle
│  │  │              │  ├─api  --kettle核心接口所在位置
│  │  │              │  ├─core
│  │  │              │  ├─listener   --kettle监听所在位置
│  │  │              │  ├─quartz   --定时器所在位置
│  │  │              │  └─service
│  │  │              ├─listener
│  │  │              └─pool   --线程池配置所在位置
│  │  └─resources   --核心配置所在位置
│  │      ├─mapper
│  │      ├─template
│  │      └─ui
│  │          └─images
│  └─test
│      └─java
│          └─org
│              └─yaukie
│                  └─frame   --核心逻辑所在位置
```   
前端工程：
- [内部网站：x-smart-kettle-front](http://open.inspur.com/yuenbin/x-smart-kettle-front.git)
- [github.com：x-smart-kettle-front](http://github.com/yaukie/x-smart-kettle-front.git)
- [gitee.com：x-smart-kettle-front](http://gitee.com/yaukie/x-smart-kettle-front.git)

```
├─docker
│  ├─dev
│  └─prod
├─jenkins
├─public
│  ├─cron
│  └─json
├─src
│  ├─api
│  │  ├─login-form
│  │  ├─main
│  │  │  └─components
│  │  │      ├─a-back-top
│  │  │      ├─error-store
│  │  │      ├─fullscreen
│  │  │      ├─header-bar
│  │  │      │  ├─custom-bread-crumb
│  │  │      │  └─sider-trigger
│  │  │      ├─language
│  │  │      ├─side-menu
│  │  │      ├─tags-nav
│  │  │      └─user
│  │  ├─page-box
│  │  │  └─src
│  │  │      └─styles
│  │  │          └─css
│  │  ├─page-table
│  │  │  └─src
│  │  │      ├─components
│  │  │      └─styles
│  │  │          └─css
│  │  ├─parent-view
│  │  ├─search-box
│  │  │  └─src
│  │  │      └─styles
│  │  │          └─css
│  │  ├─upload-file
│  │  │  └─src
│  │  │      └─styles
│  │  │          └─css
│  │  ├─upload-img
│  │  │  └─src
│  │  │      └─styles
│  │  │          ├─css
│  │  │          └─images
│  │  ├─upload-img-list
│  │  │  └─src
│  │  │      └─styles
│  │  │          └─css
│  │  ├─upload-video
│  │  │  └─src
│  │  │      └─styles
│  │  │          ├─css
│  │  │          └─images
│  │  └─weeks
│  │      └─src
│  ├─config
│  ├─directive
│  │  └─module
│  ├─libs --工具类
│  ├─locale --国际化配置
│  │  └─lang
│  ├─mock 
│  │  └─data
│  ├─router --基础路由配置
│  │  └─modules
│  ├─store --系统状态机
│  │  └─module
│  ├─styles
│  │  └─components
│  └─view --调度平台核心前端功能
│      ├─business --业务配置
│      ├─examples
│      │  ├─common
│      │  └─page
│      ├─exception --异常监控
│      ├─homepage --调度大屏
│      │  └─common
│      ├─job --作业调度
│      ├─log --日志监控
│      ├─login --登录模板
│      ├─pool  --线程池配置
│      ├─repo --资源库配置
│      ├─scheduler --定时器调度
│      ├─task --定时调度
│      ├─trans --转换调度
│      └─warning --告警监控

```
## 环境要求
- Maven3+
- Jdk1.8+
- Mysql5.7+

## 功能概览
- 登录界面  
![登录界面](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/登录/login.png)  
登录界面内置了两个账号，一个是admin，一个是superadmin，密码随便输入即可   
- 系统菜单  
![系统菜单](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/左侧菜单/1.png)  
- 调度大盘  
![调度大盘](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/大屏/2.png)    
调度大盘上半部分，展示近期实例运行详情，包括运行成功、运行失败、运行中、以及未运行的实例总体概况  
![调度大盘](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/大屏/3.png)    
调度大盘中间部分，则通过图表统计作业以及转换实例的分类情况  
![调度大盘](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/大屏/4.png)    
调度大盘下半部分，则主要展示实例在某段时间的运行趋势如何，可以透过折线图很直观的看出每个时间段的
执行情况，包括运行失败、运行成功的次数分布 
调度大盘的最下面则主要统计任务告警情况  

- 调度管理  
![调度管理](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/调度管理/5.png)
  调度管理分为作业调度以及转换调度，作业调度包括作业名称、描述、运行状态以及运行时间等信息展示 ，
  在作业调度查询界面，选择创建作业（按模板），则将会根据选定模板创建作业  
![调度管理](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/调度管理/6.png)    
   在作业调度查询界面，选择新建作业（已有），则将会从资源库中选择已通过客户端配置好的  
     作业  
![调度管理](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/调度管理/7.png)    
   在作业调度查询界面，选中目标作业之后，在上方点击合适的执行按钮，将会执行对应任务，并实时监控任务状态   
![调度管理](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/调度管理/13.png)    
   在作业调度查询界面，选中目标作业之后，点击调度监控，则可以查看作业的调度图  
   上述所有操作，转换调度的操作方式同作业类似    

- 定时调度  
在作业调度界面，选中执行方式，在执行方式中，选择定时任务执行，则进入到定时执行调度界面  
![定时调度](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/定时调度/9.png)  
 在作业定时界面中，您可以选择任意的定时规则  
![定时调度](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/定时调度/10.png)    

- 资源库管理   
可以维护多个资源库，本系统同时支持文件库以及数据库资源库，但建议使用数据库作为资源库，文件库作为 
资源库使用过程中，经常会出现一些奇怪的问题，并且从数据备份角度来讲，不安全  
![资源库](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/资源库管理/14.png)    

- 告警监控   
告警监控主要采集作业任务或转换任务在某段时间内的执行细节，并将执行细节以异常记录的形式存储下来  
![告警监控](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/告警监控/11.png)     
便于后续任务调度过程中任务执行细节的跟踪，方便开发或运维人员精准找出任务异常原因    
![告警监控](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/告警监控/12.png)       

## 使用方式
   - 下载源码，自行搭建环境
   - 给你封装好插件，本地bat或shell脚本跑
   - 基于云端地址访问（需要购买阿里云或其他什么云服务器，要收费哦）  
   
### 1. 源码访问
- 下载 x-smart-kettle-server 后端应用 ，下载地址详见上述简介    
- 步骤一  
          将源码下载到本地，建议使用IDEA打开（Eclipse的没空研究，本人很久不再使用）,至于如何下载，如何导入至IDEA，
          作为一个专业的研发人员，这里不再赘述，直接进入到步骤二：  
 - 步骤二 
          配置一下maven的`setting.xml`，方便从本人的阿里云仓库下载相应的jar包，仓库地址配置为：
 ```
              <servers>
                  <server>
                      <id>rdc-releases</id>
                      <username>Y3z0VZ</username>
                      <password>Bb8byTSlq0</password>
                  </server>
                  <server>
                      <id>rdc-snapshots</id>
                      <username>Y3z0VZ</username>
                      <password>Bb8byTSlq0</password>
                  </server>
              </servers>
```
     ```
             <pluginRepository>
                              <id>snapshots</id>
                              <url>https://maven.aliyun.com/nexus/content/groups/public</url>
                              <releases>
                                  <enabled>false</enabled>
                              </releases>
                              <snapshots>
                                  <enabled>true</enabled>
                              </snapshots>
                          </pluginRepository>
                          <pluginRepository>
                              <id>rdc-releases</id>
                              <url>https://repo.rdc.aliyun.com/repository/128991-release-EJH8o1/</url>
                              <releases>
                                  <enabled>true</enabled>
                              </releases>
                              <snapshots>
                                  <enabled>false</enabled>
                              </snapshots>
                          </pluginRepository>
                          <pluginRepository>
                              <id>rdc-snapshots</id>
                              <url>https://repo.rdc.aliyun.com/repository/128991-snapshot-NY2Ub0/</url>
                              <releases>
                                  <enabled>false</enabled>
                              </releases>
                              <snapshots>
                                  <enabled>true</enabled>
                              </snapshots>
                          </pluginRepository>
                      </pluginRepositories>
                  </profile>
  ``` 
          内容不要更改，因为里面配置的是我本人的阿里云仓库地址，密码不会再改变，如果有变化，会在网站统一通知，届时，
          重新下载即可 ，仓库环境配置好之后，静静等待jar下载吧，等下载完毕，要去仓库检查一下是否有如下几个jar：  
```xml
          x1-simple-job-1.0.0-SNAPSHOT.jar
          x-kettle-core-1.0.0-SNAPSHOT.jar
          x-common-base-1.0.0-SNAPSHOT.jar
          x-common-pro-1.0.0-SNAPSHOT.jar
```
   如果本地仓库有如上几个jar，那么恭喜您，下载成功，接下来开始进入到步骤三：  
 - 步骤三  
        开始配置一下应用的yml文件，文件内容如下：  
 ```xml
 #配置服务器  
         server:
           port: ${XTL_APP_SERVER_PORT:9876}
           servlet:
             context-path: ${XTL_APP_SERVER_PATH:/xtl-server}
           #配置数据源
         spring:
           redis:
             enabled: ${XTL_REDIS_ENABLED:false}
             host: ${XTL_REDIS_HOST:127.0.0.1}
             port: ${XTL_REDIS_PORT:6379}
             password: ${XTL_REDIS_PASS:root}
             jedis:
               pool:
                 max-active: 8
                 max-wait: -1
                 max-idle: 500
                 min-idle: 0
             lettuce:
               shutdown-timeout: 0
           application:
             name: ${XTL_APP_NAME:xtl-app} #应用服务名称
           datasource:
             type: com.alibaba.druid.pool.DruidDataSource
             driver-class-name: com.mysql.jdbc.Driver
             #系统数据库访问地址【必填项】
             url: ${XTL_APP_DATASOURCE_URL:jdbc:mysql://localhost:3306/xtl?useUniCode=true&characterEncoding=UTF-8}
             #系统数据库用户名【必填项】
             username: ${XTL_APP_DATASOURCE_USERNAME:root}
             #系统数据库密码【必填项】
             password: ${XTL_APP_DATASOURCE_PASS:root}
             # 关闭sharding-jdbc 必须为false
           shardingsphere:
             enabled: false
         kettle:
           scheduler:
             #是否开启定时调度，默认为fals，则系统启动不会自动执行定时
             enabled: ${XTL_KETTLE_SCHEDULER:false}  #kettle定时调度启用为true,应用启动之后,自动将任务加入到定时器执行,设置为false则需要手动触发定时任务
           log:
             file:
               #日志物理路径【必填项】
               path: ${XTL_KETTLE_LOG_FILE_PATH:/xtl/kettle/logs} # 这个地方建议一定要配置一个存放目录,方便后期下载,查看历史执行记录,如果为"",则不会产生日志到服务器
               size: ${XTL_KETTLE_LOG_FILE_SIZE:10} # 控制日志文件的大小,默认是10M,超过10M则截断请求
           repo:
             # 自定义数据库资源库 使用之前必须先定义资源库【必填项】
             name: ${XTL_KETTLE_REPO_NAME:临时资源库} # 资源库名称【必填项】
             hostName: ${XTL_KETTLE_DB_HOST:localhost} # 数据库连接地址【必填项】
             dbPort: ${XTL_KETTLE_DB_PORT:3306} # 数据库端口 资源库目前仅支持MySQL【必填项】
             dbName: ${XTL_KETTLE_DB_NAME:etl} # 数据库实例名【必填项】
             userName: ${XTL_KETTLE_DB_USERNAME:root} #数据库用户名【必填项】
             passWord: ${XTL_KETTLE_DB_PASS:root} # 数据库密码【必填项】
             repoLoginName: ${XTL_KETTLE_REPO_LOGINNAME:admin} #资源库登录账户 默认admin【必填项】
             repoLoginPass: ${XTL_KETTLE_REPO_LOGINPASS:admin} #资源库登录密码 默认admin【必填项】
             # 该线程池会优先充满至最大的线程数（JDK默认优先将任务提交到队列，队列满了再充满至最大的线程数）
           pool:
             # 线程池前缀
             namePrefix: ${XTL_THREAD_POOL_PREFIX:kettleThreadPool}
             # 核心线程数
             coreThreads: ${XTL_THREAD_POOL_CORE:20}
             # 最大的线程数
             maxThreads: ${XTL_THREAD_POOL_MAX:50}
             # 队列容量
             queueCapacity: ${XTL_THREAD_POOL_QUEUE_CAPACITY:100}
             # 5分钟空闲则释放
             keepAliveTimeMin: ${XTL_THREAD_POOL_KEEPALIVE:5}
         logging:
           #系统日志存放路径
           path: ${XTL_APP_LOG_PATH:/maven/xtl-web-server/logs}
           level:
             root: ${XTL_APP_LOG_LEVEL:info}
 ``` 
        
   配置注释写的很清楚了，这里不再解释，如有不懂的地方，请留言
        
 - 步骤四 
        步骤三完成之后，需要在本地建立一个应用数据库，数据库脚本详见：doc->database->Mysql，包括建表语句及初始化数据  
        请自行在本地执行，并完善yml配置 。
 - 步骤五 
        步骤四完成之后，开始配置kettle资源库数据库，虽然本系统同时支持文件库以及数据库资源库，但还是强烈建议使用数据库作为  
        资源库，考虑数据移植方便性、安全性、高效性，使用数据库作为资源库，资源库请自行建立，并完善yml配置。
 - 步骤六 
        上述内容都配置好之后，这里运行：
        ```
            Start.java
        ```
        启动应用，并在浏览器访问：http://ip:port/xtl-server/swagger-ui.hml ,出现如下截图，那么恭喜您启动成功：
        ![启动截图](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/start.png)
        ![启动截图](http://github.com/yaukie/x-smart-kettle-server/raw/master/folder/start.png)
        ![启动截图](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/start.png)
        
- 下载 x-smart-kettle-font 前端应用 ，下载地址详见上述简介   
     - 步骤一  
          Smart Kettle 调度监控平台的前端部署，需要依赖NodeJs环境，请自行百度搜素下载、安装，这里不再赘述  
   --安装NodeJs  
   --安装Vue脚手架  
   --配置node环境变量  
 ```
              # clone the project
                     git clone http://open.inspur.com/yuenbin/x-smart-kettle-front.git
                     git clone https://gitee.com/yaukie/x-smart-kettle-front.git
                     git clone http://github.com/yaukie/x-smart-kettle-front.git
                     // install dependencies
                     npm install
                     // develop
                     npm run dev
 ```
   - 步骤二
   配置一下`vue-config.js` ,把后端服务器的地址换成您的地址即可
```xml
  devServer: {
    proxy: {
      "/xtl-server": {
         target: "http://localhost:9876/xtl-server/",
        pathRewrite: { "^/xtl-server": "" },
        changeOrigin: true
     }
   }
  }
```
   然后执行 `npm run dev` 本地启动应用，出现如下控制台打印的信息，则恭喜您前端也启动成功！  
           ![启动截图](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/start2.png)
           ![启动截图](https://gitee.com/yaukie/x-smart-kettle-server/raw/master/folder/start2.png)
           ![启动截图](http://github.com/yaukie/x-smart-kettle-server/raw/master/folder/start2.png)

### 2. 插件访问
TODO
### 3. 云端访问
 TODO
## 后续计划

## 相关模块

> 注意：模块不依赖于框架，可以独立使用。

- [x-common-base](http://github.com/yaukie/x-common-base) -- 基于Springboot自研的微服务架构
- [x-common-pro](http://github.com/yaukie/x-common-pro) -- 构建统一的jar包存放基础包
- [x-kettle-core](http://github.com/yaukie/x-kettle-core) -- 基于Kettle Api 打造的核心Kettle调用接口组件
- [x1-simple-job](http://github.com/yaukie/x1-simple-job) -- 基于Quartz的定时器调用插件
 
 ## 使用情况
当前该调度监控平台于2021年初正式上线，截止最新统计时间为止，Smart Kettle已接入的公司包括不限于：

     - 1、xx软件股份有限公司
                
   > 更多接入的公司，欢迎在 [登记地址](https://gitee.com/yaukie/x-smart-kettle-server/issues/I39TQV) 登记，登记仅仅为了产品推广。 
 
## 参考资料

- Vue 那点事儿：https://my.oschina.net/yaukie/blog/1547678
- Docker 那点事儿：https://my.oschina.net/yaukie/blog/3165074

## 官方交流
Smart Kettle 官方交流群①(500人)：668964239
Smart Kettle 官方交流群②(500人)：668964239

## 联系我们
联系邮箱： 869952837@qq.com
## 捐赠
No matter how much the donation amount is enough to express your thought, thank you very much ：）   
  [To donate](https://my.oschina.net/yaukie/blog/4968854)
无论捐赠金额多少都足够表达您这份心意，非常感谢 ：）    
  [前往捐赠](https://my.oschina.net/yaukie/blog/4968854)  