package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentTokenException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.ForgetPasswordToken;
import com.kumar.backend.Model.User;
import com.kumar.backend.Utils.Enums.VerificationType;

public interface ForgetPasswordService{
    ForgetPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);

    ForgetPasswordToken findById(String id) throws NonExistentTokenException;

    ForgetPasswordToken findByUserId(Long userId) throws NonExistentTokenException, NonExistentUserException;

    void deleteToken(ForgetPasswordToken forgetPasswordToken);



}
