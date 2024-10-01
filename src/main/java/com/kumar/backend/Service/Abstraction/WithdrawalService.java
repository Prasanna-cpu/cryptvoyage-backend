package com.kumar.backend.Service.Abstraction;

import com.kumar.backend.Exception.NonExistentUserException;
import com.kumar.backend.Exception.NonExistentWithdrawalException;
import com.kumar.backend.Model.User;
import com.kumar.backend.Model.Withdrawal;

import java.util.List;

public interface WithdrawalService {
    Withdrawal requestWithdrawal(Long amount, User user);

    Withdrawal proceedWithdrawal(Long withdrawalId,boolean accept) throws NonExistentWithdrawalException;

    List<Withdrawal> getUsersWithdrawalHistory(User user) throws NonExistentUserException, NonExistentWithdrawalException;

    List<Withdrawal> getAllWithdrawalRequest();
}
