package com.yiport.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yiport.domain.entity.LoginInfo;
import com.yiport.domain.entity.User;
import com.yiport.mapper.LoginInfoMapper;
import com.yiport.mapper.UserMapper;
import com.yiport.service.LoginInfoService;
import com.yiport.service.MailCommonService;
import com.yiport.utils.AddressUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

import static com.yiport.constent.UserConstant.LOGIN_BY_ACCOUNT;
import static com.yiport.constent.UserConstant.LOGIN_SUCCESS;
import static com.yiport.constent.UserConstant.SUCCESS;

/**
 * 登录信息 业务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginInfoServiceImpl extends ServiceImpl<LoginInfoMapper, LoginInfo>
        implements LoginInfoService
{
    private final MailCommonService mailCommonService;
    private final LoginInfoMapper loginInfoMapper;
    private final AddressUtils addressUtils;

    /**
     * 登录信息记录
     *
     *      * @param userAccount 账号
     *      * @param status      访问状态
     *      * @param message     详细消息
     *      * @param type        登录类型
     */
    @Async
    @Override
    public void recordLoginInfo(String userName, String status, String message, String type, HttpServletRequest request)
    {
        final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        final String ip = ServletUtil.getClientIP(request);
        SpringUtil.getBean(LoginInfoServiceImpl.class)
                .asyncRecordLog(userName, status, message, type, userAgent, ip);
    }

    /**
     * 新增登录日志
     *
     * @param loginInfo 访问日志对象
     */
    @Override
    public String insertLoginInfo(LoginInfo loginInfo)
    {
        Date date = new Date();
        loginInfo.setLoginTime(date);
        loginInfoMapper.insert(loginInfo);
        return DateUtil.formatDateTime(date);
    }

    /**
     * 异步记录登录信息
     *
     * @param userName 账号
     * @param status      访问状态
     * @param message     详细消息
     * @param userAgent   userAgent
     * @param ip          ip
     */
    @Async
    public void asyncRecordLog(final String userName, final String status, final String message, final String type,
                               final UserAgent userAgent, final String ip, final Object... args)
    {
        // 获取地址
        String address = addressUtils.getRealAddressByIP(ip);
        // 获取客户端操作系统
        String os = userAgent.getOs().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();
        // 封装对象
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUserName(userName);
        loginInfo.setIpAddress(ip);
        loginInfo.setLoginLocation(address);
        loginInfo.setBrowser(browser);
        loginInfo.setOs(os);
        loginInfo.setMsg(message);
        // 日志状态
        loginInfo.setStatus(status);
        String mail = null;
        boolean sendMail = false;
        // 通过账号密码登录成功校验是否符合发送邮件
        if (message.equals(LOGIN_SUCCESS) && type.equals(LOGIN_BY_ACCOUNT))
        {
            mail = mailCommonService.getMailByAccount(userName);
            sendMail = verifyLoginIp(userName, ip, mail, true);
        }
        log.info("记录访问日志");
        // 插入数据
        String loginTime = insertLoginInfo(loginInfo);
        // 登录成功发送告警邮件（非常用登录地址）
        if (sendMail)
        {
            mailCommonService.sendLoginMail(mail, userName, loginTime, ip, address);
        }
    }

    /**
     * 校验是否常用登录IP
     *
     * @param userName  账号
     * @param ip           ip
     * @param subscription 是否订阅
     */
    public boolean verifyLoginIp(String userName, String ip, String email, boolean subscription)
    {
        if (!StringUtils.isBlank(email) && subscription)
        {
            return !loginInfoMapper.exists(new LambdaQueryWrapper<LoginInfo>()
                    .eq(LoginInfo::getIpAddress, ip)
                    .eq(LoginInfo::getUserName, userName)
                    .eq(LoginInfo::getStatus, SUCCESS));
        }
        return false;
    }

}




