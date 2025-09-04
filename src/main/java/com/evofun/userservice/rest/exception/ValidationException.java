package com.evofun.userservice.rest.exception;

public class ValidationException extends AppException {
    public ValidationException(String developerMessage, String userMessage) {
        super(developerMessage, userMessage);
    }
}