package com.doconnect.userservice.exception;

/**
 * Raised when a profile is attempted for an auth user that already has one.
 */
public class ProfileAlreadyExistsException extends UserServiceException {

    public ProfileAlreadyExistsException(String authUserId) {
        super("Profile already exists for auth user id: " + authUserId);
    }
}
