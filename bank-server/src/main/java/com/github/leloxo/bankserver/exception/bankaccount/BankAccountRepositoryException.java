package com.github.leloxo.bankserver.exception.bankaccount;

public class BankAccountRepositoryException extends RuntimeException {
    public BankAccountRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
