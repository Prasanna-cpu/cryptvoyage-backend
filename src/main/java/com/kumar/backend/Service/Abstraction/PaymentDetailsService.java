package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.PaymentDetails;
import com.kumar.backend.Model.User;

public interface PaymentDetailsService {

    PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifscCode, String bankName, User user);

    PaymentDetails getUserPaymentDetails(User user) throws NonExistentUserException;


}
