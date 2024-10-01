package com.kumar.backend.Exception;

public class NonExistentAssetException extends Exception {

    public NonExistentAssetException(String message) {
        super(message);
    }
}
