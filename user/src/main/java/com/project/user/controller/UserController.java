package com.project.user.controller;

import com.project.common.Result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/")
public class UserController {

    @GetMapping("info")
    public Result<String> getUserInfo(){
        return null;
    }

    @PutMapping("info")
    public Result<String> updateUserInfo(){
        return null;
    }

    @PutMapping("changePassword")
    public Result<String> changePassword(){
        return null;
    }

    @PutMapping("disableUser")
    public Result<String> disableUser(){
        return null;
    }

    @GetMapping("list")
    public Result<String> getUserList(){
        return null;
    }


}

