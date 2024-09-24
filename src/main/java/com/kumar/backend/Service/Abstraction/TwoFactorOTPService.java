package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentEmailException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.TwoFactorOTP;
import com.kumar.backend.Model.User;

public interface TwoFactorOTPService {

    TwoFactorOTP createTwoFactorOTP(User user,String otp,String jwt);

    TwoFactorOTP findByUser(Long userId);

    TwoFactorOTP findById(String id) throws NonExistentEmailException;

    boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorOTP,String otp);

    void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP);


}
