package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Utils.Enums.VerificationType;

public interface UserService {

    User findUserProfileByJwt(String jwt) throws NonExistentUserException;

    User findUserByEmail(String email) throws NonExistentUserException;

    User findUserById(Long userId) throws NonExistentUserException;

    User enableTwoFactorAuthentication(User user, VerificationType verificationType,String sendTo);

    User updatePassword(User user,String newPassword);

}
