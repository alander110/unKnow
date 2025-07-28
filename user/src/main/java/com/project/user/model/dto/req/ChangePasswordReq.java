package com.project.user.model.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordReq {
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @Size(min = 6, max = 20, message = "密码长度必须在6~20之间")
    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
