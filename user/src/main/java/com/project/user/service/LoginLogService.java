package com.project.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.project.user.model.entity.LoginLog;

public interface LoginLogService extends IService<LoginLog> {
    void record(Long userId,int i);
}
