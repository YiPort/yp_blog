# 配置文件

## Nacos文件

### 定制

<details>
<summary>主体服务-展开查看详情</summary>

[跳转进入文件](./blogservice.yaml )

```yaml
server:
  port: 7777
spring:
  servlet:
    multipart:
      max-file-size: 1MB #限定文件上传大小
      max-request-size: 5MB
```

</details>

<details>
<summary>用户服务-展开查看详情</summary>

[跳转进入文件](./userservice.yaml )

```yaml
server:
  port: 7989
  # session 失效时间
  session:
    timeout: 60m
spring:
  servlet:
    multipart:
      max-file-size: 2MB
      max-request-size: 5MB

```

</details>

<details>
<summary>网关服务-展开查看详情</summary>

[跳转进入文件](./gatewayservice.yaml )

```yaml
server:
  port: 10086

spring:
  cloud:
    gateway:
      default-filters:
        - DedupeResponseHeader=Vary Access-Control-Allow-Origin Access-Control-Allow-Credentials, RETAIN_FIRST
      discovery:
        locator:
          enabled: true # gateway可以通过开启以下配置来打开根据服务的serviceId来匹配路由,默认是大写
      globalcors: # 全局的跨域处理
        add-to-simple-url-handler-mapping: true # 解决options请求被拦截问题
        corsConfigurations:
          '[/**]':
            # allowedOrigins: # 允许哪些网站的跨域请求
            #   - "127.0.0.1:8080"
            allowedOriginPatterns: # 允许哪些网站的跨域请求
              - "*"
            allowedMethods: # 允许的跨域ajax的请求方式
              - "GET"
              - "POST"
              - "DELETE"
              - "PUT"
              - "OPTIONS"
            allowedHeaders: "*" # 允许在请求中携带的头信息
            allowCredentials: true # 是否允许携带cookie
            maxAge: 360000 # 这次跨域检测的有效期
      # httpserver:
      #   wiretap: true

      routes:
        - id: userservice
          uri: lb://userservice
          predicates:
            - Path=/user/**,
          filters: # 过滤器
            # - AddRequestHeader=Ye,Ye is freaking awesome!
            - StripPrefix=0
        - id: blogservice
          uri: lb://blogservice
          predicates:
            - Path=/blog/**,
          filters: # 过滤器
            - AddRequestHeader=Ye,Ye is freaking awesome!
            - StripPrefix=0


logging:
  level:
    org.springframework.cloud.gateway: INFO
    #日志级别  OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
```

</details>

### 共享

<details>
<summary>mysql</summary>

[跳转进入文件](./default-mysql.yaml )

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yp_blog?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: "root"
    password: "000000"
    driver-class-name: com.mysql.cj.jdbc.Driver

```

</details>

<details>
<summary>redis</summary>

[跳转进入文件](./default-redis.yaml )

```yaml
spring:
  redis:
    host: 127.0.0.1
    port: 6379
    database: 1
    password: password
```

</details>

<details>
<summary>mybatis-plus</summary>

[跳转进入文件](./default-mybatis-plus.yaml )

```yaml
mybatis-plus:
  configuration:
    # 日志
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0
      id-type: auto
```

</details>

<details>
<summary>upload-files</summary>

[跳转进入文件](./default-upload-files.yaml )

```yaml
oss: #七牛云OSS
  accessKey: xxxx
  secretKey: xxxx
  bucket: yp-blog

fdfs: #FastDFS
  pool:
    max-total: 200 ## 连接池最大数量
    max-total-per-key: 50  ## 每个tracker地址的最大连接数
    max-wait-millis: 20000  ##连接耗尽时等待获取连接的最大毫秒数
  so-timeout: 1501 # 超时时间
  connect-timeout: 601 # 连接超时时间
  thumb-image: # 缩略图
    width: 60
    height: 60
  tracker-list: # tracker地址：你的虚拟机服务器地址+端口（默认是22122）
    - 192.168.10.123:22122
```

</details>

<details>
<summary>feign</summary>

[跳转进入文件](./default-feign.yaml )

```yaml
feign:
  hystrix:
    enabled: true
  client:
    config:
      userservice: # 针对某个微服务的配置
        loggerLevel: FULL #  日志级别 记录所有请求和响应的明细，包括头信息、请求体、元数据。
      blogservice:
        loggerLevel: HEADERS  #在BASIC的基础上，额外记录了请求和响应的头信息
      default: # 这里用default就是全局配置，如果是写服务名称，则是针对某个微服务的配置
        loggerLevel: BASIC # 日志级别，BASIC就是基本的请求和响应信息
  httpclient:
    enabled: true # 开启feign对HttpClient的支持
    max-connections: 200 # 最大的连接数
    max-connections-per-route: 50 # 每个路径的最大连接数

hystrix:
  command:
    default:
      execution:
        isolation:
          strategy: SEMAPHORE
```


