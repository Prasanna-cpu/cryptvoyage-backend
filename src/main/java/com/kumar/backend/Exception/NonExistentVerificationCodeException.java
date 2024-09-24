package com.kumar.backend.Exception;

public class NonExistentVerificationCodeException extends Exception {

    public NonExistentVerificationCodeException(String message) {
        super(message);
    }
}
