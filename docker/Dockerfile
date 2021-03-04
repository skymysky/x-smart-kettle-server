#基础镜像
FROM openjdk:12
#作者信息
MAINTAINER yuenbin <yaukie@163.com>
#挂在目录
VOLUME /opt/aps/tmp:/tmp
#在构建镜像时，指定镜像的工作目录，之后的命令都是基于此工作目录，如果不存在，则会创建目录
WORKDIR /java_docker/jdk
#复制jar包到指定目录
COPY *.jar /java_docker/app.jar
#复制JDK安装包
ADD jdk1.8.0_121.tar.gz /java_docker/jdk/
#配置JDK环境变量
ENV JAVA_HOME=/java_docker/jdk/jdk1.8.0_121
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH=$JAVA_HOME/bin:$PATH
#容器启动时需要执行的命令
CMD ["java","-version"]
#启动容器后执行如下命令,启动容器
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/java_docker/app.jar"]
#暴露端口
EXPOSE 8099
