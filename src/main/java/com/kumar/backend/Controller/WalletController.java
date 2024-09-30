package com.kumar.backend.Controller;


import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Exception.NonExistentWalletException;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Service.Implementation.UserServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/wallet")
public class WalletController {

    private final WalletService walletService;
    private final UserService userService;

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



}
