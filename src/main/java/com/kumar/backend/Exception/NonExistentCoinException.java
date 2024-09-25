package com.kumar.backend.Exception;

public class NonExistentCoinException extends Exception{

    public NonExistentCoinException(String message){
        super(message);
    }
}
