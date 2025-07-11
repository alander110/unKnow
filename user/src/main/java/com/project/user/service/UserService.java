package com.project.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.user.model.entity.User;

public interface UserService extends IService<User> {
    User findByUsername(String username);
}
