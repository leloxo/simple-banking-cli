package com.github.leloxo.bankclient.model.bankaccount;

import com.github.leloxo.bankclient.model.customer.CustomerDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class BankAccountDto {
    private Long id;
    private String accountNumber;
    private CustomerDto customerDto;
    private BigDecimal balance;
    private LocalDateTime createdAt;

    public BankAccountDto() {}

    public BankAccountDto(Long id, String accountNumber, CustomerDto customerDto, BigDecimal balance, LocalDateTime createdAt) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.customerDto = customerDto;
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

    public CustomerDto getCustomerDto() {
        return customerDto;
    }

    public void setCustomerDto(CustomerDto customerDto) {
        this.customerDto = customerDto;
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
        return "BankAccount [id=" + id + ", accountNumber=" + accountNumber + ", " + customerDto.toString() + ", balance=" + balance + "]";
    }
}
