package com.kumar.backend.Exception;

public class NonExistentUserException extends Exception{

    public NonExistentUserException(String message) {
        super(message);
    }
}
