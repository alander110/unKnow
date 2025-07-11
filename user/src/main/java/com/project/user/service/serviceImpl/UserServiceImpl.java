package com.project.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.user.mapper.UserMapper;
import com.project.user.model.entity.User;
import com.project.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{
    @Override
    public User findByUsername(String username) {
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        return user;
    }
}
