package com.doconnect.userservice.exception;

/**
 * Raised when a user profile cannot be located by id or auth user id.
 */
public class UserNotFoundException extends UserServiceException {

    public UserNotFoundException(String identifier) {
        super("User not found for identifier: " + identifier);
    }
}
