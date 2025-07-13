package com.project.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.common.Result.Result;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.model.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);

    Result<String> login(LoginReq loginReq);

    Result<String> register(RegisterReq registerReq);
}
