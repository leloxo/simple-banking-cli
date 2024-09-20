package com.github.leloxo.bankserver.controller;

import com.github.leloxo.bankserver.exception.bankaccount.BankAccountNotFoundException;
import com.github.leloxo.bankserver.exception.bankaccount.InsufficientBalanceException;
import com.github.leloxo.bankserver.exception.bankaccount.InvalidBankAccountDataException;
import com.github.leloxo.bankserver.exception.customer.CustomerNotFoundException;
import com.github.leloxo.bankserver.exception.customer.InvalidCustomerDataException;
import com.github.leloxo.bankserver.model.bankaccount.BankAccount;
import com.github.leloxo.bankserver.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankserver.model.bankaccount.MoneyTransferRequestPayload;
import com.github.leloxo.bankserver.model.util.DataConverter;
import com.github.leloxo.bankserver.service.BankAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
public class BankAccountController {
    private final BankAccountService bankAccountService;

    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<BankAccountDto>> getBankAccounts() throws CustomerNotFoundException {
        List<BankAccountDto> bankAccounts = bankAccountService.getBankAccounts();
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/get/{email}")
    public ResponseEntity<List<BankAccountDto>> getBankAccountsByEmail(@PathVariable String email) throws CustomerNotFoundException {
        List<BankAccountDto> bankAccounts = bankAccountService.getBankAccountsByEmail(email);
        return ResponseEntity.ok(bankAccounts);
    }

    @PostMapping("/create/{email}")
    public ResponseEntity<BankAccountDto> createBankAccount(@PathVariable String email) throws CustomerNotFoundException {
        BankAccount bankAccount = bankAccountService.createBankAccount(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(DataConverter.toBankAccountDto(bankAccount));
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody MoneyTransferRequestPayload transferRequest) throws BankAccountNotFoundException, InsufficientBalanceException, CustomerNotFoundException {
        if (transferRequest == null) {
            throw new InvalidBankAccountDataException("Money transfer request data cannot be null.");
        }
        bankAccountService.transferMoney(transferRequest.getSenderAccountNumber(), transferRequest.getReceiverAccountNumber(), transferRequest.getAmount());
        return ResponseEntity.ok("Money has been successfully transferred.");
    }
}
