package com.kumar.backend.Controller;


import com.kumar.backend.Exception.NonExistentPaymentOrderException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Exception.NonExistentWalletException;
import com.kumar.backend.Model.*;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Response.PaymentResponse;
import com.kumar.backend.Service.Abstraction.PaymentService;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Service.Abstraction.WalletTransactionService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final WalletTransactionService walletTransactionService;

    @GetMapping("/get-wallet")
    public ResponseEntity<ApiResponse> getUserWallet(@RequestHeader("Authorization") String jwt){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Wallet wallet=walletService.getUserWallet(user);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(wallet,HttpStatus.OK.value(),"Wallet retrieved successfully"));
        }
        catch(NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @GetMapping("/transfer/wallet/{walletId}")
    public ResponseEntity<ApiResponse> walletToWalletTransfer(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long walletId,
            @RequestBody WalletTransaction request
    ){
        try{
            User sendUser=userService.findUserProfileByJwt(jwt);
            Wallet receiverWallet=walletService.findWalletById(walletId);

            Wallet  wallet=walletService.wallletToWalletTransfer(sendUser, receiverWallet, request.getAmount());

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(wallet,HttpStatus.OK.value(),"Money Transferred Successfully"));


        }
        catch(NonExistentWalletException | NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @PutMapping("/order-payment/{orderId}")
    public ResponseEntity<ApiResponse> payOrderPayment(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long orderId
    )
    {

        try{
            User user=userService.findUserProfileByJwt(jwt);
            return null;

        }
        catch(NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @PutMapping("/deposit/amount/{amount}")
    public ResponseEntity<PaymentResponse> deposit(@RequestHeader("Authorization") String jwt,@PathVariable Long amount) throws NonExistentUserException {
        User user=userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);
        PaymentResponse res = new PaymentResponse();
        res.setPaymentUrl(res.getPaymentUrl());
        walletService.addBalanceToWallet(wallet, amount);

        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @PutMapping("/add-money")
    public ResponseEntity<ApiResponse> addMoneyToWallet(
            @RequestHeader("Authorization") String jwt,
            @RequestParam(name="order_id") Long orderId,
            @RequestParam(name="payment_id")String paymentId
    ){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Wallet wallet=walletService.findWalletById(user.getId());

            PaymentOrder order = paymentService.getPaymentOrderById(orderId);
            Boolean status=paymentService.ProceedPaymentOrder(order,paymentId);
            PaymentResponse res = new PaymentResponse();
            res.setPaymentUrl(res.getPaymentUrl());

            if(status){
                wallet=walletService.addBalanceToWallet(wallet, order.getAmount());
            }

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(wallet,HttpStatus.ACCEPTED.value(),"Money deposited successfully"));

        }
        catch(NonExistentUserException | NonExistentWalletException | NonExistentPaymentOrderException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(RazorpayException e){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new ApiResponse(null,HttpStatus.BAD_GATEWAY.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }

    }

    @GetMapping("/all-transactions")
    public ResponseEntity<ApiResponse> getWalletTransactions(@RequestHeader("Authorization") String jwt){

        try{
            User user=userService.findUserProfileByJwt(jwt);

            Wallet wallet = walletService.getUserWallet(user);

            List<WalletTransaction> transactions=walletTransactionService.getTransactions(wallet,null);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ApiResponse(
                                    transactions,
                                    HttpStatus.OK.value(),
                                    "Wallet Transactions fetched"
                            )
                    );

        }
        catch (NonExistentUserException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }



}

