# 配置文件

## Nacos文件

### 定制

<details>
<summary>主体服务-展开查看详情</summary>

[跳转进入文件](./blog-service.yaml )

```yaml
user-service:
  ribbon:
    NFLoadBalancerRuleClassName: com.alibaba.cloud.nacos.ribbon.NacosRule # 负载均衡规则

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

[跳转进入文件](./user-service.yaml )

```yaml
server:
  port: 7989
  # session 失效时间
  session:
    timeout: 60m
```

</details>

<details>
<summary>搜索服务-展开查看详情</summary>

[跳转进入文件](./blog-service.yaml )

```yaml
server:
  port: 21010
spring:
  elasticsearch:
    uris: "http://192.168.10.123:19200"
    username: "elastic"
    password: "password"
```

</details>


<details>
<summary>资源服务-展开查看详情</summary>

[跳转进入文件](./blog-service.yaml )

```yaml
server:
  port: 8001
spring:
  servlet:
    multipart:
      max-file-size: 5MB #限定文件上传大小
      max-request-size: 10MB
```

</details>

<details>
<summary>网关服务-展开查看详情</summary>

[跳转进入文件](./gateway-service.yaml )

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
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/user/**,
          filters: # 过滤器
            # - AddRequestHeader=Ye,Ye is freaking awesome!
            - StripPrefix=0
        - id: resource-service
          uri: lb://resource-service
          predicates:
            - Path=/resource/**,
          filters: # 过滤器
            # - AddRequestHeader=Ye,Ye is freaking awesome!
            - StripPrefix=1
        - id: blog-service
          uri: lb://blog-service
          predicates:
            - Path=/blog/**,
          filters: # 过滤器
            # - AddRequestHeader=Ye,Ye is freaking awesome!
            - StripPrefix=1
        - id: search-service
          uri: lb://search-service
          predicates:
            - Path=/search/**,
          filters: # 过滤器
            # - AddRequestHeader=Ye,Ye is freaking awesome!
            - StripPrefix=0

logging:
  level:
    org.springframework.cloud.gateway: INFO
    #日志级别  OFF > FATAL > ERROR > WARN > INFO > DEBUG > TRACE > ALL
```

</details>

### 共享

<details>
<summary>MySQL</summary>

[跳转进入文件](./default-mysql.yaml )

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/yp_blog?characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: "root"
    password: "000000"
    driver-class-name: com.mysql.cj.jdbc.Driver
    type: com.alibaba.druid.pool.DruidDataSource
    # Druid连接池配置
    druid:
      initial-size: 5 # 初始化时建立物理连接的个数
      max-active: 20  # 最大连接池数量
      min-idle: 5     # 最小连接池数量
      maxWait: 2000   # 获取连接时最大等待时间，单位毫秒
      time-between-eviction-runs-millis: 60000 #配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      min-evictable-idle-time-millis: 300000 #连接保持空闲而不被驱逐的最小时间
      validation-query: select 1 #用来检测连接是否有效的sql 必须是一个查询语句：mysql中为 select 'x' oracle中为 select 1 from dual
      test-while-idle: true #建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
      test-on-borrow: false #申请连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
      test-on-return: false #归还连接时会执行validationQuery检测连接是否有效,开启会降低性能,默认为true
      pool-prepared-statements: true #是否缓存preparedStatement,mysql5.5+建议开启
      max-pool-prepared-statement-per-connection-size: 100 #要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
      filters: stat,wall #配置监控统计拦截的filters，去掉后监控界面sql无法统计
      filter:
        # 开启druidDataSource状态监控
        stat:
          enabled: true
          db-type: mysql
          # 开启慢SQL监控，当SQL执行时间超过两秒认为是慢SQL记录到日志中
          log-slow-sql: true
          slow-sql-millis: 2000
      connection-properties: druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500 #通过connectProperties属性来打开mergeSql功能；慢SQL记录
      use-global-data-source-stat: true #合并多个DruidDataSource的监控数据
      # StatViewServlet配置
      stat-view-servlet.enabled: true #是否启用StatViewServlet（监控页面）默认值为false
      stat-view-servlet.login-username: admin #设置访问druid监控页的账号,默认没有
      stat-view-servlet.login-password: passwd #设置访问druid监控页的密码,默认没有
      web-stat-filter:
        enabled: true   # 开启statFilter
        allow: 127.0.0.1
        url-pattern: /*                 # 过滤所有url
        exclusions: "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*" # 排除一些不必要的url
        session-stat-enable: true   #开启session统计功能
```

</details>

<details>
<summary>Redis</summary>

[跳转进入文件](./default-redis-dev.yaml )

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
<summary>Mybatis-Plus</summary>

[跳转进入文件](./default-mybatis-plus.yaml )

```yaml
mybatis-plus:
  # 对应的 XML 文件位置
  mapperLocations: classpath*:mapper/**/*Mapper.xml
  configuration:
    # MyBatis 自动映射策略
    # NONE：不启用 PARTIAL：只对非嵌套 resultMap 自动映射 FULL：对所有 resultMap 自动映射
    autoMappingBehavior: PARTIAL
    # MyBatis 自动映射时未知列或未知属性处理策
    # NONE：不做处理 WARNING：打印相关警告 FAILING：抛出异常和详细信息
    autoMappingUnknownColumnBehavior: NONE
    mapUnderscoreToCamelCase: true # 开启自动驼峰命名转换

    # 日志
    # log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
    log-impl: org.apache.ibatis.logging.nologging.NoLoggingImpl

  global-config:
    db-config:
      logic-delete-field: delFlag
      logic-delete-value: 1
      logic-not-delete-value: 0
      id-type: auto
```

</details>

<details>
<summary>Upload-Files</summary>

[跳转进入文件](./default-upload-files.yaml )

```yaml
oss:  #七牛云OSS
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

minio:
  # url: http://files.yiport.top:18110
  url: http://192.168.10.123:9002
  username: miniouser
  password: miniopasswd
  bucket: yp-blog
```

</details>

<details>
<summary>Feign</summary>

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
</details>

<details>
<summary>Log</summary>

[跳转进入文件](./default-log.yaml )

```yaml
# 日志配置
logging:
  level:
    com.yiport: debug
    org.springframework: warn
  config: classpath:logback.xml
```
</details>


<details>
<summary>Security</summary>

[跳转进入文件](./default-security.yaml )

```yaml
# security配置
security:
  # 拦截路径
  Intercepts:
    - /**
  #    - /article/postArticle
  #    - /article/getDraft
  #    - /article/getEditHistory
  #    - /article/deleteDraft/**
  #    - /article/getMyArticleTotal
  #    - /article/getTotalView
  #    - /category/addCategory
  #    - /collection/addCollection/**
  #    - /collection/getCollectList
  #    - /collection/deleteCollection/**
  #    - /comment/saveComment
  #    - /comment/allCommentList/**
  #    - /comment/setCommentTop/**
  #    - /index/postArticleIndex
  #    - /question/postQuestion
  #    - /question/getQuestionList
  #    - /question/deleteQuestion/**
  # 排除路径
  excludes:
    - /article/hotArticleList
    - /article/latestArticleList
    - /article/articleList
    - /article/articleDetail/**
    - /article/updateViewCount/**
    - /user/captchaImage
    - /user/login/**
    - /user/register
    - /mail/sendRetrieveAccountCaptcha
    - /mail/retrieveAccount
    - /mail/sendUpdatePasswordCaptcha
    - /mail/updatePasswordByMail
    - /search/index/getArticleIndex
    - /search/searchArticle
    - /category/getCategoryList
    - /comment/commentList
    - /comment/linkCommentList
    - /link/getAllLink
    - /index/getArticleIndex
    # 静态资源
    - /*.html
    - /**/*.html
    - /**/*.css
    - /**/*.js
    # swagger 文档配置
    - /favicon.ico
    - /doc.html
    - /swagger-resources/**
    - /webjars/**
    - /*/api-docs
    # druid 监控配置
    - /druid/**
```
</details>


<details>
<summary>RabbitMQ</summary>

[跳转进入文件](./default-rabbitmq.yaml )

```yaml
spring:
  rabbitmq:
    host: 192.168.10.123 # 主机名
    port: 5672 # 端口
    virtual-host: ypblog # 虚拟主机
    username: admin # 用户名
    password: 123 # 密码
    listener:
      simple:
        prefetch: 1 # 每次只能获取一条消息，处理完成才能获取下一个消息
```
</details>


<details>
<summary>Mail</summary>

[跳转进入文件](./default-mail.yaml )

```yaml
spring:
  #邮箱基本配置（自定义域名邮箱）
  mail:
    host: smtp.yiport.top
    #发送者邮箱
    username: blog@yiport.top
    #配置密码,注意不是真正的密码，而是申请到的授权码
    password: password
    port: 465
    #默认的邮件编码为UTF-8
    default-encoding: UTF-8
    properties:
      mail:
        #配置SSL 加密工厂
        smtp:
          ssl:
            enable: true
        debug: true
    #邮箱基本配置（网易）
    # mail:
    #   host: smtp.163.com
    #   #发送者邮箱
    #   username: xxx@163.com
    #   #配置密码,注意不是真正的密码，而是申请到的授权码
    #   password: xxx
    #   port: 465
    #   #默认的邮件编码为UTF-8
    #   default-encoding: UTF-8
    #   properties:
    #     mail:
    #       #配置SSL 加密工厂
    #       smtp:
    #         ssl:
    #           enable: true
    #       debug: true
    
    #邮箱基本配置（qq）
#    mail:
#      host: smtp.qq.com
#      #发送者邮箱
#      username: xxx@qq.com
#      #配置密码,注意不是真正的密码，而是申请到的授权码
#      password: xxx
#      #端口号465或587
#      port: 587
#      #默认的邮件编码为UTF-8
#      default-encoding: UTF-8
#      #其他参数
#      properties:
#        mail:
#          #配置SSL 加密工厂
#          smtp:
#            starttls:
#              enable: true
#          debug: true
```
</details>
