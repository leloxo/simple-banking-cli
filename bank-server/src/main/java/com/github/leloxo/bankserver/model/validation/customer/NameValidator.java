package com.github.leloxo.bankserver.model.validation.customer;

import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.validation.BusinessRules;
import com.github.leloxo.bankserver.model.validation.ValidationType;

public class NameValidator implements BusinessRules {
    @Override
    public boolean supports(ValidationType field) {
        return ValidationType.NAME.equals(field);
    }

    @Override
    public void validate(String name) throws InvalidCustomerDataException {
        if (name == null || name.isEmpty()) {
            throw new InvalidCustomerDataException("Name cannot be null or empty.");
        }
    }
}
