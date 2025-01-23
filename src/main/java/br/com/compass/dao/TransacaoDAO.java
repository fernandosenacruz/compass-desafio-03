package br.com.compass.dao;

import br.com.compass.HibernateUtil;
import br.com.compass.domain.Transacao;
import org.hibernate.Session;
import org.hibernate.Transaction;

public class TransacaoDAO {

    public void salvar(Transacao transacao) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.persist(transacao);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
}
