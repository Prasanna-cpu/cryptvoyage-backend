package com.kumar.backend.Controller;

import com.kumar.backend.Exception.NonExistentEmailException;
import com.kumar.backend.Exception.NonExistentTokenException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Exception.NonExistentVerificationCodeException;
import com.kumar.backend.Model.ForgetPasswordToken;
import com.kumar.backend.Model.TwoFactorOTP;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.VerificationCode;
import com.kumar.backend.Request.ForgetPasswordTokenRequest;
import com.kumar.backend.Request.ResetPasswordRequest;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Response.AuthResponse;
import com.kumar.backend.Service.Abstraction.*;
import com.kumar.backend.Utils.Enums.VerificationType;
import com.kumar.backend.Utils.OTP.OTPUtils;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final EmailSender emailSender;

    private final VerificationCodeService verificationCodeService;

    private final TwoFactorOTPService twoFactorOTPService;

    private final ForgetPasswordService forgetPasswordService;


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getUserProfile(@RequestHeader("Authorization") String jwt)  {

        try{
            User user=userService.findUserProfileByJwt(jwt);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(user,HttpStatus.OK.value(),"User retrieved successfully"));
        }
        catch(NonExistentUserException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @PostMapping("/verification/{verificationType}/send-otp")
    public ResponseEntity<ApiResponse> sendVerificationOtp(@RequestHeader("Authorization") String jwt,@PathVariable VerificationType verificationType) throws NonExistentUserException {
        try{
            User user=userService.findUserProfileByJwt(jwt);

            VerificationCode verificationCode=verificationCodeService.getVerificationCodeByUserId(user.getId());

            if(verificationCode==null){
                verificationCode=verificationCodeService.sendVerificationCode(verificationType, user);
            }
            if(verificationType.equals(VerificationType.EMAIL)){
                emailSender.sendVerificationOtpEmail(user.getEmail(),verificationCode.getOtp());
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ApiResponse(verificationCode,HttpStatus.OK.value(),"Verification code sent successfully"));

        }
        catch(NonExistentUserException | NonExistentVerificationCodeException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @PatchMapping("/enable-2fa/{otp}")
    public ResponseEntity<ApiResponse> enableTwoFactorAuth(@RequestHeader("Authorization") String jwt,@PathVariable String otp) throws NonExistentUserException {
        try{
            User user=userService.findUserProfileByJwt(jwt);

            VerificationCode verificationCode=verificationCodeService.getVerificationCodeByUserId(user.getId());

            String sendTo=verificationCode.getVerificationType().equals(VerificationType.EMAIL)?verificationCode.getEmail():verificationCode.getMobile();

            boolean isVerified=verificationCode.getOtp().equals(otp);

            if(isVerified){
                User updatedUser=userService.enableTwoFactorAuthentication(user, verificationCode.getVerificationType(), sendTo);
                verificationCodeService.deleteVerificationCode(verificationCode);

                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(new ApiResponse(updatedUser,HttpStatus.OK.value(),"Two Factor Authentication enabled successfully"));
            }
            throw new Exception("Wrong otp");

        }
        catch(NonExistentUserException | NonExistentVerificationCodeException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null, HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }


    }

    @PostMapping("/reset-password/send-otp")
    public ResponseEntity<? extends ApiResponse> sendForgotPasswordOTP(@RequestBody ForgetPasswordTokenRequest request) {

        try{
            User user=userService.findUserByEmail(request.getSendTo());
            String otp= OTPUtils.generateOTP();
            UUID uuid=UUID.randomUUID();
            String id=uuid.toString();

            ForgetPasswordToken token=forgetPasswordService.findByUserId(user.getId());

            if(token==null){
                token=forgetPasswordService.createToken(user, id, otp, request.getVerificationType(), request.getSendTo());
            }

            if(request.getVerificationType().equals(VerificationType.EMAIL)){
                emailSender.sendVerificationOtpEmail(user.getEmail(), token.getOtp());
            }



            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            new ApiResponse(token.getId(),HttpStatus.OK.value(),"OTP sent successfully")
                    );



        }
        catch(NonExistentUserException | NonExistentTokenException e){
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

    @PatchMapping("/reset-password")
    public ResponseEntity<ApiResponse> resetPassword(@RequestBody ResetPasswordRequest request,@RequestParam String id)  {
        try{

            ForgetPasswordToken token=forgetPasswordService.findById(id);
            boolean isVerified=token.getOtp().equals(request.getOtp());
            if(isVerified){
                userService.updatePassword(token.getUser(),request.getPassword());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(null,HttpStatus.ACCEPTED.value(),"Password Updated" ));
            }
            throw new BadCredentialsException("Wrong otp");
        }
        catch(BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null,HttpStatus.BAD_REQUEST.value(),e.getMessage()));
        }
        catch(NonExistentTokenException e){
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

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<? extends ApiResponse> verifyOTP(@PathVariable String otp,@RequestParam String id) {
        try{
            TwoFactorOTP twoFactorOTP=twoFactorOTPService.findById(id);

            if(twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP,otp)){
                AuthResponse authResponse=new AuthResponse();
                authResponse.setMessage("Two factor authentication verified");
                authResponse.setStatus(HttpStatus.OK.value());
                authResponse.setTwoFactorEnabled(true);
                authResponse.setToken(twoFactorOTP.getJwt());
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(authResponse);
            }
            throw new BadCredentialsException("Invalid OTP");
        }
        catch (NonExistentEmailException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null,HttpStatus.BAD_REQUEST.value(),e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }



    }

    @GetMapping("/email/{email}")
    public ResponseEntity<? extends ApiResponse> findUserByEmail(@PathVariable String email,@RequestHeader("Authorization") String jwt) {
        try{
            User user=userService.findUserByEmail(email);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(user,HttpStatus.OK.value(),"User retrieved"));
        }
        catch(NonExistentUserException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<? extends ApiResponse> findUserById(@PathVariable Long userId,@RequestHeader("Authorization") String jwt) {
        try{
            User user=userService.findUserById(userId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(user,HttpStatus.OK.value(),"User retrieved"));
        }
        catch(NonExistentUserException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

}
