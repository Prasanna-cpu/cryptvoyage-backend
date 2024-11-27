package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.InsufficientBalanceException;
import com.kumar.backend.Exception.NonExistentWalletException;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Model.WalletTransaction;
import com.kumar.backend.Repository.WalletRepository;
import com.kumar.backend.Repository.WalletTransactionRepository;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Service.Abstraction.WalletTransactionService;
import com.kumar.backend.Utils.Enums.OrderType;
import com.kumar.backend.Utils.Enums.WalletTransactionType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImplementation implements WalletService {


    private final WalletRepository walletRepository;
    private final WalletTransactionRepository walletTransactionRepository;
    private final WalletTransactionService walletTransactionService;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet=walletRepository.findByUserId(user.getId());
        if(wallet==null){
            wallet=new Wallet();
            wallet.setUser(user);
            walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalanceToWallet(Wallet wallet, Long money) {
        BigDecimal balance=wallet.getBalance();
        BigDecimal newBalance=balance.add(BigDecimal.valueOf(money));
        wallet.setBalance(newBalance);
        Wallet savedWallet=walletRepository.save(wallet);

        WalletTransaction walletTransaction=walletTransactionService.createTransaction(savedWallet, WalletTransactionType.ADD_MONEY, null, "Add Money", money);
        return savedWallet;
    }

    @Override
    public Wallet findWalletById(Long id) throws NonExistentWalletException {
        Wallet wallet=walletRepository.findById(id).orElseThrow(()->new NonExistentWalletException("Wallet not found with id " + id));
        return wallet;
    }

    @Override
    public Wallet wallletToWalletTransfer(User sender, Wallet recieverWallet, Long amount) throws InsufficientBalanceException {
        Wallet senderWallet=getUserWallet(sender);
        if(senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount))<0){
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        BigDecimal senderBalance=senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepository.save(senderWallet);

        BigDecimal receiverBalance=recieverWallet.getBalance().add(BigDecimal.valueOf(amount));
        recieverWallet.setBalance(receiverBalance);
        walletRepository.save(recieverWallet);
        return senderWallet;
    }

    @Override
    public Wallet orderPayment(Order order, User user) throws InsufficientBalanceException {
        Wallet wallet = getUserWallet(user);

        WalletTransaction walletTransaction=new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setPurpose(order.getOrderType()+ " " + order.getOrderItem().getCoin().getId() );

        walletTransaction.setDate(LocalDate.now());
        walletTransaction.setTransferId(order.getOrderItem().getCoin().getSymbol());

        // Null check for order and its related fields
        if (order.getOrderItem() == null || order.getOrderItem().getCoin() == null) {
            throw new IllegalArgumentException("Order, order item, or coin cannot be null");
        }

        // Null check for order price
        if (order.getPrice() == null) {
            throw new IllegalArgumentException("Order price cannot be null");
        }



        BigDecimal newBalance;

        if (order.getOrderType().equals(OrderType.BUY)) {
            // Subtract the order price from the wallet balance
            newBalance = wallet.getBalance().subtract(order.getPrice());

            // Check if the new balance is below zero, indicating insufficient funds
            if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new InsufficientBalanceException("Insufficient Balance");
            }

            wallet.setBalance(newBalance);
        }
        else if (order.getOrderType().equals(OrderType.SELL)) {
            // Add the order price to the wallet balance for a sell order
            newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }

        // Save the transaction and updated wallet
        walletTransactionRepository.save(walletTransaction);
        walletRepository.save(wallet);

        return wallet;
    }

}
