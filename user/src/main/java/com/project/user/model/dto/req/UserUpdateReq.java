package com.project.user.model.dto.req;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class UserUpdateReq implements Serializable {

    /**
     * 用户ID
     */
    private Long UserId;

    /**
     * 用户登录账号，全局唯一
     */
    private String username;

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
     * 账户最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
