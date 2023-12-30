package com.yiport.utils;

import com.yiport.exception.SystemException;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static com.yiport.constants.BusinessConstants.BLOG_ADMIN;
import static com.yiport.constants.BusinessConstants.TOKEN_KEY;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;


/**
 *
 */
public class LoginUtils
{

    /**
     * 权限检查
     */
    public static void checkRole(RedisCache redisCache, HttpServletRequest httpServletRequest)
    {
        // token校验
        String token = httpServletRequest.getHeader(TOKEN_KEY);
        if (StringUtils.isAnyBlank(token))
        {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        Claims claims;
        try
        {
            claims = JwtUtil.parseJWT(token);
        }
        catch (Exception e)
        {
            throw new SystemException(NEED_LOGIN, "未登录，请登录后重试");
        }
        Object admin = redisCache.getCacheObject(BLOG_ADMIN + claims.getId());
        if (Objects.isNull(admin))
        {
            throw new SystemException(NO_OPERATOR_AUTH);
        }
    }
}
