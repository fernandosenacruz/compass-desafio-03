package br.com.compass.dao;

import br.com.compass.HibernateUtil;
import br.com.compass.domain.Client;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientDAO {

    public void save(Client client) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Client findByCPF(String cpf) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Client where cpf = :cpf", Client.class)
                    .setParameter("cpf", cpf)
                    .getSingleResult();
        }
    }
}
