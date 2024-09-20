package com.github.leloxo.bankserver.service;

import com.github.leloxo.bankserver.exception.bankaccount.BankAccountNotFoundException;
import com.github.leloxo.bankserver.exception.bankaccount.BankAccountRepositoryException;
import com.github.leloxo.bankserver.exception.bankaccount.InsufficientBalanceException;
import com.github.leloxo.bankserver.exception.bankaccount.InvalidBankAccountDataException;
import com.github.leloxo.bankserver.exception.customer.CustomerNotFoundException;
import com.github.leloxo.bankserver.exception.customer.CustomerRepositoryException;
import com.github.leloxo.bankserver.model.bankaccount.BankAccount;
import com.github.leloxo.bankserver.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankserver.model.customer.Customer;
import com.github.leloxo.bankserver.model.validation.ValidationManager;
import com.github.leloxo.bankserver.model.validation.ValidationType;
import com.github.leloxo.bankserver.repository.BankAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class BankAccountService {
    private static final Logger logger = LoggerFactory.getLogger(BankAccountService.class);

    private final BankAccountRepository bankAccountRepository;
    private final CustomerService customerService;
    private final ValidationManager validationManager;

    public BankAccountService(BankAccountRepository bankAccountRepository, CustomerService customerService, ValidationManager validationManager) {
        this.bankAccountRepository = bankAccountRepository;
        this.customerService = customerService;
        this.validationManager = validationManager;
    }

    /**
     * Retrieves all customers from the repository.
     *
     * @return A list of all customers.
     * @throws CustomerNotFoundException If the customer of the bank account is not found.
     * @throws BankAccountRepositoryException If an error occurs while retrieving customers.
     */
    public List<BankAccountDto> getBankAccounts() throws CustomerNotFoundException {
        try {
            logger.info("Fetching all bank accounts.");
            return bankAccountRepository.getBankAccounts();
        } catch (BankAccountRepositoryException e) {
            logger.error("Error while fetching bank accounts: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Retrieves a list of bank accounts associated with a customer based on the provided email address.
     *
     * @param email The email address of the customer whose bank accounts are being retrieved.
     * @return A list of {@link BankAccountDto} associated with the customer.
     * @throws CustomerNotFoundException If no customer with the provided email is found.
     * @throws BankAccountRepositoryException If there is an error while retrieving the bank accounts.
     */
    public List<BankAccountDto> getBankAccountsByEmail(String email) throws CustomerNotFoundException {
        Customer customer = customerService.getCustomerByEmail(email);

        Long customerId = customer.getId();
        try {
            logger.info("Searching for bank account of customer with email: {}", email);
            return bankAccountRepository.getBankAccountsByCustomerId(customerId);
        } catch (BankAccountRepositoryException e) {
            logger.error("Error while searching bank account of customer with email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Creates a new bank account and saves it to the repository.
     *
     * @param email The email of the customer for whom the bank account is created.
     * @return The newly created {@link BankAccount}.
     * @throws CustomerNotFoundException If no customer with the email is found.
     * @throws CustomerRepositoryException  If an error occurs during the search.
     * @throws BankAccountRepositoryException If an error occurs while saving the bank account.
     */
    public BankAccount createBankAccount(String email) throws CustomerNotFoundException {
        Customer customer = customerService.getCustomerByEmail(email);

        BankAccount bankAccount = new BankAccount(customer);
        try {
            logger.info("Creating new bank account for customer with email: {}", email);
            return bankAccountRepository.saveBankAccount(bankAccount);
        } catch (BankAccountRepositoryException e) {
            logger.error("Error while creating bank account for customer with email {}: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Transfers money between two bank accounts.
     *
     * @param senderAccountNumber   The account number from which the money is sent.
     * @param receiverAccountNumber The account number to which the money is sent.
     * @param amount                The amount of money to transfer.
     * @throws BankAccountNotFoundException If either the sender or receiver account does not exist.
     * @throws InsufficientBalanceException If the sender account does not have enough balance.
     * @throws CustomerNotFoundException If no customer is associated with the account.
     */
    public void transferMoney(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount) throws BankAccountNotFoundException, CustomerNotFoundException, InsufficientBalanceException {
        validationManager.validate(ValidationType.BANK_ACCOUNT_NUMBER, senderAccountNumber);
        validationManager.validate(ValidationType.BANK_ACCOUNT_NUMBER, receiverAccountNumber);
        validationManager.validate(ValidationType.TRANSFER_AMOUNT, amount.toString());

        if (!existsByAccountNumber(senderAccountNumber)) {
            logger.warn("The sender's bank account with account number {} does not exist.", senderAccountNumber);
            throw new BankAccountNotFoundException("Bank account does not exist.");
        }
        if (!existsByAccountNumber(receiverAccountNumber)) {
            logger.warn("The receiver's bank account with account number {} does not exist.", receiverAccountNumber);
            throw new BankAccountNotFoundException("Bank account does not exist.");
        }

        BankAccountDto bankAccountDto = getBankAccountByAccountNumber(senderAccountNumber);
        if (bankAccountDto.getBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Your balance is insufficient for this transaction.");
        }

        if (senderAccountNumber.equals(receiverAccountNumber)) {
            throw new InvalidBankAccountDataException("Cannot transfer to the same account.");
        }

        try {
            logger.info("Transferring money.");
            bankAccountRepository.transferMoney(senderAccountNumber, receiverAccountNumber, amount);
        } catch (BankAccountRepositoryException e) {
            logger.error("Error while transferring money: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Checks if a bank account exists for a given account number.
     *
     * @param accountNumber The bank account number to check.
     * @return true if the account exists, false otherwise.
     * @throws CustomerNotFoundException If no customer is associated with the account.
     */
    public boolean existsByAccountNumber(String accountNumber) throws CustomerNotFoundException {
        Optional<BankAccountDto> optionalBankAccountDto = bankAccountRepository.getBankAccountByAccountNumber(accountNumber);
        return optionalBankAccountDto.isPresent();
    }

    /**
     * Retrieves a bank account based on its account number.
     *
     * @param accountNumber The account number of the bank account.
     * @return The {@link BankAccountDto} associated with the account number.
     * @throws BankAccountNotFoundException If no bank account is found with the given account number.
     * @throws CustomerNotFoundException If no customer is associated with the account.
     */
    public BankAccountDto getBankAccountByAccountNumber(String accountNumber) throws BankAccountNotFoundException, CustomerNotFoundException {
        try {
            validationManager.validate(ValidationType.BANK_ACCOUNT_NUMBER, accountNumber);
            logger.info("Searching for bank account with account number: {}", accountNumber);
            return bankAccountRepository.getBankAccountByAccountNumber(accountNumber)
                    .orElseThrow(() -> new BankAccountNotFoundException("No bank account with account number " + accountNumber + " was found."));
        } catch (CustomerRepositoryException e) {
            logger.error("Error while searching for bank account with account number {}: {}", accountNumber, e.getMessage(), e);
            throw e;
        }
    }
}
