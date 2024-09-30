package com.kumar.backend.Exception;

public class NonExistentOrderException extends Exception {
    public NonExistentOrderException(String message){
        super(message);
    }
}
