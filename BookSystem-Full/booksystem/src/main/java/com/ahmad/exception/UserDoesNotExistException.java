package com.ahmad.exception;

public class UserDoesNotExistException extends RuntimeException{
    /**
     * Exception for user not exist
     * 
     * @param message
     * @return 409
     */
    public UserDoesNotExistException(String message) {
        super(message);
    }

}