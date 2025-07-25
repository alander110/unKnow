package com.project.user.model.dto.req;

import lombok.Data;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 用户查询请求参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SelectUserReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户登录账号（模糊查询）
     */
    private String username;

    /**
     * 用户手机号码（模糊查询）
     */
    private String phone;

    /**
     * 用户电子邮箱（模糊查询）
     */
    private String email;

    /**
     * 账户状态：0-禁用 1-启用
     */
    private Integer status;

    /**
     * 创建时间开始范围
     */
    private String createTimeStart;

    /**
     * 创建时间结束范围
     */
    private String createTimeEnd;
}

