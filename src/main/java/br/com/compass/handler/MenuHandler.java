package br.com.compass.handler;

import br.com.compass.domain.Account;
import br.com.compass.domain.Client;
import br.com.compass.service.AccountService;

import java.util.Scanner;

import static br.com.compass.validation.ClientValidator.validateClient;

public class MenuHandler {
    public static void mainMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("=========== Main Menu ============");
            System.out.println("|| 1. Login                     ||");
            System.out.println("|| 2. Account Opening           ||");
            System.out.println("|| 0. Exit                      ||");
            System.out.println("==================================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    loginMenu(scanner);
                    break;
                case 2:
                    AccountService.openAccount(scanner);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again!");
            }
        }
    }

    public static void loginMenu(Scanner scanner) {
        boolean running = true;

        while (running) {
            System.out.println("========== Login Menu ============");
            System.out.println("|| 1. Already client            ||");
            System.out.println("|| 2. Account Opening           ||");
            System.out.println("|| 0. Exit                      ||");
            System.out.println("==================================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Inform an account number: ");
                    String accountNumber = scanner.next();
                    Account account = AccountService.findAccount(accountNumber);

                    if (account == null) {
                        System.out.println("Account not found!");
                    } else {
                        bankMenu(scanner, account);
                        running = false;
                    }
                    break;
                case 2:
                    AccountService.openAccount(scanner);
                    running = false;
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again!");
            }
        }
    }

    public static void bankMenu(Scanner scanner, Account account) {
        boolean running = true;

        while (running) {
            System.out.println("========= Bank Menu =========");
            System.out.println("|| 1. Deposit              ||");
            System.out.println("|| 2. Withdraw             ||");
            System.out.println("|| 3. Check Balance        ||");
            System.out.println("|| 4. Transfer             ||");
            System.out.println("|| 5. Bank Statement       ||");
            System.out.println("|| 0. Exit                 ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    AccountService.deposit(scanner, account);
                    break;
                case 2:
                    AccountService.withdraw(scanner, account);
                    break;
                case 3:
                    System.out.println("Current balance: " + account.getBalance());
                    break;
                case 4:
                    AccountService.transfer(scanner, account);
                    break;
                case 5:
                    AccountService.printBankStatement(account);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again!");
            }
        }
    }

    public static Client clientMenu(Scanner scanner) {
        System.out.println("========= Client Register ==========");
        System.out.println("Name: ");
        String name = scanner.next();
        System.out.println("CPF number: ");
        String cpfNumber = scanner.next();
        System.out.println("Birth date (year-month-day): ");
        String birthDate = scanner.next();
        System.out.println("Phone number (ex: 21999999999): ");
        String phoneNumber = scanner.next();

        Client newClient = new Client(name, cpfNumber, birthDate, phoneNumber);
        if (validateClient(newClient) == null) {
            clientMenu(scanner);
        }
        return newClient;
    }
}

