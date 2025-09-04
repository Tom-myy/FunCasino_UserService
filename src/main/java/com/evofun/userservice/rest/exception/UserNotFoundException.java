package com.evofun.userservice.rest.exception;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(String developerMessage, String userMessage) {
        super(developerMessage, userMessage);

    }
}