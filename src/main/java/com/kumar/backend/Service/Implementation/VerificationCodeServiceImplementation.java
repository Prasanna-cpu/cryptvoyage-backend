package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentVerificationCodeException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.VerificationCode;
import com.kumar.backend.Repository.VerificationCodeRepository;
import com.kumar.backend.Service.Abstraction.VerificationCodeService;
import com.kumar.backend.Utils.Enums.VerificationType;
import com.kumar.backend.Utils.OTP.OTPUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class VerificationCodeServiceImplementation implements VerificationCodeService {


    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public VerificationCode sendVerificationCode(VerificationType verificationType, User user) {
        VerificationCode verificationCode1=new VerificationCode();

        verificationCode1.setOtp(OTPUtils.generateOTP());
        verificationCode1.setVerificationType(verificationType);
        verificationCode1.setUser(user);
        return verificationCodeRepository.save(verificationCode1);
    }

    @Override
    public Boolean verifyOtp(String otp, VerificationCode verificationCode) {
        return otp.equals(verificationCode.getOtp());
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws NonExistentVerificationCodeException {

        VerificationCode verificationCode=verificationCodeRepository
                .findById(id)
                .orElseThrow(()->new NonExistentVerificationCodeException("Verification code not found with id : "+id));

        return verificationCode;
    }

    @Override
    public VerificationCode getVerificationCodeByUserId(Long userId) throws NonExistentVerificationCodeException {

        VerificationCode verificationCode=verificationCodeRepository
                .findByUserId(userId)
                .orElseThrow(()->new NonExistentVerificationCodeException("Verification code not found with userId : "+userId));

        return verificationCode;
    }

    @Override
    public void deleteVerificationCode(VerificationCode verificationCode) {

        verificationCodeRepository.delete(verificationCode);
    }
}
