package com.kumar.backend.Repository;

import com.kumar.backend.Model.ForgetPasswordToken;
import com.kumar.backend.Model.User;
import com.kumar.backend.Utils.Enums.VerificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ForgetPasswordRepository extends JpaRepository<ForgetPasswordToken, String> {
    ;
    @Query("select t from ForgetPasswordToken t where t.user.id = ?1")
    Optional<ForgetPasswordToken> findByUserId(Long userId);


}
