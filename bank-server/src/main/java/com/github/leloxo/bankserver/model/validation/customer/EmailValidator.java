package com.github.leloxo.bankserver.model.validation.customer;

import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.validation.BusinessRules;
import com.github.leloxo.bankserver.model.validation.ValidationType;

public class EmailValidator implements BusinessRules {
    @Override
    public boolean supports(ValidationType field) {
        return ValidationType.EMAIL.equals(field);
    }

    @Override
    public void validate(String email) throws InvalidCustomerDataException {
        if (email == null || email.isEmpty()) {
            throw new InvalidCustomerDataException("Email cannot be null or empty.");
        }
        if (!email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
            throw new InvalidCustomerDataException("Email is not valid.");
        }
    }
}
