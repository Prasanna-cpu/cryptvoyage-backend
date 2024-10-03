package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;

import java.util.List;

public interface TransactionService {

    List<WalletTransaction> getTransactionsByWallet(Wallet wallet);


}
