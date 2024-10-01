package com.kumar.backend.Exception;

public class NonExistentWithdrawalException extends Exception {
    public NonExistentWithdrawalException(String message) {
        super(message);
    }
}
