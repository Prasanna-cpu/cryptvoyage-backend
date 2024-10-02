package com.kumar.backend.Request;


import lombok.Data;

@Data
public class PaymentDetailsRequest {
    private String accountHolderName;
    private String accountNumber;
    private String ifscCode;
    private String bankName;
}
