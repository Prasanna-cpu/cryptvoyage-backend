package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentEmailException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.TwoFactorOTP;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.TwoFactorOTPRepository;
import com.kumar.backend.Service.Abstraction.TwoFactorOTPService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class TwoFactorOTPServiceImplementation implements TwoFactorOTPService {

    private final TwoFactorOTPRepository twoFactorOTPRepository;

    @Override
    public TwoFactorOTP createTwoFactorOTP(User user, String otp, String jwt) {
        UUID uuid=UUID.randomUUID();

        String id=uuid.toString();

        TwoFactorOTP twoFactorOTP=new TwoFactorOTP();
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);

        twoFactorOTPRepository.save(twoFactorOTP);

        return twoFactorOTP;

    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        TwoFactorOTP twoFactorOTP=twoFactorOTPRepository.findByUserId(userId);

        return twoFactorOTP;
    }

    @Override
    public TwoFactorOTP findById(String id) throws NonExistentEmailException {

        TwoFactorOTP otp=twoFactorOTPRepository.findById(id).orElseThrow(()->new NonExistentEmailException("User not found with id : "+id));

        return otp;

    }

    @Override
    public boolean verifyTwoFactorOTP(TwoFactorOTP twoFactorOTP, String otp) {
        boolean isValid=twoFactorOTP.getOtp().equals(otp);
        return isValid;
    }

    @Override
    public void deleteTwoFactorOTP(TwoFactorOTP twoFactorOTP) {

        twoFactorOTPRepository.delete(twoFactorOTP);
    }
}
