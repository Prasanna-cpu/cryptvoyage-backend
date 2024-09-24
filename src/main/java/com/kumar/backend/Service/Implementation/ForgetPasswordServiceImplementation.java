package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentTokenException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.ForgetPasswordToken;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.ForgetPasswordRepository;
import com.kumar.backend.Repository.UserRepository;
import com.kumar.backend.Service.Abstraction.ForgetPasswordService;
import com.kumar.backend.Utils.Enums.VerificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class ForgetPasswordServiceImplementation implements ForgetPasswordService {

    private final ForgetPasswordRepository forgetPasswordRepository;
    private final UserRepository userRepository;

    @Override
    public ForgetPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo) {
        ForgetPasswordToken forgetPasswordToken=new ForgetPasswordToken();
        forgetPasswordToken.setUser(user);
        forgetPasswordToken.setOtp(otp);
        forgetPasswordToken.setVerificationType(verificationType);
        forgetPasswordToken.setSendTo(sendTo);
        forgetPasswordToken.setId(id);
        ForgetPasswordToken newToken=forgetPasswordRepository.save(forgetPasswordToken);
        return newToken;
    }

    @Override
    public ForgetPasswordToken findById(String id) throws NonExistentTokenException {
        ForgetPasswordToken token=forgetPasswordRepository.findById(id).orElseThrow(()->new NonExistentTokenException("Token does not exist"));
        return token;
    }

    @Override
    public ForgetPasswordToken findByUserId(Long userId) throws NonExistentTokenException, NonExistentUserException {

        User user=userRepository.findById(userId).orElseThrow(()->new NonExistentUserException("User does not exist"));
        ForgetPasswordToken token=forgetPasswordRepository.findByUserId(userId).orElseThrow(()->new NonExistentTokenException("Token does not exist"));
        return token;
    }

    @Override
    public void deleteToken(ForgetPasswordToken forgetPasswordToken) {
        forgetPasswordRepository.delete(forgetPasswordToken);
    }
}
