package com.kumar.backend.Request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String fullname;
    private String email;
    private String password;
    private String mobile;
}
