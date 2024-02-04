package com.yiport.aspect;


import cn.hutool.core.date.DateUtil;
import com.yiport.annotation.LimitRequest;
import com.yiport.domain.ResponseResult;
import com.yiport.exception.SystemException;
import com.yiport.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


import static cn.hutool.core.date.BetweenFormatter.Level.SECOND;
import static com.yiport.constants.BusinessConstants.TOKEN_KEY;
import static com.yiport.constants.SystemConstants.ADMIN_ID_1;
import static com.yiport.constants.SystemConstants.ADMIN_ID_2;
import static com.yiport.constants.SystemConstants.FALSE;
import static com.yiport.constants.SystemConstants.TRUE;
import static com.yiport.constants.SystemConstants.X_FORWARDED_FOR;
import static com.yiport.enums.AppHttpCodeEnum.LIMIT_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;

/**
 * 接口请求限制切面类
 *
 * @author wzy
 * @version 2022/5/26 10:05
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LimitRequestAspect
{
    private static ConcurrentHashMap<String, ExpiringMap<String, Integer>> book = new ConcurrentHashMap<>();
    private final HttpServletRequest request;

    // 定义切点
    @Pointcut("@annotation(limitRequest)")
    public void includeService(LimitRequest limitRequest)
    {
    }

    /**
     * 拦截被限流请求
     */
    @Around(value = "includeService(limitRequest)", argNames = "pjp,limitRequest")
    public Object doAround(ProceedingJoinPoint pjp, LimitRequest limitRequest) throws Throwable
    {
        String key = getKey(limitRequest);
        // 获取Map对象， 如果没有则返回默认值
        ExpiringMap<String, Integer> uc = book.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
        Integer uCount = uc.getOrDefault(key, 0);

        if (!limitRequest.limitAdmin() && StringUtils.equalsAny(key, ADMIN_ID_1.toString(), ADMIN_ID_2.toString()))
        {
            // 对管理员放行
            return pjp.proceed();
        }
        if (uCount >= limitRequest.count())
        {
            // 超过次数，不执行目标方法
            if (limitRequest.tip().equals(FALSE))
            {
                return ResponseResult.errorResult(LIMIT_ERROR, limitRequest.description());
            }
            else if (limitRequest.tip().equals(TRUE))
            {
                return ResponseResult.errorResult(LIMIT_ERROR, DateUtil.formatBetween(uc.getExpectedExpiration(key), SECOND) + " 后再试");
            }
            else
            {
                return ResponseResult.okResult();
            }
        }
        return pjp.proceed();
    }

    /**
     * 计数器（只记录成功的请求）
     */
    @AfterReturning(value = "includeService(limitRequest)", returning = "result", argNames = "jp,limitRequest,result")
    public void doAfterReturning(JoinPoint jp, LimitRequest limitRequest, ResponseResult result)
    {
        String key = getKey(limitRequest);
        // 获取Map对象， 如果没有则返回默认值
        ExpiringMap<String, Integer> uc = book.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
        Integer uCount = uc.getOrDefault(key, 0);

        if (limitRequest.limitAdmin() || !StringUtils.equalsAny(key, ADMIN_ID_1.toString(), ADMIN_ID_2.toString()))
        {
            if (uCount == 0)
            { // 第一次请求时，设置有效时间
                uc.put(key, uCount + 1, ExpirationPolicy.CREATED, limitRequest.time(), TimeUnit.MILLISECONDS);
            }
            else
            { // 未超过次数， 记录加一
                uc.put(key, uCount + 1);
            }
            book.put(request.getRequestURI(), uc);
        }
    }

    public String getKey(LimitRequest limitRequest)
    {
        String key;

        if (limitRequest.type().equals("IP"))
        {
            // 获取真实IP
            key = request.getHeader(X_FORWARDED_FOR) == null ? request.getRemoteHost() : request.getHeader(X_FORWARDED_FOR);
        }
        else
        {
            // 获取用户ID
            String token = request.getHeader(TOKEN_KEY);
            if (StringUtils.isBlank(token))
            {
                throw new SystemException(NO_OPERATOR_AUTH);
            }
            Claims claims;
            try
            {
                claims = JwtUtil.parseJWT(token);
            }
            catch (Exception e)
            {
                throw new SystemException(NEED_LOGIN);
            }
            key = claims.getId();
        }
        return key;
    }

}