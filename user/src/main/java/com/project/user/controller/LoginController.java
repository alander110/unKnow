package com.project.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.common.Result.Result;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录管理
 */
@RestController
@RequestMapping("/")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 登录
     * @param loginReq 登录参数
     * @return 登录结果
     */
    @PostMapping("login")
    public Result<String> Login(@RequestBody LoginReq loginReq) throws JsonProcessingException {
        return userService.login(loginReq);
    }

    /**
     *  注册
     * @param registerReq 注册请求参数
     * @return 注册结果
     */
    @PostMapping("register")
    public Result<String> register(@RequestBody RegisterReq registerReq) {
        return userService.register(registerReq);
    }

    /**
     * 刷新token
     * @param refreshToken 刷新的token
     * @return 新的token
     */
    @PostMapping("token/refresh")
    public Result<String> refreshToken(@RequestParam("refreshToken") String refreshToken) throws Exception {
        return userService.refreshToken(refreshToken);
    }

    /**
     * 登出
     * @param token 登出的token
     * @return 登出结果
     */
    @GetMapping("logout")
    public Result<String> logout(@RequestHeader("Authorization") String token) throws Exception {
        return userService.logout(token);
    }

}

