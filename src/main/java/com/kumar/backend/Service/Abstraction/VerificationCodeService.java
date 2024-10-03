package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentVerificationCodeException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.VerificationCode;
import com.kumar.backend.Utils.Enums.VerificationType;

public interface VerificationCodeService {

    VerificationCode sendVerificationCode(VerificationType verificationType, User user);

    Boolean verifyOtp(String otp, VerificationCode verificationCode);

    VerificationCode getVerificationCodeById(Long id) throws NonExistentVerificationCodeException;

    VerificationCode getVerificationCodeByUserId(Long userId) throws NonExistentVerificationCodeException;

    void deleteVerificationCode(VerificationCode verificationCode);

//    boolean verifyTwoFactorOTP(VerificationCode verificationCode, String otp);

}


