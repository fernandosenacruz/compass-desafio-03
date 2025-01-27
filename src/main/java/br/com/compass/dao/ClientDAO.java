package br.com.compass.dao;

import br.com.compass.util.HibernateUtil;
import br.com.compass.domain.Client;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class ClientDAO {

    public Client findByCPF(String cpf) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session
                    .createQuery("from Client where cpf = :cpf", Client.class)
                    .setParameter("cpf", cpf)
                    .getSingleResultOrNull();
        }
    }

    public Client saveOrUpdate(Client client) {
        Client persistedClient = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            Client dbClient = findByCPF(client.getCpf());
            if (dbClient != null) {
                dbClient.setName(client.getName());
                dbClient.setBirthDate(client.getBirthDate());
                dbClient.setPhoneNumber(client.getPhoneNumber());
                persistedClient = session.merge(dbClient);
            } else {
                session.save(client);
                persistedClient = client;
            }
            transaction.commit();
        }
        return persistedClient;
    }

}
