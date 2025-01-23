package br.com.compass.dao;

import br.com.compass.HibernateUtil;
import br.com.compass.domain.AccountTransaction;
import org.hibernate.Session;

public class AccountTransactionDAO {

    public void save(AccountTransaction accountTransaction) {
        org.hibernate.Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(accountTransaction);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
