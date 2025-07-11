package com.project.user.model.dto.req;

import lombok.Data;

@Data
public class LoginReq {
    private String username;
    private String password;
}
