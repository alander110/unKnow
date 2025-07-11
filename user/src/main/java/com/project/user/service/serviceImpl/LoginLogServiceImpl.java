package com.project.user.service.serviceImpl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.user.mapper.LoginLogMapper;
import com.project.user.model.entity.LoginLog;
import com.project.user.service.LoginLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    @Override
    public void record(Long userId,int i) {
        LoginLog.builder()
                .userId(userId)
                .loginIp("127.0.0.1")
                .deviceInfo("Windows 10")
                .result(i)
                .build();
    }
}
