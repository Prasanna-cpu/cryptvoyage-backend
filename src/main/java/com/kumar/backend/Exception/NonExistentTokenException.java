package com.kumar.backend.Exception;

public class NonExistentTokenException extends Exception{

    public NonExistentTokenException(String message) {
        super(message);
    }
}
