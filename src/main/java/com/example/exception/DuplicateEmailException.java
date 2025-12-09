package com.example.exception;

/**
 * 邮箱重复异常
 */
public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}