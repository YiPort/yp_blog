package com.yiport.aspect;

import com.alibaba.fastjson.JSON;
import com.yiport.annotation.SystemLog;
import com.yiport.utils.AddressUtils;
import com.yiport.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.yiport.constants.SystemConstants.*;


/**
 * 日志切面类
 */
@Component
@Aspect
@Slf4j
public class LogAspect
{

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private AddressUtils addressUtils;

    @Pointcut("@annotation(com.yiport.annotation.SystemLog)")
    public void pt()
    {

    }

    @Around("pt()")
    public Object printLog(ProceedingJoinPoint joinPoint) throws Throwable
    {

        Object ret;
        try
        {
            handlerBefore(joinPoint);
            ret = joinPoint.proceed();
            handlerAfter(ret);
        }
        finally
        {
            log.info("=======End=======" + System.lineSeparator());
        }
        return ret;
    }


    private void handlerAfter(Object ret)
    {
        // 打印出参
        log.info("Response       : {}", JSON.toJSONString(ret));
    }

    /**
     * 打印前置日志
     *
     * @param joinPoint
     */
    private void handlerBefore(ProceedingJoinPoint joinPoint)
    {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null)
        {
            request = requestAttributes.getRequest();
        }
        // 获取真实ip对应的地址存入RedisHash
        String ip = request.getHeader("X-Forwarded-For") == null ? request.getRemoteHost() : request.getHeader("X-Forwarded-For");
        String realAddressByIP = addressUtils.getRealAddressByIP(ip);

        if (!realAddressByIP.equals(INTRANET_IP) && !realAddressByIP.equals(UNKNOWN))
        {
            redisCache.setCacheMapValue(IP, ip, realAddressByIP);
        }

        // 获取被增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);

        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURL());
        // 打印描述信息
        log.info("BusinessName   : {}", systemLog.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}", request.getMethod());
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
        // 打印请求的 IP
        log.info("IP             : {}-{}", ip, realAddressByIP);
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));
    }

    /**
     * 获取被增强方法上的注解对象
     *
     * @param joinPoint
     * @return
     */
    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint)
    {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = methodSignature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;
    }

}
