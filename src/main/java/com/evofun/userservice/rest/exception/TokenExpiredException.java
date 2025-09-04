package com.evofun.userservice.rest.exception;

public class TokenExpiredException extends AppException {
    public TokenExpiredException(String developerMessage, String userMessage) {
        super(developerMessage, userMessage);
    }
}