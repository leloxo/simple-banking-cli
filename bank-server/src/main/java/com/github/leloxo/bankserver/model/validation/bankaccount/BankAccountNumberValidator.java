package com.github.leloxo.bankserver.model.validation.bankaccount;

import com.github.leloxo.bankserver.exception.bankaccount.InvalidBankAccountDataException;
import com.github.leloxo.bankserver.model.util.AccountNumberGenerator;
import com.github.leloxo.bankserver.model.validation.BusinessRules;
import com.github.leloxo.bankserver.model.validation.ValidationType;

public class BankAccountNumberValidator implements BusinessRules {
    @Override
    public boolean supports(ValidationType field) {
        return ValidationType.BANK_ACCOUNT_NUMBER.equals(field);
    }

    @Override
    public void validate(String accountNumber) throws InvalidBankAccountDataException {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new InvalidBankAccountDataException("Account number can not be null or empty");
        }
        if (accountNumber.length() != AccountNumberGenerator.getAccountNumberLength()) {
            throw new InvalidBankAccountDataException("Account number has to be "
                    + AccountNumberGenerator.getAccountNumberLength() + " characters long.");
        }
    }
}
