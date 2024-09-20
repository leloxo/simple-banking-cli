package com.github.leloxo.bankserver.model.validation;

import com.github.leloxo.bankserver.model.validation.bankaccount.TransferAmountValidator;
import com.github.leloxo.bankserver.model.validation.bankaccount.BankAccountNumberValidator;
import com.github.leloxo.bankserver.model.validation.customer.EmailValidator;
import com.github.leloxo.bankserver.model.validation.customer.NameValidator;
import com.github.leloxo.bankserver.model.validation.customer.PasswordValidator;

import java.util.Arrays;
import java.util.List;

public class ValidationManager {
    private final List<BusinessRules> businessRules = Arrays.asList(
            new EmailValidator(),
            new PasswordValidator(),
            new NameValidator(),
            new BankAccountNumberValidator(),
            new TransferAmountValidator()
    );

    public void validate(ValidationType field, String input) {
        for (BusinessRules eachBusinessRule : this.businessRules) {
            if (eachBusinessRule.supports(field)) {
                eachBusinessRule.validate(input);
                return;
            }
        }
    }
}
