package com.kumar.backend.Controller;


import com.kumar.backend.Model.PaymentDetails;
import com.kumar.backend.Repository.UserRepository;
import com.kumar.backend.Request.PaymentDetailsRequest;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Service.Abstraction.PaymentDetailsService;
import com.kumar.backend.Service.Abstraction.UserService;
import lombok.RequiredArgsConstructor;
import com.kumar.backend.Model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/paymentDetails")
@RequiredArgsConstructor
public class PaymentDetailsController {
    private final UserService userService;
    private final PaymentDetailsService paymentDetailsService;


    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addPaymentDetails(@RequestBody PaymentDetailsRequest request, @RequestHeader("Authorization") String jwt) {
        try{
            User user=userService.findUserProfileByJwt(jwt);
            PaymentDetails paymentDetails=paymentDetailsService.addPaymentDetails(
                    request.getAccountNumber(),
                    request.getAccountHolderName(),
                    request.getBankName(),
                    request.getIfscCode(),
                    user
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new ApiResponse(
                                    paymentDetails,
                                    HttpStatus.CREATED.value(),
                                    "Payment Details created and saved"
                            )
                    );


        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage())
            );
        }
    }

    @GetMapping("/details")
    public ResponseEntity<ApiResponse> getUserPaymentDetails(@RequestHeader("Authorization") String jwt) {
        try{
            User user=userService.findUserProfileByJwt(jwt);
            PaymentDetails paymentDetails=paymentDetailsService.getUserPaymentDetails(user);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ApiResponse(
                                    paymentDetails,
                                    HttpStatus.OK.value(),
                                    "Payment Details fetched"
                            )
                    );


        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }


}
