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


