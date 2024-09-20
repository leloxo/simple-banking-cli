package com.github.leloxo.bankserver.exception.customer;

public class CustomerRepositoryException extends RuntimeException {
    public CustomerRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
