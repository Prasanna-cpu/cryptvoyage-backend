package com.kumar.backend.Request;

import com.kumar.backend.Utils.Enums.VerificationType;
import lombok.Data;

@Data
public class ForgetPasswordTokenRequest {
    private String sendTo;
    private VerificationType verificationType;
}
