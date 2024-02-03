package com.yiport.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import com.yiport.annotation.SystemLog;
import com.yiport.utils.AddressUtils;
import com.yiport.utils.RedisCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    @Autowired
    private HttpServletRequest request;

    private final TransmittableThreadLocal<StopWatch> invokeTimeTL = new TransmittableThreadLocal<>();

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
            StopWatch stopWatch = invokeTimeTL.get();
            stopWatch.stop();
            log.info("结束请求 => {}/{} URL:[{}] 耗时:[{}毫秒]",
                    request.getMethod(),
                    getSystemLog(joinPoint).businessName(),
                    request.getRequestURI(),
                    stopWatch.getTime());
            invokeTimeTL.remove();
        }
        return ret;
    }


    private void handlerAfter(Object ret)
    {
        // 打印出参
        log.info("响应体 => {}", JSON.toJSONString(ret));
    }

    /**
     * 打印前置日志
     */
    private void handlerBefore(ProceedingJoinPoint joinPoint)
    {
        // 获取真实ip对应的地址存入RedisHash
        String ip = request.getHeader(X_FORWARDED_FOR) == null ? request.getRemoteHost() : request.getHeader(X_FORWARDED_FOR);
        String realAddressByIP = addressUtils.getRealAddressByIP(ip);

        if (!realAddressByIP.equals(INTRANET_IP) && !realAddressByIP.equals(UNKNOWN))
        {
            redisCache.setCacheMapValue(IP, ip, realAddressByIP);
        }

        // 获取被增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);

        log.info("开始请求 => {}/{} URL:[{}] 参数:{} IP:[{}-{}]",
                request.getMethod(),
                systemLog.businessName(),
                request.getRequestURI(),
                JSON.toJSONString(joinPoint.getArgs()),
                ip, realAddressByIP);

        StopWatch stopWatch = new StopWatch();
        invokeTimeTL.set(stopWatch);
        stopWatch.start();
    }

    /**
     * 获取被增强方法上的注解对象
     */
    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint)
    {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return methodSignature.getMethod().getAnnotation(SystemLog.class);
    }

}
