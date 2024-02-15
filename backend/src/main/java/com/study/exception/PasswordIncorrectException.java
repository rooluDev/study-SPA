package com.study.exception;

/**
 * password 불일치 Exception
 */
public class PasswordIncorrectException extends Exception{
    public PasswordIncorrectException() {
    }

    public PasswordIncorrectException(String message) {
        super(message);
    }

    public PasswordIncorrectException(String message, Throwable cause) {
        super(message, cause);
    }
}
