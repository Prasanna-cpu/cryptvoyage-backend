package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.PaymentDetails;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.PaymentDetailsRepository;
import com.kumar.backend.Repository.UserRepository;
import com.kumar.backend.Service.Abstraction.PaymentDetailsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class PaymentDetailsServiceImplementation implements PaymentDetailsService {

    private final PaymentDetailsRepository paymentDetailsRepository;
    private final UserRepository userRepository;

    @Override
    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifscCode, String bankName, User user) {

        PaymentDetails paymentDetails=new PaymentDetails(accountNumber,accountHolderName,ifscCode,bankName,user);
        PaymentDetails savedPaymentDetails=paymentDetailsRepository.save(paymentDetails);

        return savedPaymentDetails;
    }

    @Override
    public PaymentDetails getUserPaymentDetails(User user) throws NonExistentUserException {

        User userTarget=userRepository.findById(user.getId()).orElseThrow(()->new NonExistentUserException("User not found"));
        PaymentDetails paymentDetails=paymentDetailsRepository.findByUserId(userTarget.getId()).orElseThrow(()->new NonExistentUserException("Payment details not found"));

        return paymentDetails;
    }
}
