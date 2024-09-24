package com.kumar.backend.Exception;

public class NonExistentEmailException extends Exception{

    public NonExistentEmailException(String message) {
        super(message);
    }
}
