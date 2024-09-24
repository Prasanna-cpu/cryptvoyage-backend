package com.kumar.backend.Exception;

public class ExistingEmailException extends Exception{

    public ExistingEmailException(String message) {
        super(message);
    }
}
