package com.project.user.exception;

import com.project.common.Result.Result;
import com.project.common.Result.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 权限拦截
    /**
     * 处理通用异常
     * @param e 异常对象
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Result<String> handleGenericException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(StatusCode.SYSTEM_ERROR, "系统内部错误: " + e.getMessage(),null);
    }


    /**
     * 处理运行时异常
     * @param e 运行时异常
     * @return 统一响应结果
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Result<String> handleRuntimeException(RuntimeException e) {
        return Result.fail(StatusCode.BUSINESS_ERROR, "系统内部错误: " + e.getMessage(),null);
    }
}

