package com.example.exception;

/**
 * 用户未找到异常
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}