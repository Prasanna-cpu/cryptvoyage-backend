package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentPaymentOrderException;
import com.kumar.backend.Model.PaymentOrder;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.PaymentRepository;
import com.kumar.backend.Response.PaymentResponse;
import com.kumar.backend.Service.Abstraction.PaymentService;
import com.kumar.backend.Utils.Enums.PaymentMethod;
import com.kumar.backend.Utils.Enums.PaymentOrderStatus;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import com.razorpay.PaymentLink;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(rollbackOn = Exception.class)
public class PaymentServiceImplementation implements PaymentService {

    private final PaymentRepository paymentRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String apiKey;

    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder=new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setAmount(amount);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);

        PaymentOrder newOrder=paymentRepository.save(paymentOrder);
        return newOrder;
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws NonExistentPaymentOrderException {
        PaymentOrder paymentOrder=paymentRepository.findById(id).orElseThrow(
                ()-> new NonExistentPaymentOrderException("Payment order with id "+id+" does not exist")
        );
        return paymentOrder;
    }

    @Override
    public Boolean ProceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {

        if(paymentOrder.getStatus()==null){
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }

        if(paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)){

            if(paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)){
                RazorpayClient razorpay=new RazorpayClient(apiKey,apiSecretKey);
                Payment payment=razorpay.payments.fetch(paymentId);

                Integer amount=payment.get("amount");

                String status=payment.get("status");

                if(status.equals("captured")) {
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    return true;
                }

                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentRepository.save(paymentOrder);
                return false;

            }
            paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
            paymentRepository.save(paymentOrder);
            return true;

        }


        return false;
    }

    @Override
    public PaymentResponse createRazorPaymentLink(User user, Long amount,Long orderId) throws RazorpayException {

        Long Amount=amount*100;

        try{

            RazorpayClient razorpay=new RazorpayClient(apiKey,apiSecretKey);
            JSONObject paymentLinkRequest = getJsonObject(user, amount, orderId);

            // Create the payment link using the paymentLink.create() method
            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            PaymentResponse res=new PaymentResponse();
            res.setPaymentUrl(paymentLinkUrl);


            return res;

        }
        catch(RazorpayException e){
            System.out.println("Error creating payment link: " + e.getMessage());
            throw new RazorpayException(e.getMessage());
        }

    }

    private static @NotNull JSONObject getJsonObject(User user, Long amount, Long orderId) {
        JSONObject paymentLinkRequest=new JSONObject();

        paymentLinkRequest.put("amount", amount);
        paymentLinkRequest.put("currency","INR");


        // Create a JSON object with the customer details
        JSONObject customer = new JSONObject();
        customer.put("name", user.getFullname());

        customer.put("email", user.getEmail());
        paymentLinkRequest.put("customer",customer);

        // Create a JSON object with the notification settings
        JSONObject notify = new JSONObject();
        notify.put("email",true);
        paymentLinkRequest.put("notify",notify);

        // Set the reminder settings
        paymentLinkRequest.put("reminder_enable",true);

        // Set the callback URL and method
//        paymentLinkRequest.put("callback_url","http://localhost:3000/wallet/"+orderId);
        paymentLinkRequest.put("callback_url","http://localhost:3000/wallet?order_id"+ orderId);
        paymentLinkRequest.put("callback_method","get");



        return paymentLinkRequest;
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount,Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:3000/wallet?order_id="+orderId)
                .setCancelUrl("http://localhost:3000/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount*100)
                                .setProductData(SessionCreateParams
                                        .LineItem
                                        .PriceData
                                        .ProductData
                                        .builder()
                                        .setName("Top up wallet")
                                        .build()
                                ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);

        System.out.println("session _____ " + session);

        PaymentResponse res = new PaymentResponse();

        res.setPaymentUrl(session.getUrl());

        return res;
    }
}
