package com.project.user.model.dto.req;

import lombok.Data;

@Data
public class RegisterReq {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String phone;
}
