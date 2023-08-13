# 用户服务接口文档

[返回目录](./README.md)

## 1.登录

### 1.1 需求

> 有些功能必须登录后才能使用，未登录状态是不能使用的。

### 1.2 接口设计

| 请求方式 | 请求路径        |
|------|-------------|
| POST | /user/login |

请求体：

```json
{
  "username": "yiport",
  "password": "12345678",
  "captcha": "30",
  "uuid": "bf5ccbad2bc74327b127c34244b4f06a"
}
```

响应格式：

```json
{
  "code": 200,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI3Y2M4NDUxNjU3OGU0NWExYWEzZjQ4Mjg2NGM2NmFjYSIsInN1YiI6IjEiLCJpc3MiOiJ5cCIsImlhdCI6MTY5NTUwMDUyNCwiZXhwIjoxNjk1NTg2OTI0fQ.LxiCy3aqDEFGilc_OTK26QyZXnXu8xWh3lxgvJUxN1M",
    "userInfo": {
      "avatar": "http://power-api.cretinzp.com:8000/girls/35/6pvpk8fcs3tjojpq.jpg",
      "createTime": "2023-04-06 09:01:56",
      "email": "2727@qq.com",
      "id": "1",
      "nickName": "yiport",
      "sex": "1",
      "userName": "yiport",
      "userRole": "1"
    }
  },
  "msg": "操作成功"
}
```

## 2.退出登录

### 2.1 需求

> 用户退出登录,并且删除redis中的用户信息

### 2.2 接口设计

| 请求方式 | 请求路径         | 请求头        |
|------|--------------|------------|
| POST | /user/logout | 需要token请求头 |

