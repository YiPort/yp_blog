package com.yiport.exception;

import com.yiport.domain.ResponseResult;
import com.yiport.enums.AppHttpCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static com.yiport.enums.AppHttpCodeEnum.PARAMETER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常
     */
    @ExceptionHandler(SystemException.class)
    public ResponseResult<Void> systemExceptionHandler(SystemException e)
    {
        //打印异常信息
        log.error("出现了异常:" + (e.getDescription() != null ? e.getDescription() : e.getMsg()), e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(e.getCode(),e.getMsg(),e.getDescription());
    }


    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public ResponseResult<Void> exceptionHandler(Exception e)
    {
        //打印异常信息
        log.error("出现了异常:",e);
        //从异常对象中获取提示信息封装返回
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR.getCode(), "系统异常");
    }

    /**
     * Mybatis系统异常 通用处理
     */
    @ExceptionHandler(MyBatisSystemException.class)
    public ResponseResult<Void> handleCannotFindDataSourceException(MyBatisSystemException e, HttpServletRequest request)
    {
        String requestURI = request.getRequestURI();
        String message = e.getMessage();
        if (message.contains("CannotFindDataSourceException"))
        {
            log.error("请求地址'{}', 未找到数据源", requestURI);
            return ResponseResult.errorResult(PARAMETER_ERROR, "未找到数据源，请联系管理员确认");
        }
        log.error("请求地址'{}', Mybatis系统异常", requestURI, e);
        return ResponseResult.errorResult(PARAMETER_ERROR, "Mybatis系统异常");
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(BindException.class)
    public ResponseResult<Void> handleBindException(BindException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ResponseResult.errorResult(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseResult<Void> constraintViolationException(ConstraintViolationException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseResult.errorResult(message);
    }

    /**
     * 自定义验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseResult<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e)
    {
        log.error(e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return ResponseResult.errorResult(message);
    }
}