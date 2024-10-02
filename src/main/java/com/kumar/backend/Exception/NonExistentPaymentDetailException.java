package com.kumar.backend.Exception;

public class NonExistentPaymentDetailException extends Exception{

    public NonExistentPaymentDetailException(String message) {
        super(message);
    }
}
