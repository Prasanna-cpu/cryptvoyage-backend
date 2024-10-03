package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Configuration.JwtProvider;
import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.TwoFactorAuthentication;
import com.kumar.backend.Model.User;
import com.kumar.backend.Repository.UserRepository;
import com.kumar.backend.Service.Abstraction.UserService;
import com.kumar.backend.Utils.Enums.VerificationType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;


    @Override
    public User findUserProfileByJwt(String jwt) throws NonExistentUserException {

        String email= JwtProvider.getEmailFromToken(jwt);

        User user=userRepository.findByEmail(email).orElseThrow(()->new NonExistentUserException("User not found with email : "+email));

        return user;
    }

    @Override
    public User findUserByEmail(String email) throws NonExistentUserException {

        User user=userRepository
                .findByEmail(email)
                .orElseThrow(()->new NonExistentUserException("User not found with email : "+email));
        return user;
    }

    @Override
    public User findUserById(Long userId) throws NonExistentUserException {
        User user=userRepository.findById(userId).orElseThrow(()->new NonExistentUserException("User not found with id : "+userId));
        return user;
    }

    @Override
    public User enableTwoFactorAuthentication(User user, VerificationType verificationType, String sendTo) {
        TwoFactorAuthentication twoFactorAuthentication=new TwoFactorAuthentication();
        twoFactorAuthentication.setEnabled(true);
        twoFactorAuthentication.setSendTo(verificationType);
        user.setTwoFactorAuthentication(twoFactorAuthentication);
        User savedUser=userRepository.save(user);
        return savedUser;
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        return userRepository.save(user);
    }
}
