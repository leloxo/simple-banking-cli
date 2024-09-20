package com.github.leloxo.bankserver.model.util;

import java.util.Random;

public class AccountNumberGenerator {
    private static final int ACCOUNT_NUMBER_LENGTH = 10;

    public static String generateRandomAccountNumber() {
        Random random = new Random();
        StringBuilder accountNumber = new StringBuilder(ACCOUNT_NUMBER_LENGTH);
        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            accountNumber.append(random.nextInt(10));
        }
        return accountNumber.toString();
    }

    public static int getAccountNumberLength() {
        return ACCOUNT_NUMBER_LENGTH;
    }
}
