package com.github.leloxo.bankserver.model.util;

import com.github.leloxo.bankserver.model.bankaccount.BankAccount;
import com.github.leloxo.bankserver.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.customer.CustomerDto;

public class DataConverter {

    public static CustomerDto toCustomerDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getCreatedAt()
        );
    }

    public static BankAccountDto toBankAccountDto(BankAccount bankAccount) {
        CustomerDto customerDto = toCustomerDto(bankAccount.getCustomer());
        return new BankAccountDto(
                bankAccount.getId(),
                bankAccount.getAccountNumber(),
                customerDto,
                bankAccount.getBalance(),
                bankAccount.getCreatedAt()
        );
    }
}
