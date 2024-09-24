package com.kumar.backend.Request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private Long id;
    private String email;
    private String password;
}
