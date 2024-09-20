package com.github.leloxo.bankserver.model.validation.customer;

import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.validation.BusinessRules;
import com.github.leloxo.bankserver.model.validation.ValidationType;

public class PasswordValidator implements BusinessRules {
    private static final int PASSWORD_LENGTH = 8;

    @Override
    public boolean supports(ValidationType field) {
        return ValidationType.PASSWORD.equals(field);
    }

    @Override
    public void validate(String password) throws InvalidCustomerDataException {
        if (password == null || password.isEmpty()) {
            throw new InvalidCustomerDataException("Password cannot be null or empty.");
        }
        if (password.length() < PASSWORD_LENGTH) {
            throw new InvalidCustomerDataException("Password must be at least " + PASSWORD_LENGTH + " characters long.");
        }
    }
}
