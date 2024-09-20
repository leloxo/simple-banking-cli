package com.github.leloxo.bankserver.model.validation;

public interface BusinessRules {
    boolean supports(ValidationType field);
    void validate(String value);
}
