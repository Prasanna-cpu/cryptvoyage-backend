package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.InsufficientBalanceException;
import com.kumar.backend.Exception.NonExistentWalletException;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Repository.WalletTransactionRepository;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Service.Abstraction.WalletTransactionService;
import com.kumar.backend.Utils.Enums.WalletTransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class WalletTransactionServiceImplemetation implements WalletTransactionService {

    private final WalletTransactionRepository walletTransactionRepository;

    @Override
    public WalletTransaction createTransaction(Wallet wallet, WalletTransactionType type, String transferId, String purpose, Long amount) {
        WalletTransaction walletTransaction=new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setDate(LocalDate.now());
        walletTransaction.setType(type);
        walletTransaction.setTransferId(transferId);
        walletTransaction.setPurpose(purpose);
        walletTransaction.setAmount(amount);

        WalletTransaction savedTransaction=walletTransactionRepository.save(walletTransaction);

        return savedTransaction;
    }

    @Override
    public List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type) {
        List<WalletTransaction> transactions=walletTransactionRepository.findByWalletOrderByDateDesc(wallet);
        return transactions;
    }
}
