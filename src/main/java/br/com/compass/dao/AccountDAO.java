package br.com.compass.dao;

import br.com.compass.HibernateUtil;
import br.com.compass.domain.Account;
import br.com.compass.domain.Client;
import br.com.compass.domain.AccountTransaction;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class AccountDAO {

    public void save(Account account) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(account);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            System.err.println("Error saving account: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Account findByNumber(String number) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Account where number = :number", Account.class)
                    .setParameter("number", number)
                    .uniqueResult();
        }
    }

    public boolean hasSameTypeAccount(Client client, AccountType type) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String query = """
                select count(c)
                from Account c
                where c.type = :type
                and c.client.cpf = :cpf
            """;
            Long count = session
                    .createQuery(query, Long.class)
                    .setParameter("type", type)
                    .setParameter("cpf", client.getCpf())
                    .uniqueResult();

            return count != null && count > 0;
        }
    }

    public void deposit(Account account, BigDecimal value) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            account.setBalance(account.getBalance().add(value));

            AccountTransaction accountTransaction = new AccountTransaction(TransactionType.DEPOSIT, value, account);
            AccountTransactionDAO accountTransactionDAO = new AccountTransactionDAO();
            accountTransactionDAO.save(accountTransaction);

            session.merge(account);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public boolean withdraw(Account account, BigDecimal value) {
        if (account.getBalance().compareTo(value) < 0) {
            System.out.println("Insufficient balance!");
            return false;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            account.setBalance(account.getBalance().subtract(value));

            AccountTransaction accountTransaction = new AccountTransaction(TransactionType.WITHDRAWAL, value, account);
            AccountTransactionDAO accountTransactionDAO = new AccountTransactionDAO();
            accountTransactionDAO.save(accountTransaction);

            session.merge(account);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean transfer(Account origin, Account destination, BigDecimal value) {
        if (origin.getBalance().compareTo(value) < 0) {
            System.out.println("Insufficient balance!");
            return false;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            origin.setBalance(origin.getBalance().subtract(value));
            destination.setBalance(destination.getBalance().add(value));

            AccountTransaction accountTransactionOrigin =
                    new AccountTransaction(TransactionType.INCOME_TRANSFER, value, origin);
            AccountTransaction accountTransactionDestination =
                    new AccountTransaction(TransactionType.EXPENSE_TRANSFER, value, destination);

            AccountTransactionDAO accountTransactionDAO = new AccountTransactionDAO();

            accountTransactionDAO.save(accountTransactionOrigin);
            accountTransactionDAO.save(accountTransactionDestination);

            session.merge(origin);
            session.merge(destination);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<AccountTransaction> bankStatement(Account account) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from AccountTransaction where account = :account", AccountTransaction.class)
                    .setParameter("account", account)
                    .list();
        }
    }
}
