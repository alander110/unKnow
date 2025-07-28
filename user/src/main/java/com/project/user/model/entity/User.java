package com.project.user.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @description 用户表
 * @author alander
 * @date 2025-07-11
 */
@Data
@TableName("sys_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户唯一标识，主键自增长
     */
    @TableId(type = IdType.AUTO)
    private Long userId;

    /**
     * 用户登录账号，全局唯一
     */
    private String username;

    /**
     * 使用BCrypt算法加密后的密码
     */
    private String passwordHash;

    /**
     * 账户状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 用户手机号码，全局唯一
     */
    private String phone;

    /**
     * 用户电子邮箱，全局唯一
     */
    private String email;

    /**
     * 最后一次登录的IP地址
     */
    private String lastLoginIp;

    /**
     * 账户创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 账户最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
