package com.kumar.backend.Exception;

public class NonExistentPaymentOrderException extends Exception{
    public NonExistentPaymentOrderException(String message) {
        super(message);
    }
}
