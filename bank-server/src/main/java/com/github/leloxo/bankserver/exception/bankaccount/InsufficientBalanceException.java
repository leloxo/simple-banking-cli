package com.github.leloxo.bankserver.exception.bankaccount;

public class InsufficientBalanceException extends Exception {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
