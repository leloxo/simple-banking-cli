package com.github.leloxo.bankserver.exception.bankaccount;

public class BankAccountNotFoundException extends Exception {
    public BankAccountNotFoundException(String message) {
        super(message);
    }
}
