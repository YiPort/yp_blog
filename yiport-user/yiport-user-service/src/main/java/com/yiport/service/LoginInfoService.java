package com.yiport.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yiport.domain.entity.LoginInfo;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录信息 服务层
 */
public interface LoginInfoService extends IService<LoginInfo>
{
    /**
     * 登录信息记录
     */
    void recordLoginInfo(String userName, String status, String message, HttpServletRequest request);

    /**
     * 新增登录日志
     *
     * @param loginInfo 访问日志对象
     */
    String insertLoginInfo(LoginInfo loginInfo);


}
