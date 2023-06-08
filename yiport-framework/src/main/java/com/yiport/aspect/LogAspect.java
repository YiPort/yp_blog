package com.yiport.aspect;


import com.yiport.annotation.SystemLog;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(com.yiport.annotation.SystemLog)")
    public void pt() {
    }

    @Around("pt()")
    public Object priaLog(ProceedingJoinPoint joinPoint) {

        Object ret = null;

        try {
            handBefore(joinPoint);
            ret = joinPoint.proceed();
            handAfter(ret);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            log.info("=======End=======" + System.lineSeparator());
        }

        return ret;
    }

    private void handAfter(Object ret) {
        // 打印出参
        log.info("Response       : {}", JSON.toJSONString(ret));


    }

    private void handBefore(ProceedingJoinPoint joinPoint) {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = null;
        if (requestAttributes != null) {
            request = requestAttributes.getRequest();
        }

        // 获取被增强方法上的注解对象
        SystemLog systemLog = getSystemLog(joinPoint);


        log.info("=======Start=======");
        // 打印请求 URL
        log.info("URL            : {}", request.getRequestURI());
        // 打印描述信息
        log.info("BusinessName   : {}", systemLog.businessName());
        // 打印 Http method
        log.info("HTTP Method    : {}",request.getMethod() );
        // 打印调用 controller 的全路径以及执行方法
        log.info("Class Method   : {}.{}", joinPoint.getSignature().getDeclaringTypeName(),((MethodSignature)joinPoint.getSignature()).getName());
        // 打印请求的 IP
        log.info("IP             : {}",request.getRemoteHost());
        // 打印请求入参
        log.info("Request Args   : {}", JSON.toJSONString(joinPoint.getArgs()));


    }

    private SystemLog getSystemLog(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature= (MethodSignature) joinPoint.getSignature();
        SystemLog systemLog = methodSignature.getMethod().getAnnotation(SystemLog.class);
        return systemLog;

    }


}
