package com.project.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @description 用户登录历史记录审计表
 * @author BEJSON.com
 * @date 2025-07-11
 */
@Data
@Builder
@TableName("sys_loginLog")
public class LoginLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    /**
     * 日志记录ID，主键自增长
     */
    private Long logId;

    /**
     * 用户ID，关联sys_user.user_id
     */
    private Long userId;

    /**
     * 登录请求的IP地址
     */
    private String loginIp;

    /**
     * 登录设备信息（UA字符串）
     */
    private String deviceInfo;

    /**
     * 登录发生时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime loginTime;

    /**
     * 登录结果：0-失败 1-成功
     */
    private Integer result;
}
