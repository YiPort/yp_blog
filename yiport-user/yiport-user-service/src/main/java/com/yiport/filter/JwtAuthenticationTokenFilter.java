package com.yiport.filter;

import com.yiport.domain.entity.LoginUser;
import com.yiport.exception.SystemException;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
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

import static com.yiport.constants.BusinessConstants.BLOG_LOGIN;
import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constants.BusinessConstants.TOKEN_KEY;
import static com.yiport.constent.ExceptionDescription.NOT_LOGIN;
import static com.yiport.constent.ExceptionDescription.TOKEN_EXPIRE;
import static com.yiport.constent.ExceptionDescription.TOKEN_ILLEGAL;
import static com.yiport.constent.UserConstant.LIMIT_TIME;
import static com.yiport.constent.UserConstant.TOKEN_HEADER_KEY;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private RedisCache redisCache;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        //获取 Token
        String token = request.getHeader(TOKEN_HEADER_KEY);
        if (!StringUtils.hasText(token))
        {
            // 未找到 Token，放行
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
            throw new SystemException(NEED_LOGIN, TOKEN_ILLEGAL);
        }
        long now = System.currentTimeMillis();
        long startTime = claims.getIssuedAt().getTime();
        if (now - startTime > LIMIT_TIME)
        {
            throw new SystemException(NEED_LOGIN, TOKEN_EXPIRE);
        }

        userId = claims.getId();

        // 从 redis中获取用户信息
        String key = BLOG_LOGIN + userId;
        LoginUser loginUser = redisCache.getCacheObject(key);
        String tokenKey = BLOG_TOKEN + userId;
        String redisToken = redisCache.getCacheObject(tokenKey);
        if (loginUser == null || !redisToken.equals(token))
        {
            throw new SystemException(NEED_LOGIN, NOT_LOGIN);
        }
        // 将用户信息(loginUser)和对应的权限信息(permissions)存入 SecurityContextHolder
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 放行
        filterChain.doFilter(request, response);
    }


}