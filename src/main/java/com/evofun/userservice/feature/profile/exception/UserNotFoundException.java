package com.evofun.userservice.feature.profile.exception;

import com.evofun.userservice.shared.exception.AppException;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String developerMessage, String userMessage) {
        super(developerMessage, userMessage);
    }
}