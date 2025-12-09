package com.doconnect.userservice.exception;

/**
 * Base runtime exception for user-service specific failures.
 */
public abstract class UserServiceException extends RuntimeException {

    protected UserServiceException(String message) {
        super(message);
    }
}
