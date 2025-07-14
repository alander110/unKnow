package com.project.user.model.dto.req;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterReq {
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Size(min = 6, max = 20, message = "密码长度必须在6~20之间")
    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^\\d{11}$", message = "手机号码格式错误")
    private String phone;
}
