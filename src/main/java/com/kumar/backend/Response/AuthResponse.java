package com.kumar.backend.Response;

import com.kumar.backend.Model.User;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString
public class AuthResponse extends ApiResponse{

    private String token;

    private boolean isTwoFactorEnabled=false;

    private String session;

    public AuthResponse(Object data, int status, String message, String jwt) {
        super(data,status,message);
        this.token = jwt;
    }
}
