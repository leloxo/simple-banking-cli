package com.github.leloxo.bankclient.cli;

import com.github.leloxo.bankclient.model.bankaccount.BankAccount;
import com.github.leloxo.bankclient.model.bankaccount.BankAccountDto;
import com.github.leloxo.bankclient.model.bankaccount.MoneyTransferRequestPayload;
import com.github.leloxo.bankclient.service.BankAccountService;
import com.github.leloxo.bankclient.utils.TerminalColors;
import com.github.leloxo.bankclient.utils.TerminalUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

@Component
public class BankAccountMenu {
    private final BankAccountService bankAccountService;

    public BankAccountMenu(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    public void displayBankAccountCreationMenu(Scanner scanner, String email) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Bank Account Creation"));

        System.out.print("Would you like to create a new bank account for this email (" + email + ")? (y/n): ");
        String response = scanner.nextLine().trim();
        if (response.equalsIgnoreCase("y")) {
            try {
                BankAccount createdBankAccount = bankAccountService.createBankAccount(email);
                System.out.println(TerminalUtils.colorizeSuccess("\nSuccess! A new bank account has been created."));
                System.out.println("Your new account number: " + createdBankAccount.getAccountNumber() + "\n");
            } catch (WebClientResponseException e) {
                System.out.println(TerminalUtils.colorizeError("An error occurred while creating the bank account: " + e.getResponseBodyAsString() + ". Please try again.\n"));
            } catch (Exception e) {
                System.out.println(TerminalUtils.colorizeError("An unexpected error occurred while creating the bank account: " + e.getMessage() + ". Please try again.\n"));
            }
        } else {
            System.out.println("No bank account was created. Returning to the customer menu...\n");
        }
    }

    public void displayBankAccountInfo(String email) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Bank Account Information"));

        try {
            List<BankAccountDto> bankAccounts = bankAccountService.getBankAccountsByEmail(email);

            if (bankAccounts.isEmpty()) {
                System.out.println("No bank accounts found for the provided email: " + email + "\n");
                return;
            }

            double totalBalance = 0.0;
            System.out.println("Bank Accounts for: " + email + "\n");
            for (BankAccountDto eachBankAccount : bankAccounts) {
                System.out.println("Account Number: " + eachBankAccount.getAccountNumber());
                System.out.println("Balance: " + eachBankAccount.getBalance() + " EUR\n");

                totalBalance += eachBankAccount.getBalance().doubleValue();
            }
            String formattedTotalBalance = String.format("%.2f", totalBalance);
            System.out.println("Total Balance across all accounts: " + formattedTotalBalance + " EUR\n");
        } catch (WebClientResponseException e) {
            System.out.println(TerminalUtils.colorizeError("An error occurred while fetching bank account information: " + e.getResponseBodyAsString() + ". Please try again.\n"));
        } catch (Exception e) {
            System.out.println(TerminalUtils.colorizeError("An unexpected error occurred while fetching bank account information: " + e.getMessage() + ". Please try again.\n"));
        }
    }

    public void displayMoneyTransferMenu(Scanner scanner, String email) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Transfer Money"));

        List<BankAccountDto> bankAccounts = bankAccountService.getBankAccountsByEmail(email);

        if (bankAccounts.isEmpty()) {
            System.out.println("No bank accounts available for money transfer. Returning to the customer menu...\n");
            return;
        }

        String accountNumberFrom;
        if (bankAccounts.size() == 1) {
            accountNumberFrom = bankAccounts.get(0).getAccountNumber();
        } else {
            System.out.print("Enter the account number where the money should be taken from: ");
            accountNumberFrom = scanner.nextLine().trim();
        }

        System.out.print("Enter the account number where the money should be sent to: ");
        String accountNumberTo = scanner.nextLine().trim();
        if (accountNumberFrom.equals(accountNumberTo)) {
            System.out.println(TerminalUtils.colorizeError("Cannot transfer to the same account. Please use a different target account.\n"));
            return;
        }

        double availableBalance = 0.0;
        for (BankAccountDto eachAccount : bankAccounts) {
            if (eachAccount.getAccountNumber().equals(accountNumberFrom)) {
                availableBalance += eachAccount.getBalance().doubleValue();
            }
        }
        System.out.println("\nAvailable balance on bank account (" + accountNumberFrom + "): " + String.format("%.2f", availableBalance) + " EUR");
        System.out.print("Enter the amount to transfer in EUR: ");
        String amountInput = scanner.nextLine().trim();
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountInput);
        } catch (NumberFormatException e) {
            System.out.println(TerminalUtils.colorizeError("'" + amountInput + "' is not a valid number. Please try again.\n"));
            return;
        }

        System.out.println(TerminalUtils.colorize("\nReview transaction:", TerminalColors.YELLOW));
        System.out.println("From bank account: " + accountNumberFrom);
        System.out.println("To bank account: " + accountNumberTo);
        System.out.println("Amount: " + amount + " EUR\n");

        System.out.print("Are you sure you want to make this transaction)? (y/n): ");
        String response = scanner.nextLine().trim();
        if (response.equalsIgnoreCase("y")) {
            MoneyTransferRequestPayload transferRequest = new MoneyTransferRequestPayload(accountNumberFrom, accountNumberTo, amount);
            try {
                boolean isSuccessful = bankAccountService.transferMoney(transferRequest);
                if (isSuccessful) {
                    System.out.println(TerminalUtils.colorizeSuccess("Transfer successful!\n"));
                }
            } catch (Exception e) {
                System.out.println(TerminalUtils.colorizeError("An error occurred during the transfer: " + e.getMessage() + " Please try again.\n"));
            }
        } else {
            System.out.println("Aborting transaction. Returning to the customer menu...\n");
        }
    }
}
