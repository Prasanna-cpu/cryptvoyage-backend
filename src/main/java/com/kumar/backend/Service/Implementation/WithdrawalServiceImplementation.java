package com.kumar.backend.Service.Implementation;

import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Exception.NonExistentWithdrawalException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Withdrawal;
import com.kumar.backend.Repository.UserRepository;
import com.kumar.backend.Service.Abstraction.WithdrawalRepository;
import com.kumar.backend.Service.Abstraction.WithdrawalService;
import com.kumar.backend.Utils.Enums.WithdrawalStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(rollbackOn = Exception.class)
@Slf4j
public class WithdrawalServiceImplementation implements WithdrawalService {


    private final WithdrawalRepository withdrawalRepository;
    private final UserRepository userRepository;

    @Override
    public Withdrawal requestWithdrawal(Long amount, User user) {
        Withdrawal withdrawal=new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        Withdrawal savedWithdrawal=withdrawalRepository.save(withdrawal);
        return savedWithdrawal;
    }

    @Override
    public Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws NonExistentWithdrawalException {
        Withdrawal withdrawal=withdrawalRepository
                .findById(withdrawalId)
                .orElseThrow(
                        ()->new NonExistentWithdrawalException("Withdrawal with id : "+withdrawalId+" not found")
                );

        withdrawal.setDate(LocalDateTime.now());

        if(accept){
            withdrawal.setStatus(WithdrawalStatus.SUCCESS);
        }
        else{
            withdrawal.setStatus(WithdrawalStatus.PENDING);
        }

        Withdrawal newWithdrawal=withdrawalRepository.save(withdrawal);

        return newWithdrawal;
    }

    @Override
    public List<Withdrawal> getUsersWithdrawalHistory(User user) throws NonExistentUserException, NonExistentWithdrawalException {
        User findUser=userRepository
                .findById(user.getId())
                .orElseThrow(()->new NonExistentUserException("User with id :"+user.getId()+" not found"));

        List<Withdrawal> withdrawals=withdrawalRepository
                .findAllByUserId(findUser.getId())
                .orElseThrow(()->new NonExistentWithdrawalException("Withdrawals not found: "+findUser.getId()));

        return withdrawals;

    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        List<Withdrawal> allWithdrawals=withdrawalRepository.findAll();
        return allWithdrawals;
    }
}
