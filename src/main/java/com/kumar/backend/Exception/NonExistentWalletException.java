package com.kumar.backend.Exception;

public class NonExistentWalletException extends Exception {

    public NonExistentWalletException(String message) {
        super(message);
    }
}
