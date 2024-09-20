package com.github.leloxo.bankclient.cli;

import com.github.leloxo.bankclient.utils.TerminalColors;
import com.github.leloxo.bankclient.utils.TerminalUtils;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Component
public class CliHandler {
    private final LoginMenu loginMenu;
    private final CustomerMenu customerMenu;

    public CliHandler(LoginMenu loginMenu, CustomerMenu customerMenu) {
        this.loginMenu = loginMenu;
        this.customerMenu = customerMenu;
    }

    public void run() {
        clearScreen();
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        boolean isLoggedIn = false;

        printLogo();
        System.out.println(TerminalUtils.colorize("\nWelcome to Simple Banking CLI", TerminalColors.GREEN));
        displayAvailableCommands();

        while (!exit) {
            System.out.print(TerminalUtils.colorize("home", TerminalColors.MAGENTA) +
                    TerminalUtils.colorize("> ", TerminalColors.CYAN));
            String command = scanner.nextLine().trim();

            if (!isLoggedIn) {
                switch (command.toLowerCase()) {
                    case "help":
                        displayAvailableCommands();
                        break;
                    case "login":
                        isLoggedIn = loginMenu.displayLoginMenu(scanner);
                        break;
                    case "register":
                        loginMenu.displayRegistrationMenu(scanner);
                        break;
                    case "exit":
                        scanner.close();
                        exit = true;
                        System.out.println("Shutting down...");
                        break;
                    default:
                        System.out.println("Invalid command. Please try again.\n");
                        break;
                }
            }

            if (isLoggedIn) {
                isLoggedIn = customerMenu.displayCustomerMenu(scanner);

                System.out.println(TerminalUtils.colorizeTitle("\n > Home"));
                displayAvailableCommands();
            }
        }
    }

    private void displayAvailableCommands() {
        System.out.println("\nAvailable commands:");
        System.out.println("    help            Shows this menu.");
        System.out.println("    login           Opens the login menu.");
        System.out.println("    register        Opens the registration menu.");
        System.out.println("    exit            Exits the application.\n");
    }

    private void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void printLogo() {
        String ascii =
                """
                
                 $$$$$$\\ $$$$$$\\$$\\      $$\\$$$$$$$\\ $$\\      $$$$$$$$\\       $$$$$$$\\  $$$$$$\\ $$\\   $$\\$$\\   $$\\$$$$$$\\$$\\   $$\\ $$$$$$\\         $$$$$$\\ $$\\      $$$$$$\\\s
                $$  __$$\\\\_$$  _$$$\\    $$$ $$  __$$\\$$ |     $$  _____|      $$  __$$\\$$  __$$\\$$$\\  $$ $$ | $$  \\_$$  _$$$\\  $$ $$  __$$\\       $$  __$$\\$$ |     \\_$$  _|
                $$ /  \\__| $$ | $$$$\\  $$$$ $$ |  $$ $$ |     $$ |            $$ |  $$ $$ /  $$ $$$$\\ $$ $$ |$$  /  $$ | $$$$\\ $$ $$ /  \\__|      $$ /  \\__$$ |       $$ | \s
                \\$$$$$$\\   $$ | $$\\$$\\$$ $$ $$$$$$$  $$ |     $$$$$\\          $$$$$$$\\ $$$$$$$$ $$ $$\\$$ $$$$$  /   $$ | $$ $$\\$$ $$ |$$$$\\       $$ |     $$ |       $$ | \s
                 \\____$$\\  $$ | $$ \\$$$  $$ $$  ____/$$ |     $$  __|         $$  __$$\\$$  __$$ $$ \\$$$$ $$  $$<    $$ | $$ \\$$$$ $$ |\\_$$ |      $$ |     $$ |       $$ | \s
                $$\\   $$ | $$ | $$ |\\$  /$$ $$ |     $$ |     $$ |            $$ |  $$ $$ |  $$ $$ |\\$$$ $$ |\\$$\\   $$ | $$ |\\$$$ $$ |  $$ |      $$ |  $$\\$$ |       $$ | \s
                \\$$$$$$  $$$$$$\\$$ | \\_/ $$ $$ |     $$$$$$$$\\$$$$$$$$\\       $$$$$$$  $$ |  $$ $$ | \\$$ $$ | \\$$\\$$$$$$\\$$ | \\$$ \\$$$$$$  |      \\$$$$$$  $$$$$$$$\\$$$$$$\\\s
                 \\______/\\______\\__|     \\__\\__|     \\________\\________|      \\_______/\\__|  \\__\\__|  \\__\\__|  \\__\\______\\__|  \\__|\\______/        \\______/\\________\\______|
                """;
        System.out.println(TerminalUtils.colorize(ascii, TerminalColors.BLUE));
    }
}
