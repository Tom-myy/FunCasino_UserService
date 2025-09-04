package com.evofun.userservice.rest.exception;

public class InvalidTokenException extends AppException {
    public InvalidTokenException(String developerMessage, String userMessage) {
        super(developerMessage, userMessage);
    }
}
