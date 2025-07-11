package com.project.user.exception;


import cn.dev33.satoken.exception.NotPermissionException;
import com.project.common.Result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 权限拦截
    @ExceptionHandler(NotPermissionException.class)
    public Result<String> handlerException(Exception e) {
        log.warn(String.valueOf(e));
        return Result.fail("无此权限");
    }
}

