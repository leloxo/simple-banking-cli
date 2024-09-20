package com.github.leloxo.bankserver.exception.bankaccount;

public class InvalidBankAccountDataException extends RuntimeException {
    public InvalidBankAccountDataException(String message) {
        super(message);
    }
}
