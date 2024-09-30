package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.InsufficientBalanceException;
import com.kumar.backend.Exception.NonExistentWalletException;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;

public interface WalletService {
    Wallet getUserWallet(User user);

    Wallet addBalanceToWallet(Wallet wallet,Long money);

    Wallet findWalletById(Long id) throws NonExistentWalletException;

    Wallet wallletToWalletTransfer(User sender,Wallet recieverWallet,Long amount) throws InsufficientBalanceException;

    Wallet orderPayment(Order order,User user) throws InsufficientBalanceException;


}
