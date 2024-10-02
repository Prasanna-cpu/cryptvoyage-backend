package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentPaymentOrderException;
import com.kumar.backend.Model.PaymentOrder;
import com.kumar.backend.Model.User;
import com.kumar.backend.Response.PaymentResponse;
import com.kumar.backend.Utils.Enums.PaymentMethod;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);

    PaymentOrder getPaymentOrderById(Long id) throws NonExistentPaymentOrderException;

    Boolean ProceedPaymentOrder(PaymentOrder paymentOrder,String paymentId) throws RazorpayException;

    PaymentResponse createRazorPaymentLink(User user,Long amount,Long orderId) throws RazorpayException;

    PaymentResponse createStripePaymentLink(User user,Long amount,Long orderId) throws StripeException;
}
