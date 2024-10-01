package com.kumar.backend.Repository;

import com.kumar.backend.Model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal,Long> {
    @Query("select w from Withdrawal w where w.user.id = ?1")
    Optional<List<Withdrawal>> findAllByUserId(Long userId);
}
