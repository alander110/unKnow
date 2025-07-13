package com.project.user.controller;

import com.project.common.Result.Result;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录
 */
@RestController
@RequestMapping("/user/")
public class LoginController {

    @Autowired
    private UserService userService;

    @PostMapping("login")
    public Result<String> Login(@RequestBody LoginReq loginReq) {
        return userService.login(loginReq);
    }

    @PostMapping("register")
    public Result<String> register(@RequestBody RegisterReq registerReq) {
        return userService.register(registerReq);
    }

}

