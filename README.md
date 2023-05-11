<p align="center">
  <h3 align="center">YiPortBlog</h3>
  <p align="center">
    🚀欢迎使用 YiPortBlog！
    <br/> 项目搭建中，快来和YiPort一起搭建博客吧！
    <br/>
    <br/>
    <a href="https://github.com/YiPort/yp_blog_frontend"><strong>博客前端项目 »</strong></a>
    <br/>
    <a href="https://github.com/YiPort/yp_blog/"><strong>博客后端项目 »</strong></a>
    <br/>
  </p>

[![JDK](https://img.shields.io/badge/JDK-1.8%2B-brightgreen)](https://github.com/YiPort/yp_blog)
[![SpringBoot](https://img.shields.io/badge/SpringBoot-2.5.0.RELEASE-brightgreen)](https://github.com/YiPort/yp_blog)
[![MySQL](https://img.shields.io/badge/MySQL-5.7.36-brightgreen)](https://github.com/YiPort/yp_blog)
[![vue](https://img.shields.io/badge/Vue-2.5.2-brightgreen)](https://github.com/YiPort/yp_blog_frontend)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.4.3-brightgreen)](https://github.com/YiPort/yp_blog)
[![Redis](https://img.shields.io/badge/Redis-6.2.6-brightgreen)](https://github.com/YiPort/yp_blog)
![Github stars](https://badgen.net/github/stars/YiPort/yp_blog?icon=github&label=stars)
---
## 目录结构

SQL文件位于根目录下 [sql](https://github.com/YiPort/yp_blog/tree/main/sql) 文件夹下面

```
yiport-blog          --  后端模块
└── controller       --  控制器模块

yiport-framework     --  公共模块
├── config           --  配置模块
├── constants        --  常量模块
├── domain
│      ├── entity    --  实体类模块      
│      └── vo        --  vo模块
├── enums            --  枚举模块
├── filter           --  过滤器模块
├── handler          --  处理器模块
├── mapper           --  框架核心模块
├── service          --  服务模块
└── utils            --  工具类模块
```
## 项目特点
- 采用 Restful 风格的 API，注释完善，代码遵循阿里巴巴开发规范，有利于开发者学习
- 为解决多个子系统内代码大量重复的问题，抽象模型层和业务层代码为公共模块

## 技术介绍
Spring Boot + MyBatis-Plus + Mysql + Redis + vue

## 运行环境

**服务器环境：** CentOS7.6

## 开发环境

| 开发工具                          | 说明               |
|-------------------------------|------------------|
| IDEA                          | Java开发工具IDE      |
| VSCode                        | Vue开发工具IDE       |
| Navicat                       | MySQL远程连接工具      |
| Xshell                        | Linux 远程连接工具 |
| Xftp                          | Linux 文件上传工具 |


|    开发环境    | 版本     |
| ------------- |--------|
| JDK           | 1.8    |
| MySQL         | 5.7.36 |
| Redis         | 6.2.6  |