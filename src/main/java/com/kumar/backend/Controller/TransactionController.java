package com.kumar.backend.Controller;

import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.TransactionService;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Service.Abstraction.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final UserService userService;

    private final WalletService walletService;

    private final TransactionService transactionService;

    @GetMapping("/get-transactions")
    public ResponseEntity<ApiResponse> getTransactionsByWallet(@RequestHeader("Authorization") String jwt){
        try{
            User user=userService.findUserProfileByJwt(jwt);
            Wallet wallet=walletService.getUserWallet(user);
            List<WalletTransaction> transactions=transactionService.getTransactionsByWallet(wallet);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(transactions,HttpStatus.OK.value(),"Transactions retrieved successfully"));
        }
        catch(NonExistentUserException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }



}
