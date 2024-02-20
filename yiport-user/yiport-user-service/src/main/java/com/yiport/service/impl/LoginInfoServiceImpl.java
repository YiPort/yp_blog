package com.yiport.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.extra.servlet.ServletUtil;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

import static com.yiport.constent.UserConstant.LOGIN_SUCCESS;
import static com.yiport.constent.UserConstant.SUCCESS;

/**
 * 登录信息 业务层
 */
@Service
@RequiredArgsConstructor
public class LoginInfoServiceImpl extends ServiceImpl<LoginInfoMapper, LoginInfo>
        implements LoginInfoService
{
    private final MailCommonService mailCommonService;
    private final LoginInfoMapper loginInfoMapper;
    private final AddressUtils addressUtils;
    private final UserMapper userMapper;

    /**
     * 登录信息记录
     */
    @Async
    @Override
    public void recordLoginInfo(final String userName, final String status, final String message,
                                HttpServletRequest request, final Object... args)
    {
        final UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
        final String ip = ServletUtil.getClientIP(request);
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
        LoginInfo norLoginInfo = null;
        if (message.equals(LOGIN_SUCCESS))
        {
            // 查询是否常用登录地址
            norLoginInfo = loginInfoMapper.selectOne(new LambdaQueryWrapper<LoginInfo>()
                    .eq(LoginInfo::getIpAddress, ip)
                    .eq(LoginInfo::getStatus, SUCCESS));
        }
        // 插入数据
        String loginTime = insertLoginInfo(loginInfo);
        // 登录成功发送告警邮件（非常用登录地址）
        // todo:发送登录告警邮件待完善
        if (message.equals(LOGIN_SUCCESS) && Objects.isNull(norLoginInfo))
        {
            User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                    .eq(User::getUserName, userName));
            mailCommonService.sendLoginMail(user.getEmail(), userName, loginTime, ip, address);
        }
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

}




