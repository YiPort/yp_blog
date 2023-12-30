package com.yiport.filter;

import com.yiport.domain.ResponseResult;
import com.yiport.domain.entity.LoginUser;
import com.yiport.exception.SystemException;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
import com.yiport.utils.WebUtils;
import com.alibaba.fastjson.JSON;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static com.yiport.constants.BusinessConstants.BLOG_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //获取请求头中的token
        String token = request.getHeader("Token");
        if(!StringUtils.hasText(token)){
            //说明该接口不需要登录  直接放行
            filterChain.doFilter(request, response);
            return;
        }
        // 解析 Token
        Claims claims;
        String userId;
        try
        {
            claims = JwtUtil.parseJWT(token);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Token非法！");
        }
        long now = System.currentTimeMillis();
        long startTime = claims.getIssuedAt().getTime();
        if (now - startTime > 5 * 24 * 60 * 60 * 1000L)
        {
            throw new SystemException(NEED_LOGIN, "认证过期，请重新登录");
        }

        userId = claims.getId();

        //从redis中获取用户信息
        LoginUser loginUser = redisCache.getCacheObject(BLOG_LOGIN + userId);
        //如果获取不到
        if (Objects.isNull(loginUser)) {
            //说明登录过期  提示重新登录
            ResponseResult result = ResponseResult.errorResult(NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        }
        //将用户信息(loginUser)和对应的权限信息(permissions)存入 SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 放行
        filterChain.doFilter(request, response);
    }


}