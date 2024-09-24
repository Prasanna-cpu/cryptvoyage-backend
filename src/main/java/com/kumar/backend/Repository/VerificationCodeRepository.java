package com.kumar.backend.Repository;

import com.kumar.backend.Model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode,Long> {

    Optional<VerificationCode> findByUserId(Long userId);




}
