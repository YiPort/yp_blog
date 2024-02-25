package com.yiport.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yiport.domain.entity.LoginUser;
import com.yiport.domain.entity.User;
import com.yiport.mapper.UserMapper;
import com.yiport.service.LoginCommonService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.yiport.constent.UserConstant.EMAIL_REGEX;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService
{
    private final LoginCommonService loginCommonService;
    private final HttpServletRequest request;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        User user;
        Matcher matcher = Pattern.compile(EMAIL_REGEX).matcher(username);
        if (matcher.matches())
        {
            user = loginCommonService.loadUserByEmail(username, request);
        }
        else
        {
            user = loginCommonService.loadUserByUsername(username, request);
        }
        // 获取权限集合
        Set<String> permissions = new HashSet<>();
        permissions.add(user.getUserRole().toString());
        // 将用户信息封装成 UserDetails返回
        return new LoginUser(user, permissions);
    }
}