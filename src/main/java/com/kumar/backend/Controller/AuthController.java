package com.kumar.backend.Controller;


import com.kumar.backend.Configuration.JwtProvider;
import com.kumar.backend.Exception.ExistingEmailException;
import com.kumar.backend.Exception.NonExistentEmailException;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.TwoFactorOTP;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.UserRepository;
import com.kumar.backend.Request.UserLoginRequest;
import com.kumar.backend.Request.UserRegisterRequest;
import com.kumar.backend.Response.ApiResponse;
import com.kumar.backend.Response.AuthResponse;
import com.kumar.backend.Service.Abstraction.EmailSender;
import com.kumar.backend.Service.Abstraction.TwoFactorOTPService;
import com.kumar.backend.Service.Abstraction.WatchListService;
import com.kumar.backend.Service.Implementation.CustomUserDetailsService;
import com.kumar.backend.Utils.OTP.OTPUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmailSender emailSender;
    private final TwoFactorOTPService twoFactorOTPService;
    private final PasswordEncoder passwordEncoder;
    private final WatchListService watchListService;

    @PostMapping("/register")
    public ResponseEntity<? extends ApiResponse> register(@RequestBody UserRegisterRequest userRegisterRequest)  {
        try {
            User newUser = new User();

            User existingEmail = userRepository
                    .findByEmail(userRegisterRequest.getEmail())
                    .orElse(null);
            if (existingEmail != null) {
                throw new ExistingEmailException("Email already exists");
            }


            newUser.setEmail(userRegisterRequest.getEmail());
            newUser.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
            newUser.setFullname(userRegisterRequest.getFullname());
            newUser.setMobile(userRegisterRequest.getMobile());
            User user=userRepository.save(newUser);

            watchListService.createWatchList(user);

            Authentication auth=new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    user.getPassword()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            String jwt= JwtProvider.generateToken(auth);

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            new AuthResponse(user, HttpStatus.CREATED.value(), "User Registered Successfully",jwt)
                    );
        }

        catch (ExistingEmailException e){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ApiResponse(null,HttpStatus.CONFLICT.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<? extends ApiResponse> login(@RequestBody UserLoginRequest userLoginRequest)  {
        try{
            String username=userLoginRequest.getEmail();
            String password=userLoginRequest.getPassword();

            User targetUser=userRepository
                    .findByEmail(username)
                    .orElseThrow(()->new NonExistentEmailException("User not found with email : "+username));

            Authentication authentication=authenticate(username,password);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt= JwtProvider.generateToken(authentication);

            if(targetUser.getTwoFactorAuthentication().isEnabled()){
                AuthResponse authResponse=new AuthResponse();
                authResponse.setMessage("Two factor authentication enabled");
                authResponse.setTwoFactorEnabled(true);
                String otp= OTPUtils.generateOTP();

                TwoFactorOTP oldOTP=twoFactorOTPService.findByUser(targetUser.getId());

                if(oldOTP!=null){
                    twoFactorOTPService.deleteTwoFactorOTP(oldOTP);
                }

                TwoFactorOTP newOTP=twoFactorOTPService.createTwoFactorOTP(targetUser,otp,jwt);

                emailSender.sendVerificationOtpEmail(username,otp);

                authResponse.setSession(newOTP.getId());
                return ResponseEntity.status(HttpStatus.ACCEPTED)
                        .body(
                                new AuthResponse(null,HttpStatus.ACCEPTED.value(),"OTP Issued Successfully",jwt)
                        );
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                        new AuthResponse(null,HttpStatus.OK.value(),"Login Successful",jwt)
                    );
        }
        catch(BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(null,HttpStatus.BAD_REQUEST.value(),e.getMessage()));
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    private Authentication authenticate(String username,String password) {
        UserDetails userDetails=customUserDetailsService.loadUserByUsername(username);
        if(userDetails==null){
            throw new BadCredentialsException("Invalid username / password");
        }
        if(!passwordEncoder.matches(password,userDetails.getPassword())){
            throw new BadCredentialsException("Invalid  password");
        }

        Authentication auth=new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());
        return auth;
    }

//    @PostMapping("/two-factor/otp/{otp}")
//    public ResponseEntity<? extends ApiResponse> verifyOTP(@PathVariable String otp,@RequestParam String id) {
//        try{
//            TwoFactorOTP twoFactorOTP=twoFactorOTPService.findById(id);
//
//            if(twoFactorOTPService.verifyTwoFactorOTP(twoFactorOTP,otp)){
//                AuthResponse authResponse=new AuthResponse();
//                authResponse.setMessage("Two factor authentication verified");
//                authResponse.setStatus(HttpStatus.OK.value());
//                authResponse.setTwoFactorEnabled(true);
//                authResponse.setToken(twoFactorOTP.getJwt());
//                return ResponseEntity
//                        .status(HttpStatus.OK)
//                        .body(authResponse);
//            }
//            throw new BadCredentialsException("Invalid OTP");
//        }
//        catch (NonExistentEmailException e){
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ApiResponse(null,HttpStatus.NOT_FOUND.value(),e.getMessage()));
//        }
//        catch(BadCredentialsException e){
//            return ResponseEntity
//                    .status(HttpStatus.BAD_REQUEST)
//                    .body(new ApiResponse(null,HttpStatus.BAD_REQUEST.value(),e.getMessage()));
//        }
//        catch (Exception e){
//             return ResponseEntity
//                     .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                     .body(new ApiResponse(null,HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
//        }
//
//
//
//    }


}
