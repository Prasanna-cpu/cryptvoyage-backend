package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.InsufficientBalanceException;
import com.kumar.backend.Exception.NonExistentWalletException;
import com.kumar.backend.Model.Order;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Wallet;
import com.kumar.backend.Repository.WalletRepository;
import com.kumar.backend.Service.Abstraction.WalletService;
import com.kumar.backend.Utils.Enums.OrderType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class WalletServiceImplementation implements WalletService {


    private final WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet=walletRepository.findByUserId(user.getId());
        if(wallet==null){
            wallet=new Wallet();
            wallet.setUser(user);
        }
        return wallet;
    }

    @Override
    public Wallet addBalanceToWallet(Wallet wallet, Long money) {
        BigDecimal balance=wallet.getBalance();
        BigDecimal newBalance=balance.add(BigDecimal.valueOf(money));
        wallet.setBalance(newBalance);
        Wallet savedWallet=walletRepository.save(wallet);
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
        Wallet wallet=getUserWallet(user);

        if(order.getOrderType().equals(OrderType.BUY)){
            BigDecimal newBalance=wallet.getBalance().subtract(order.getPrice());
            if(newBalance.compareTo(order.getPrice())<0){
                throw new InsufficientBalanceException("Insufficient Balance");
            }
            wallet.setBalance(newBalance);
        }
        else{
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }
        walletRepository.save(wallet);
        return wallet;
    }
}
