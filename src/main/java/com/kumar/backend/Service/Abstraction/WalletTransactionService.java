package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Utils.Enums.WalletTransactionType;

import java.util.List;

public interface WalletTransactionService {
    WalletTransaction createTransaction(Wallet wallet,
                                        WalletTransactionType type,
                                        String transferId,
                                        String purpose,
                                        Long amount
    );

    List<WalletTransaction> getTransactions(Wallet wallet, WalletTransactionType type);
}
