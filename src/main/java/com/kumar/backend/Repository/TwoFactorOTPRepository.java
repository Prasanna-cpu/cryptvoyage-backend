package com.kumar.backend.Repository;

import com.kumar.backend.Model.TwoFactorOTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TwoFactorOTPRepository extends JpaRepository<TwoFactorOTP, String> {

    @Query("select t from TwoFactorOTP t where t.user.id = ?1")
    TwoFactorOTP findByUserId(Long userId);
}
