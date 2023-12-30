package com.yiport.handler;


import com.yiport.exception.SystemException;
import com.yiport.utils.JwtUtil;
import com.yiport.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.yiport.constants.BusinessConstants.BLOG_TOKEN;
import static com.yiport.constants.BusinessConstants.TOKEN_KEY;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;


/**
 * 登录拦截器
 */
@Slf4j
@RequiredArgsConstructor
public class UserLoginInterceptor implements HandlerInterceptor
{

    private final RedisCache redisCache;

    /***
     * 在请求处理之前进行调用(Controller方法调用之前)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
    {
        if (HttpMethod.OPTIONS.toString().equals(request.getMethod()))
        {
            log.debug("OPTIONS请求，放行");
            return true;
        }

        log.debug("执行了拦截器的preHandle方法");
        long userId = Long.parseLong(JwtUtil.checkToken(request));
        // id校验
        if (userId <= 0)
        {
            throw new SystemException(PARAMETER_ERROR);
        }
        // token校验
        String token = request.getHeader(TOKEN_KEY);
        if (StringUtils.isAnyBlank(token))
        {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        String tokenKey = BLOG_TOKEN + userId;
        Object cacheObject = redisCache.getCacheObject(tokenKey);
        if (cacheObject == null)
        {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        if (!token.equals(String.valueOf(cacheObject)))
        {
            throw new SystemException(NEED_LOGIN, "登录过期，请重新登录");
        }
        return true;
    }

}

