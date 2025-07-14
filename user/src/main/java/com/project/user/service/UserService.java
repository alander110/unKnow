package com.project.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.common.Result.Result;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.model.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);

    Result<String> login(LoginReq loginReq) throws JsonProcessingException;

    Result<String> register(RegisterReq registerReq);

    Result<String> refreshToken(String refreshToken) throws Exception;

    Result<String> logout(String token) throws Exception;
}
