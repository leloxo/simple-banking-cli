package com.github.leloxo.bankserver.model.bankaccount;

import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.util.AccountNumberGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankAccount {
    private Long id;
    private String accountNumber;
    private Customer customer;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public BankAccount() {}

    public BankAccount(Customer customer) {
        if (customer == null) {
            throw new InvalidCustomerDataException("Customer cannot be null.");
        }
        this.accountNumber = AccountNumberGenerator.generateRandomAccountNumber();
        this.customer = customer;
        this.balance = BigDecimal.ZERO;
        this.createdAt = LocalDateTime.now();
    }

    public BankAccount(Long id, String accountNumber, Customer customer, BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = balance;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "BankAccount [id=" + id + ", accountNumber=" + accountNumber + ", " + customer.toString() + ", balance=" + balance + "]";
    }
}
