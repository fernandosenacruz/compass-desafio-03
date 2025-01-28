package br.com.compass.service;

import br.com.compass.dao.AccountDAO;
import br.com.compass.dao.AccountType;
import br.com.compass.dao.ClientDAO;
import br.com.compass.domain.Account;
import br.com.compass.domain.Client;
import br.com.compass.util.AccountNumberGeneratorUtil;
import br.com.compass.util.DateTimeFormatterUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

import static br.com.compass.handler.MenuHandler.clientMenu;

public class AccountService {
    private static final AccountDAO accountDAO = new AccountDAO();
    private static final ClientDAO clientDAO = new ClientDAO();

    public static void openAccount(Scanner scanner) {
        String accountNumber = AccountNumberGeneratorUtil.generate();

        System.out.println("Select account type:");
        System.out.println("1. Checking Account");
        System.out.println("2. Savings Account");
        System.out.println("3. Payroll Account");
        System.out.println("4. Payments Account");
        System.out.println("5. Corporate Account");

        if (scanner.hasNextInt()) {
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
            client = clientDAO.saveOrUpdate(client);
            Account account = new Account(accountNumber, accountType, client);

            if (accountDAO.hasSameTypeAccount(client, accountType)) {
                System.out.println("An account already exists with the same type for the informed CPF!");
                return;
            }

            accountDAO.save(account);

            System.out.println("Account registered successfully!");
            System.out.println("Account number: " + account.getNumber());
        } else {
            System.out.println("Invalid input! Please enter a number.");
            scanner.nextLine();
            openAccount(scanner);
        }
    }

    public static Account findAccount(String accountNumber) {
        return accountDAO.findByNumber(accountNumber);
    }

    public static void deposit(Scanner scanner, Account account) {
        System.out.print("Inform a value to deposit: ");
        BigDecimal value = scanner.hasNextBigDecimal()
                ? scanner.nextBigDecimal()
                : new BigDecimal(0);

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid value!");
            return;
        }

        accountDAO.deposit(account, value);
        System.out.println("Deposit successful!");
    }

    public static void withdraw(Scanner scanner, Account account) {
        System.out.print("Inform a value to withdraw: ");
        BigDecimal value = scanner.hasNextBigDecimal()
                ? scanner.nextBigDecimal()
                : new BigDecimal(0);

        if (value.compareTo(BigDecimal.ZERO) <= 0) return;
        if (accountDAO.withdraw(account, value.abs())) {
            System.out.println("Withdraw successful!");
        }
    }

    public static void transfer(Scanner scanner, Account origin) {
        System.out.print("Inform an account number to transfer: ");
        String destinationNumber = scanner.next();
        Account destination = accountDAO.findByNumber(destinationNumber);

        if (destination == null) {
            System.out.println("Account not found!");
            return;
        }

        if (destination.getNumber().equals(origin.getNumber())) {
            System.out.println("You can't transfer to the same account!");
            return;
        }

        if (origin.getType() == AccountType.SAVING || origin.getType() == AccountType.PAYROLL) {
            List<Account> accountList = accountDAO.accountsByClient(origin.getClient().getCpf());

            boolean mayTransfer = accountList.stream()
                    .anyMatch(account -> account.getNumber().equals(destination.getNumber()));

            if (!mayTransfer) {
                System.out.println("You can only transfer to another account of the same CPF!");
                return;
            }
        }

        System.out.print("Inform a value to transfer: ");
        BigDecimal value = scanner.hasNextBigDecimal()
                ? scanner.nextBigDecimal()
                : new BigDecimal(0);

        if (value.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Invalid value!");
            return;
        }

        if (accountDAO.transfer(origin, destination, value)) {
            System.out.println("Transfer successful!");
        }
    }

    public static void printBankStatement(Account account) {
        accountDAO.bankStatement(account).forEach(transaction ->
                System.out.println(DateTimeFormatterUtil.format(transaction.getDateTime())
                        + " - " + transaction.getType()
                        + ": " + transaction.getValue()));
    }
}

