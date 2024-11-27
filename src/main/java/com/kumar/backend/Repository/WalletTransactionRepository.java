package com.kumar.backend.Repository;

import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction,Long> {
    List<WalletTransaction> findByWalletOrderByDateDesc(Wallet wallet);

}
