package com.kumar.backend.Repository;

import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    @Query("select w from Wallet w where w.user.id = ?1")
    Wallet findByUserId(Long userId);

    @Repository
    interface WithdrawalRepository extends JpaRepository<Withdrawal,Long> {

        @Query("select w from Withdrawal w where w.user.id = ?1")
        Optional<List<Withdrawal>> findAllByUserId(Long userId);

    }
}
