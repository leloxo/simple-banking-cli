package com.github.leloxo.bankclient.cli;

import com.github.leloxo.bankclient.model.customer.Customer;
import com.github.leloxo.bankclient.model.customer.CustomerDto;
import com.github.leloxo.bankclient.model.customer.LoginRequestPayload;
import com.github.leloxo.bankclient.service.CustomerService;
import com.github.leloxo.bankclient.utils.TerminalUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Scanner;

@Component
public class LoginMenu {
    private final CustomerService customerService;
    private CustomerDto loggedInCustomer;

    public LoginMenu(CustomerService customerService) {
        this.customerService = customerService;
    }

    public boolean displayLoginMenu(Scanner scanner) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Login"));
        System.out.print("Enter email: ");
        String loginEmail = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        LoginRequestPayload loginRequestPayload = new LoginRequestPayload(loginEmail, password);
        try {
            boolean isAuthenticated = customerService.login(loginRequestPayload);
            if (isAuthenticated) {
                loggedInCustomer = customerService.getCustomerByEmail(loginEmail);
                System.out.println(TerminalUtils.colorizeSuccess("Login successful!"));
                return true;
            }
        } catch (Exception e) {
            System.out.println(TerminalUtils.colorizeError("An error occurred during login: " + e.getMessage() + " Please try again.\n"));
        }
        return false;
    }

    public void displayRegistrationMenu(Scanner scanner) {
        System.out.println(TerminalUtils.colorizeTitle("\n> Registration"));
        System.out.print("Enter your first name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter your second name: ");
        String secondName = scanner.nextLine().trim();
        System.out.print("Enter a valid email address: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter a password (at least 8 characters long): ");
        String password = scanner.nextLine().trim();

        Customer customerData = new Customer(firstName, secondName, email, password);
        try {
            customerService.register(customerData);
            System.out.println(TerminalUtils.colorizeSuccess("\nRegistration successful! You can now log in.\n"));
        } catch (WebClientResponseException e) {
            System.out.println(TerminalUtils.colorizeError("An error occurred during registration: " + e.getResponseBodyAsString() + " Please try again.\n"));
        } catch (Exception e) {
            System.out.println(TerminalUtils.colorizeError("An unexpected error occurred during registration: " + e.getMessage() + " Please try again.\n"));
        }
    }

    public boolean confirmPassword(String loginEmail, String password) {
        LoginRequestPayload loginRequestPayload = new LoginRequestPayload(loginEmail, password);
        try {
            boolean isAuthenticated = customerService.login(loginRequestPayload);
            if (isAuthenticated) {
                loggedInCustomer = customerService.getCustomerByEmail(loginEmail);
                System.out.println(TerminalUtils.colorizeSuccess("Password confirmed!\n"));
                return true;
            }
        } catch (Exception e) {
            System.out.println(TerminalUtils.colorizeError("An error occurred during password confirmation: " + e.getMessage() + " Please try again.\n"));
        }
        return false;
    }

    public CustomerDto getLoggedInCustomer() {
        return loggedInCustomer;
    }
}
