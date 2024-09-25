package com.kumar.backend.Repository;

import com.kumar.backend.Model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface WalletRepository extends JpaRepository<Wallet,Long> {

    @Query("select w from Wallet w where w.user.id = ?1")
    Wallet findByUserId(Long userId);
}
