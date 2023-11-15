package com.yiport.handler.mybatisplus;

import com.yiport.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId = null;

        userId = SecurityUtils.getUserId();
        // 获取时间戳,设置创建时间
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        this.setFieldValByName("createTime", createTime, metaObject);
        this.setFieldValByName("createBy", userId, metaObject);
        this.setFieldValByName("updateTime", createTime, metaObject);
        this.setFieldValByName("updateBy", userId, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        // 获取时间戳,设置创建时间
        String createTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        this.setFieldValByName("updateTime", createTime, metaObject);
        this.setFieldValByName(" ", SecurityUtils.getUserId(), metaObject);
    }
}