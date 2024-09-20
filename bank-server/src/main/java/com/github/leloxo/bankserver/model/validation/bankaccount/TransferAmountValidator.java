package com.github.leloxo.bankserver.model.validation.bankaccount;

import com.github.leloxo.bankserver.exception.bankaccount.InvalidBankAccountDataException;
import com.github.leloxo.bankserver.model.validation.BusinessRules;
import com.github.leloxo.bankserver.model.validation.ValidationType;

import java.math.BigDecimal;

public class TransferAmountValidator implements BusinessRules {
    private static final BigDecimal MAX_TRANSFER_AMOUNT = new BigDecimal("9999999.99");

    @Override
    public boolean supports(ValidationType field) {
        return ValidationType.TRANSFER_AMOUNT.equals(field);
    }

    @Override
    public void validate(String amountString) throws InvalidBankAccountDataException {
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountString);
        } catch (NumberFormatException e) {
            throw new InvalidBankAccountDataException("Invalid amount format.");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidBankAccountDataException("Transfer amount must be greater than zero.");
        }

        if (amount.scale() > 2) {
            throw new InvalidBankAccountDataException("Transfer amount cannot have more than 2 decimal places.");
        }

        if (amount.compareTo(MAX_TRANSFER_AMOUNT) > 0) {
            throw new InvalidBankAccountDataException("Amount exceeds the maximum allowed value.");
        }
    }
}
