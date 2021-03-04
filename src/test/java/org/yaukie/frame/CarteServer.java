//package org.yaukie.frame;
//
//import org.pentaho.di.cluster.SlaveServer;
//import org.pentaho.di.frame.KettleEnvironment;
//import org.pentaho.di.frame.exception.KettleException;
//import org.pentaho.di.job.Job;
//import org.pentaho.di.job.JobMeta;
//import org.pentaho.di.repository.Repository;
//
///**
// * @Author: yuenbin
// * @Date :2020/10/29
// * @Time :9:17
// * @Motto: It is better to be clear than to be clever !
// * @Destrib:
// **/
//public class CallJob {
//    // private void run(String hostname, int port) throws Exception {
//// SlaveServerConfig config = new SlaveServerConfig(hostname, port, false);
//// Carte.runCarte(config);
////
////
//// }
//
//    private Repository repository;
//    private SlaveServer remoteSlaveServer;
//    private boolean remoteFlag = false;
//    private String lastCarteObjectId;
//    private JobMeta jobMeta;
//    private Job job;
//
//    /**
//     *
//     * @param oracle资源库信息
//     *
//     *            初始化对象，初始化kettle环境和资源库
//     */
//    public CallJob(DBRepositoryInfo rposInfo) {
//        // TODO Auto-generated constructor stub
//        try {
//            // 初始化
//            KettleEnvironment.init();
//            this.initRepository(rposInfo);
//        } catch (KettleException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public SlaveServer getRemoteSlaveServer() {
//        return remoteSlaveServer;
//    }
//
//    /**
//     * 设置远程运行服务器
//     *
//     * @param remoteSlaveServer
//     */
//    public void setRemoteSlaveServer(SlaveServer remoteSlaveServer) {
//        this.remoteSlaveServer = remoteSlaveServer;
//    }
//
//    public boolean isRemoteFlag() {
//        return remoteFlag;
//    }
//
//    public void setRemoteFlag(boolean remoteFlag) {
//        this.remoteFlag = remoteFlag;
//    }
//
//    /**
//     * 初始化远程服务器
//     *
//     * @param slaveInfo
//     */
//    public void initSlaveServer(SlaveServerInfo slaveInfo) {
//        remoteSlaveServer = new SlaveServer();
//        remoteSlaveServer.setHostname(slaveInfo.getServerHost());
//        remoteSlaveServer.setMaster(true);
//        remoteSlaveServer.setPort(slaveInfo.getServerPort());
//        remoteSlaveServer.setUsername(slaveInfo.getServerUsername());
//        remoteSlaveServer.setPassword(slaveInfo.getServerPassword());
//    }
//
//    /**
//     * 初始化资源库
//     *
//     * @param rposInfo
//     * @throws KettleException
//     */
//    public void initRepository(DBRepositoryInfo rposInfo) throws KettleException {
//
//        // 新建数据库资源库
//        repository = new KettleDatabaseRepository();
//        // 建立数据库连接
//        DatabaseMeta databaseMeta = new DatabaseMeta(rposInfo.getDbName(), rposInfo.getDbType(), "Native",
//                rposInfo.getDbHostname(), rposInfo.getDbName(), rposInfo.getDbPort(), rposInfo.getDbUsername(),
//                rposInfo.getDbPassword());
//        // 建立资源库信息
//        KettleDatabaseRepositoryMeta kettleDatabaseMeta = new KettleDatabaseRepositoryMeta(rposInfo.getRepoId(),
//                rposInfo.getRepoName(), "Transformation description", databaseMeta);
//        // 初始化资源库
//        repository.init(kettleDatabaseMeta);
//        // 连接资源库
//        repository.connect(rposInfo.getRepoUsername(), rposInfo.getRepoPassword());
//        // 资源库目录
//    }
//
//    /**
//     * 停止远程执行的任务
//     *
//     * @param Transname
//     * @param carteObjectid
//     */
//
//    public void stopRemoteJob(String Transname, String carteObjectid) {
//        try {
//            remoteSlaveServer.stopJob(Transname, carteObjectid);
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 停止 执行的Job
//     *
//     * @param Transname
//     * @param carteObjectid
//     */
//
//    public void stopLacalJob() {
//        try {
//            job.stopAll();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    public void suspendLocalJob() {
//
//        try {
//            job.suspend();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    public void resumeLocalJob() {
//        try {
//            job.resume();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public String getLocalJobStatus() {
//
//        return job.getStatus();
//    }
//
//    public String getRemoteJobStatus() {
//        SlaveServerJobStatus jobStatus = null;
//
//        try {
//            jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), lastCarteObjectId, 0);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return jobStatus.toString();
//    }
//
//    /**
//     * 远程执行任务
//     *
//     * @param jobNmae
//     * @throws KettleException
//     */
//
//    public void executeJobRemote(String dir, String jobName) throws KettleException {
//
//        RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree(); // Default
//        // =
//        RepositoryDirectoryInterface jobdir = repository.findDirectory(dir);
//
//        JobMeta jobMeta = repository.loadJob(jobName, jobdir, null, null); // reads
//        // last
//        // version
//
//        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
//
//        jobExecutionConfiguration.setRemoteServer(remoteSlaveServer);
//        jobExecutionConfiguration.setRepository(repository);
//
//        // lastCarteObjectId = Job.sendToSlaveServer(jobMeta,
//        // jobExecutionConfiguration, repository);
//        // IMetaStore metastore = new IMetaStore();
//        lastCarteObjectId = Job.sendToSlaveServer(jobMeta, jobExecutionConfiguration, repository, null);
//        SlaveServerJobStatus jobStatus = null;
//
//        Result oneResult = new Result();
//
//        while (true) {
//            try {
//                jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), lastCarteObjectId, 0);
//
//                if (jobStatus.getResult() != null) {
//                    // The job is finished, get the result...
//                    //
//                    oneResult = jobStatus.getResult();
//
//                    break;
//                }
//            } catch (Exception e1) {
//
//                oneResult.setNrErrors(1L);
//                break; // Stop looking too, chances are too low the server
//                // will
//                // come back on-line
//            }
//        }
//
//    }
//
//    public String getLastCarteObjectId() {
//        return lastCarteObjectId;
//    }
//
//    /**
//     * 本地执行job
//     *
//     * @param jobNmae
//     * @throws KettleException
//     */
//
//    public Result executeJobLocal(String jobName) throws KettleException {
//
//        RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree(); // Default
//        // =
//        // root
//        JobMeta jobMeta;
//
//        jobMeta = repository.loadJob(jobName, directory, null, null); // reads
//
//        Result oneResult = new Result();
//        job = new Job(repository, jobMeta);
//
//        job.start();
//
//        job.waitUntilFinished();
//        oneResult = job.getResult();
//        return oneResult;
//    }
//
//    public static void main(String[] args) {
//        //基于数据库资源库方式
//        DBRepositoryInfo rposInfo = new DBRepositoryInfo();
//        rposInfo.setDbHostname("192.168.70.227");
//        rposInfo.setDbName("kettle");
//        rposInfo.setDbPort("3306");
//        rposInfo.setDbType("MYSQL");
//        rposInfo.setDbUsername("root");
//        rposInfo.setDbPassword("admin");
//        rposInfo.setRepoId("kettle_repo_mysql");
//        rposInfo.setRepoName("kettle_repo_mysql");
//
//        rposInfo.setRepoPassword("admin");
//        rposInfo.setRepoUsername("admin");
//
//        CallJob ctf = new CallJob(rposInfo);
//        SlaveServerInfo ssi = new SlaveServerInfo();
//        ssi.setServerHost("localhost");
//        ssi.setServerPort("8080");
//        ssi.setServerName("master1");
//        ssi.setServerUsername("cluster");
//        ssi.setServerPassword("cluster");
//        ctf.initSlaveServer(ssi);
//
//        try {
//            ctf.executeJobRemote("/frame", "job_test");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }// private void run(String hostname, int port) throws Exception {
//// SlaveServerConfig config = new SlaveServerConfig(hostname, port, false);
//// Carte.runCarte(config);
////
////
//// }
//
//    private Repository repository;
//    private SlaveServer remoteSlaveServer;
//    private boolean remoteFlag = false;
//    private String lastCarteObjectId;
//    private JobMeta jobMeta;
//    private Job job;
//
//    /**
//     *
//     * @param oracle资源库信息
//     *
//     *            初始化对象，初始化kettle环境和资源库
//     */
//    public CallJob(DBRepositoryInfo rposInfo) {
//        // TODO Auto-generated constructor stub
//        try {
//            // 初始化
//            KettleEnvironment.init();
//            this.initRepository(rposInfo);
//        } catch (KettleException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public SlaveServer getRemoteSlaveServer() {
//        return remoteSlaveServer;
//    }
//
//    /**
//     * 设置远程运行服务器
//     *
//     * @param remoteSlaveServer
//     */
//    public void setRemoteSlaveServer(SlaveServer remoteSlaveServer) {
//        this.remoteSlaveServer = remoteSlaveServer;
//    }
//
//    public boolean isRemoteFlag() {
//        return remoteFlag;
//    }
//
//    public void setRemoteFlag(boolean remoteFlag) {
//        this.remoteFlag = remoteFlag;
//    }
//
//    /**
//     * 初始化远程服务器
//     *
//     * @param slaveInfo
//     */
//    public void initSlaveServer(SlaveServerInfo slaveInfo) {
//        remoteSlaveServer = new SlaveServer();
//        remoteSlaveServer.setHostname(slaveInfo.getServerHost());
//        remoteSlaveServer.setMaster(true);
//        remoteSlaveServer.setPort(slaveInfo.getServerPort());
//        remoteSlaveServer.setUsername(slaveInfo.getServerUsername());
//        remoteSlaveServer.setPassword(slaveInfo.getServerPassword());
//    }
//
//    /**
//     * 初始化资源库
//     *
//     * @param rposInfo
//     * @throws KettleException
//     */
//    public void initRepository(DBRepositoryInfo rposInfo) throws KettleException {
//
//        // 新建数据库资源库
//        repository = new KettleDatabaseRepository();
//        // 建立数据库连接
//        DatabaseMeta databaseMeta = new DatabaseMeta(rposInfo.getDbName(), rposInfo.getDbType(), "Native",
//                rposInfo.getDbHostname(), rposInfo.getDbName(), rposInfo.getDbPort(), rposInfo.getDbUsername(),
//                rposInfo.getDbPassword());
//        // 建立资源库信息
//        KettleDatabaseRepositoryMeta kettleDatabaseMeta = new KettleDatabaseRepositoryMeta(rposInfo.getRepoId(),
//                rposInfo.getRepoName(), "Transformation description", databaseMeta);
//        // 初始化资源库
//        repository.init(kettleDatabaseMeta);
//        // 连接资源库
//        repository.connect(rposInfo.getRepoUsername(), rposInfo.getRepoPassword());
//        // 资源库目录
//    }
//
//    /**
//     * 停止远程执行的任务
//     *
//     * @param Transname
//     * @param carteObjectid
//     */
//
//    public void stopRemoteJob(String Transname, String carteObjectid) {
//        try {
//            remoteSlaveServer.stopJob(Transname, carteObjectid);
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 停止 执行的Job
//     *
//     * @param Transname
//     * @param carteObjectid
//     */
//
//    public void stopLacalJob() {
//        try {
//            job.stopAll();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    public void suspendLocalJob() {
//
//        try {
//            job.suspend();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    public void resumeLocalJob() {
//        try {
//            job.resume();
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    public String getLocalJobStatus() {
//
//        return job.getStatus();
//    }
//
//    public String getRemoteJobStatus() {
//        SlaveServerJobStatus jobStatus = null;
//
//        try {
//            jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), lastCarteObjectId, 0);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//
//        return jobStatus.toString();
//    }
//
//    /**
//     * 远程执行任务
//     *
//     * @param jobNmae
//     * @throws KettleException
//     */
//
//    public void executeJobRemote(String dir, String jobName) throws KettleException {
//
//        RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree(); // Default
//        // =
//        RepositoryDirectoryInterface jobdir = repository.findDirectory(dir);
//
//        JobMeta jobMeta = repository.loadJob(jobName, jobdir, null, null); // reads
//        // last
//        // version
//
//        JobExecutionConfiguration jobExecutionConfiguration = new JobExecutionConfiguration();
//
//        jobExecutionConfiguration.setRemoteServer(remoteSlaveServer);
//        jobExecutionConfiguration.setRepository(repository);
//
//        // lastCarteObjectId = Job.sendToSlaveServer(jobMeta,
//        // jobExecutionConfiguration, repository);
//        // IMetaStore metastore = new IMetaStore();
//        lastCarteObjectId = Job.sendToSlaveServer(jobMeta, jobExecutionConfiguration, repository, null);
//        SlaveServerJobStatus jobStatus = null;
//
//        Result oneResult = new Result();
//
//        while (true) {
//            try {
//                jobStatus = remoteSlaveServer.getJobStatus(jobMeta.getName(), lastCarteObjectId, 0);
//
//                if (jobStatus.getResult() != null) {
//                    // The job is finished, get the result...
//                    //
//                    oneResult = jobStatus.getResult();
//
//                    break;
//                }
//            } catch (Exception e1) {
//
//                oneResult.setNrErrors(1L);
//                break; // Stop looking too, chances are too low the server
//                // will
//                // come back on-line
//            }
//        }
//
//    }
//
//    public String getLastCarteObjectId() {
//        return lastCarteObjectId;
//    }
//
//    /**
//     * 本地执行job
//     *
//     * @param jobNmae
//     * @throws KettleException
//     */
//
//    public Result executeJobLocal(String jobName) throws KettleException {
//
//        RepositoryDirectoryInterface directory = repository.loadRepositoryDirectoryTree(); // Default
//        // =
//        // root
//        JobMeta jobMeta;
//
//        jobMeta = repository.loadJob(jobName, directory, null, null); // reads
//
//        Result oneResult = new Result();
//        job = new Job(repository, jobMeta);
//
//        job.start();
//
//        job.waitUntilFinished();
//        oneResult = job.getResult();
//        return oneResult;
//    }
//
//    public static void main(String[] args) {
//        //基于数据库资源库方式
//        DBRepositoryInfo rposInfo = new DBRepositoryInfo();
//        rposInfo.setDbHostname("192.168.70.227");
//        rposInfo.setDbName("kettle");
//        rposInfo.setDbPort("3306");
//        rposInfo.setDbType("MYSQL");
//        rposInfo.setDbUsername("root");
//        rposInfo.setDbPassword("admin");
//        rposInfo.setRepoId("kettle_repo_mysql");
//        rposInfo.setRepoName("kettle_repo_mysql");
//
//        rposInfo.setRepoPassword("admin");
//        rposInfo.setRepoUsername("admin");
//
//        CallJob ctf = new CallJob(rposInfo);
//        SlaveServerInfo ssi = new SlaveServerInfo();
//        ssi.setServerHost("localhost");
//        ssi.setServerPort("8080");
//        ssi.setServerName("master1");
//        ssi.setServerUsername("cluster");
//        ssi.setServerPassword("cluster");
//        ctf.initSlaveServer(ssi);
//
//        try {
//            ctf.executeJobRemote("/frame", "job_test");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
