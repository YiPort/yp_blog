package com.yiport.aspect;


import com.yiport.annotation.LimitRequest;
import com.yiport.domain.ResponseResult;
import com.yiport.exception.SystemException;
import com.yiport.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import static com.yiport.constants.BusinessConstants.ADMIN_ID;
import static com.yiport.enums.AppHttpCodeEnum.LIMIT_ERROR;
import static com.yiport.enums.AppHttpCodeEnum.NEED_LOGIN;
import static com.yiport.enums.AppHttpCodeEnum.NO_OPERATOR_AUTH;

/**
 * 接口请求限制切面类
 */
@Aspect
@Component
public class LimitRequestAspect {

    private static ConcurrentHashMap<String, ExpiringMap<String, Integer>> book = new ConcurrentHashMap<>();

    // 定义切点
    // 让所有有@LimitRequest注解的方法都执行切面方法
    @Pointcut("@annotation(limitRequest)")
    public void excudeService(LimitRequest limitRequest) {
    }

    @Around(value = "excudeService(limitRequest)", argNames = "pjp,limitRequest")
    public Object doAround(ProceedingJoinPoint pjp, LimitRequest limitRequest) throws Throwable {

        // 获得request对象
        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = Objects.requireNonNull(sra).getRequest();

        // 获取真实IP
        String key;

        if (limitRequest.type().equals("IP"))
        {
            // 获取真实IP
            key = request.getHeader("X-Forwarded-For") == null ? request.getRemoteHost() : request.getHeader("X-Forwarded-For");
        }
        else
        {
            // 获取用户ID
            String token = request.getHeader("token");
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
            if (!limitRequest.limitAdmin() && key.equals(String.valueOf(ADMIN_ID)))
            {
                return pjp.proceed();
            }

        }

        // 获取Map对象， 如果没有则返回默认值
        // 第一个参数是key， 第二个参数是默认值
        ExpiringMap<String, Integer> uc = book.getOrDefault(request.getRequestURI(), ExpiringMap.builder().variableExpiration().build());
        Integer uCount = uc.getOrDefault(key, 0);

        if (uCount >= limitRequest.count()) {
            // 超过次数，不执行目标方法
            if (limitRequest.tip())
            {
                return ResponseResult.errorResult(LIMIT_ERROR, limitRequest.description());
            }
            else
            {
                return ResponseResult.okResult();
            }
        } else if (uCount == 0) { // 第一次请求时，设置有效时间
            uc.put(key, uCount + 1, ExpirationPolicy.CREATED, limitRequest.time(), TimeUnit.MILLISECONDS);
        } else { // 未超过次数， 记录加一
            uc.put(key, uCount + 1);
        }
        book.put(request.getRequestURI(), uc);

        // 返回被拦截方法的返回值
        return pjp.proceed();
    }

}