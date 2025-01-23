package br.com.compass;

import br.com.compass.dao.AccountDAO;
import br.com.compass.dao.AccountType;
import br.com.compass.domain.Account;
import br.com.compass.domain.Client;
import io.github.cdimascio.dotenv.Dotenv;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class App {

    public static void loadEnv() {
        Dotenv dotenv = Dotenv.load();
        System.setProperty("DB_USERNAME", Objects.requireNonNull(dotenv.get("DB_USERNAME")));
        System.setProperty("DB_PASSWORD", Objects.requireNonNull(dotenv.get("DB_PASSWORD")));
        System.setProperty("DB_URL", Objects.requireNonNull(dotenv.get("DB_URL")));
    }
    
    public static void main(String[] args) {
        loadEnv();
        Scanner scanner = new Scanner(System.in);
        mainMenu(scanner);
        scanner.close();
        System.out.println("Application closed");
    }

    public static void loginMenu(Scanner scanner, boolean running) {
        AccountDAO accountDAO = new AccountDAO();

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
                    Account account = accountDAO.findByNumber(accountNumber);

                    if (account == null) {
                        System.out.println("Account not found!");
                    } else {
                        bankMenu(scanner, account);
                        running = false;
                    }
                    break;
                case 2:
                    openAccount(scanner, accountDAO);
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

    public static void mainMenu(Scanner scanner) {
        AccountDAO accountDAO = new AccountDAO();
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
                    loginMenu(scanner, running);
                    break;
                case 2:
                    openAccount(scanner, accountDAO);
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
        System.out.println("Phone number (code+number): ");
        String phoneNumber = scanner.next();

        Client newClient = new Client(name, cpfNumber, birthDate, phoneNumber);
        if (validateClient(newClient) == null) {
            clientMenu(scanner);
        }
        return newClient;
    }

    public static void openAccount(Scanner scanner, AccountDAO accountDAO) {
        String accountNumber = accountNumberGenerate();

        System.out.println("Select account type:");
        System.out.println("1. Checking Account");
        System.out.println("2. Savings Account");
        System.out.println("3. Payroll Account");
        System.out.println("4. Payments Account");
        System.out.println("5. Corporate Account");

        int option = scanner.nextInt();
        AccountType accountType;

        switch (option) {
            case 1:
                accountType = AccountType.CHECKING;
                break;
            case 2:
                accountType = AccountType.SAVING;
                break;
            case 3:
                accountType = AccountType.PAYROLL;
                break;
            case 4:
                accountType = AccountType.PAYMENT;
                break;
            case 5:
                accountType = AccountType.CORPORATE;
                break;
            default:
                System.out.println("Invalid type!");
                return;
        }

        Client client = clientMenu(scanner);
        Account account = new Account(accountNumber, accountType, client);
        if (accountDAO.hasSameTypeAccount(client, accountType)) {
            System.out.println("An account already exists with the same type to informed CPF!");
            return;
        }
        accountDAO.save(account);
        System.out.println("Account registered successfully!");
        System.out.println("Account number: " + account.getNumber());
    }

    public static void bankMenu(Scanner scanner, Account account) {
        AccountDAO accountDAO = new AccountDAO();
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
                    deposit(scanner, account, accountDAO);
                    break;
                case 2:
                    withdraw(scanner, account, accountDAO);
                    break;
                case 3:
                    System.out.println("Current balance: " + account.getBalance());
                    break;
                case 4:
                    transfer(scanner, account, accountDAO);
                    break;
                case 5:
                    bankStatement(accountDAO, account);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again!");
            }
        }
    }

    public static void deposit(Scanner scanner, Account account, AccountDAO accountDAO) {
        System.out.print("Inform a value to deposit: ");
        BigDecimal value = scanner.nextBigDecimal();

        if (value.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Not possible to transfer negative values");
            return;
        }
        if (value.compareTo(BigDecimal.ZERO) > 0) {
            accountDAO.deposit(account, value);
            System.out.println("Deposit successful!");
        }
    }

    public static void withdraw(Scanner scanner, Account account, AccountDAO accountDAO) {
        System.out.print("Inform a value to withdraw: ");
        BigDecimal value = scanner.nextBigDecimal();
        if (!accountDAO.withdraw(account, value)) return;
        System.out.println("Withdraw successful!");
    }

    public static void transfer(Scanner scanner, Account origin, AccountDAO accountDAO) {
        System.out.print("Inform a account to transfer: ");
        String numberAccount = scanner.next();
        Account accountDestination = accountDAO.findByNumber(numberAccount);

        if (accountDestination == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Inform a value to transfer: ");
        BigDecimal value = scanner.nextBigDecimal();
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Not possible to transfer negative values!");
            return;
        }

        if (!accountDAO.transfer(origin, accountDestination, value)) return;
        System.out.println("Transfer successful!");
    }

    public static void bankStatement(AccountDAO accountDAO, Account account) {
        System.out.println("Bank Statement:");
        accountDAO.bankStatement(account).forEach(accountTransaction ->
                System.out.println(
                        formatDateTime(accountTransaction.getDateTime())
                        + " - " + accountTransaction.getType()
                        + ": " + accountTransaction.getValue())
        );
    }
    
    public static String accountNumberGenerate() {
        StringBuilder accountNumber = new StringBuilder();
        Random r = new Random();
        
        for (int i = 0; i < 6; i++) {
            int num = r.nextInt(10);
            accountNumber.append(num);
        }
        
        return accountNumber.toString();
    }

    public static String formatDateTime(LocalDateTime dataHora) {
        return dataHora.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));
    }

    public static Client validateClient(Client newClient) {
        if (
                newClient.getName().isEmpty()
                || newClient.getCpf().isEmpty()
                || newClient.getBirthDate().isEmpty()
                || newClient.getPhoneNumber().isEmpty()
        ) {
            System.out.println("All fields must be filled!");
            return null;
        }
        if (newClient.getCpf().trim().length() != 11) {
            System.out.println("Invalid CPF!");
            return null;
        }

        String[] data = newClient.getBirthDate().split("-");
        if (data.length != 3) {
            System.out.println("Invalid birth date!.");
            return null;
        }

        return newClient;
    }
}
