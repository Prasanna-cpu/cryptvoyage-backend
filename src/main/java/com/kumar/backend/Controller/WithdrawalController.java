package com.kumar.backend.Controller;


import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Model.Withdrawal;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Service.Abstraction.WalletTransactionService;
import com.kumar.backend.Service.Abstraction.WithdrawalService;
import com.kumar.backend.Utils.Enums.WalletTransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final UserService userService;
    private final WalletService walletService;
    private final WalletTransactionService walletTransactionService;

    @PostMapping("/withdraw/{amount}")
    public ResponseEntity<ApiResponse> withdrawalRequest(@RequestHeader("Authorization") String jwt, @PathVariable Long amount){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Wallet userWallet=walletService.getUserWallet(user);

            Withdrawal withdrawal=withdrawalService.requestWithdrawal(amount,user);
            walletService.addBalanceToWallet(userWallet,-withdrawal.getAmount());

            WalletTransaction walletTransaction=walletTransactionService.createTransaction(
                    userWallet,
                    WalletTransactionType.WITHDRAWAL,
                    null,
                    "Bank Account withdrawal",
                    withdrawal.getAmount()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse(
                                    withdrawal,
                                    HttpStatus.CREATED.value(),
                                    "Request Withdrawal"
                            )
                    );

        }
        catch(NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ApiResponse(
                                    null,
                                    HttpStatus.NOT_FOUND.value(),
                                    e.getMessage()
                            )
                    );
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @PatchMapping("/withdrwal/{id}/proceed/{accept}")
    public ResponseEntity<ApiResponse> proceedWithdrawal(@PathVariable Long id,@PathVariable boolean accept,@RequestHeader("Authorization") String jwt){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Withdrawal withdrawal=withdrawalService.proceedWithdrawal(id,accept);

            Wallet userWallet=walletService.getUserWallet(user);
            if(!accept){
                walletService.addBalanceToWallet(userWallet, withdrawal.getAmount());
            }

            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(
                            new ApiResponse(
                                    withdrawal,
                                    HttpStatus.ACCEPTED.value(),
                                    "Withdrawal proceeded"
                            )
                    );
        }
        catch(NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            new ApiResponse(
                                    null,
                                    HttpStatus.NOT_FOUND.value(),
                                    e.getMessage()
                            )
                    );
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

}
