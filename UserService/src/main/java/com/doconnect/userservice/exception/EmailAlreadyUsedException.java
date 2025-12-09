package com.doconnect.userservice.exception;

/**
 * Raised when an email address is already attached to another profile.
 */
public class EmailAlreadyUsedException extends UserServiceException {

    public EmailAlreadyUsedException(String email) {
        super("Email already in use: " + email);
    }
}
