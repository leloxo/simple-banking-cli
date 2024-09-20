package com.github.leloxo.bankclient.cli;

import com.github.leloxo.bankclient.model.customer.CustomerDto;
import com.github.leloxo.bankclient.service.CustomerService;
import com.github.leloxo.bankclient.utils.TerminalColors;
import com.github.leloxo.bankclient.utils.TerminalUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Scanner;

@Component
public class CustomerMenu {
    private final BankAccountMenu bankAccountMenu;
    private final LoginMenu loginMenu;
    private final CustomerService customerService;

    public CustomerMenu(LoginMenu loginMenu, BankAccountMenu bankAccountMenu, CustomerService customerService) {
        this.loginMenu = loginMenu;
        this.bankAccountMenu = bankAccountMenu;
        this.customerService = customerService;
    }

    public boolean displayCustomerMenu(Scanner scanner) {
        CustomerDto loggedInCustomer = loginMenu.getLoggedInCustomer();

        displayHomeScreen(loggedInCustomer.getFirstName(), loggedInCustomer.getLastName());

        boolean isLoggedIn = true;
        while (isLoggedIn) {
            String userName = loggedInCustomer.getFirstName() + loggedInCustomer.getLastName();
            System.out.print(TerminalUtils.colorize(userName.toLowerCase(), TerminalColors.MAGENTA) +
                    TerminalUtils.colorize("> ", TerminalColors.CYAN));

            String command = scanner.nextLine();
            switch (command.toLowerCase()) {
                case "help":
                    displayAvailableCommands();
                    break;
                case "home":
                    displayHomeScreen(loggedInCustomer.getFirstName(), loggedInCustomer.getLastName());
                    break;
                case "info":
                    bankAccountMenu.displayBankAccountInfo(loggedInCustomer.getEmail());
                    break;
                case "transfer":
                    bankAccountMenu.displayMoneyTransferMenu(scanner, loggedInCustomer.getEmail());
                    break;
                case "edit":
                    loggedInCustomer = editCustomerInfo(scanner, loggedInCustomer);
                    break;
                case "open":
                    bankAccountMenu.displayBankAccountCreationMenu(scanner, loggedInCustomer.getEmail());
                    break;
                case "delete":
                    deleteCustomer(scanner, loggedInCustomer.getEmail());
                    isLoggedIn = logout();
                    break;
                case "logout", "exit":
                    isLoggedIn = logout();
                    break;
                default:
                    System.out.println("Invalid command. Please try again.\n");
                    break;
            }
        }
        return false;
    }

    private void deleteCustomer(Scanner scanner, String email) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Delete Customer Account"));
        System.out.println(TerminalUtils.colorizeError("WARNING!") + " This can not be undone!\n");

        System.out.print("Are you sure you want to delete this customer account with the email (" + email + ")? (y/n): ");
        String response = scanner.nextLine().trim();
        if (response.equalsIgnoreCase("y")) {
            System.out.print("Please confirm your password to delete your account: ");
            String password = scanner.nextLine().trim();

            if (loginMenu.confirmPassword(email, password)) {
                try {
                    customerService.deleteCustomer(email);
                    System.out.println(TerminalUtils.colorizeSuccess("Your customer account has been successfully deleted!\n"));
                } catch (WebClientResponseException e) {
                    System.out.println(TerminalUtils.colorizeError("An error occurred while deleting your customer account: " + e.getResponseBodyAsString() + ". Please try again.\n"));
                } catch (Exception e) {
                    System.out.println(TerminalUtils.colorizeError("An unexpected error occurred while deleting your customer account: " + e.getMessage() + ". Please try again.\n"));
                }
            }
        } else {
            System.out.println("Aborting. Returning to the customer menu...\n");
        }
    }

    private CustomerDto editCustomerInfo(Scanner scanner, CustomerDto loggedInCustomer) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Edit Personal Information"));

        CustomerDto updatedCustomer = new CustomerDto(
                loggedInCustomer.getId(),
                loggedInCustomer.getFirstName(),
                loggedInCustomer.getLastName(),
                loggedInCustomer.getEmail(),
                loggedInCustomer.getCreatedAt()
        );

        System.out.println("Current Information:");
        System.out.println("1. First Name: " + loggedInCustomer.getFirstName());
        System.out.println("2. Last Name: " + loggedInCustomer.getLastName());
        System.out.println("3. Email Address: " + loggedInCustomer.getEmail());

        boolean fieldUpdated = false;
        while (true) {
            System.out.print("\nWhich field would you like to edit? (Enter 0 to exit): ");
            String choice = scanner.nextLine().trim().toLowerCase();
            if (choice.equals("0")) {
                break;
            }
            fieldUpdated = updateCustomerField(choice, scanner, updatedCustomer);
        }
        if (!fieldUpdated) {
            System.out.println("No values have been edited. Returning to the previous menu...\n");
            return loggedInCustomer;
        }

        System.out.println(TerminalUtils.colorize("\nUpdated Information:", TerminalColors.YELLOW));
        System.out.println("First Name: " + updatedCustomer.getFirstName());
        System.out.println("Last Name: " + updatedCustomer.getLastName());
        System.out.println("Email Address: " + updatedCustomer.getEmail());

        System.out.print("\nWould you like to save these changes? (y/n): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("y")) {
            saveCustomerUpdates(loggedInCustomer.getEmail(), updatedCustomer);
            return updatedCustomer;
        } else {
            System.out.println("No changes were saved.\n");
            return loggedInCustomer;
        }
    }

    private boolean updateCustomerField(String choice, Scanner scanner, CustomerDto customer) {
        switch (choice) {
            case "1":
                System.out.print("Enter new First Name: ");
                customer.setFirstName(scanner.nextLine().trim());
                return true;
            case "2":
                System.out.print("Enter new Last Name: ");
                customer.setLastName(scanner.nextLine().trim());
                return true;
            case "3":
                System.out.print("Enter new Email Address: ");
                customer.setEmail(scanner.nextLine().trim());
                return true;
            default:
                return false;
        }
    }

    private void saveCustomerUpdates(String email, CustomerDto updatedCustomer) {
        try {
            customerService.updateCustomer(email, updatedCustomer);
            System.out.println(TerminalUtils.colorizeSuccess("Your information has been successfully updated.\n"));
        } catch (WebClientResponseException e) {
            System.out.println(TerminalUtils.colorizeError("An error occurred while updating your information: " + e.getResponseBodyAsString() + ". Please try again.\n"));
        } catch (Exception e) {
            System.out.println(TerminalUtils.colorizeError("An unexpected error occurred while updating your information: " + e.getMessage() + ". Please try again.\n"));
        }
    }

    private void displayHomeScreen(String firstName, String lastName) {
        System.out.println(TerminalUtils.colorizeTitle("\n > Customer Menu\n"));
        System.out.println(TerminalUtils.colorize("Logged in as: ", TerminalColors.GREEN)
                + firstName + " " + lastName);
        displayAvailableCommands();
    }

    private void displayAvailableCommands() {
        System.out.println("\nAvailable commands:");
        System.out.println("    help            Shows the available commands menu.");
        System.out.println("    home            Shows the home screen.");
        System.out.println("    info            Shows details of all owned bank accounts.");
        System.out.println("    transfer        Opens the money transfer menu.");
        System.out.println("    edit            Edit personal information.");
        System.out.println("    open            Open up a new bank account.");
        System.out.println("    delete          Delete your customer account.");
        System.out.println("    logout          Performs a logout and returns to the login menu.");
        System.out.println("    exit            Performs a logout and returns to the login menu.\n");
    }

    private boolean logout() {
        System.out.println("Logging out...");
        return false;
    }
}
