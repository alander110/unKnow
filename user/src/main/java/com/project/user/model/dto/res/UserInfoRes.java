package com.project.user.model.dto.res;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.user.util.SensitiveDataSerializer;
import lombok.Data;

import java.util.Date;

@Data
public class UserInfoRes {
    /**
     * 用户登录账号，全局唯一
     */
    private String username;

    /**
     * 用户手机号码，全局唯一
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    private String phone;

    /**
     * 用户电子邮箱，全局唯一
     */
    @JsonSerialize(using = SensitiveDataSerializer.class)
    private String email;

    /**
     * 最后一次登录的IP地址
     */
    private String loginIp;

    /**
     * 登录设备信息
     */
    private String deviceInfo;

    /**
     * 账户最后更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date loginTime;
}
