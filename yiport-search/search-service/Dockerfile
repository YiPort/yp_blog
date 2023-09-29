FROM openjdk:8-jdk-alpine
# 表示使用 JDK8 为基础镜像。
ARG JAR_FILE=target/*.jar
# 表示定义一个参数名为 JAR_FILE，值为 `target/*.jar` 的构建参数
COPY ${JAR_FILE} app.jar
# 表示把 target 目录下的 jar 文件复制一份新的到镜像内的 app.jar 文件
ENTRYPOINT ["java","-jar","/app.jar"]
# 表示通过 `java -jar` 的形式运行 `app.jar`，ENTRYPOINT 用来配置容器启动时的运行命令，第一个参数是运行命令，后面是一个一个参数。