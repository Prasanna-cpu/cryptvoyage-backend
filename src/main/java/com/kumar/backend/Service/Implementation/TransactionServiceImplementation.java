package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Repository.WalletTransactionRepository;
import com.kumar.backend.Service.Abstraction.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class TransactionServiceImplementation implements TransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<WalletTransaction> getTransactionsByWallet(Wallet wallet) {
        List<WalletTransaction> walletTransaction=walletTransactionRepository.findByWalletOrderByDateDesc(wallet);
        return walletTransaction;
    }
}
