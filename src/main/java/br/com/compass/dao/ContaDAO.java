package br.com.compass.dao;

import br.com.compass.HibernateUtil;
import br.com.compass.domain.Conta;
import br.com.compass.domain.Transacao;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class ContaDAO {

    public void salvar(Conta conta) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(conta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public Conta buscarPorNumero(String numero) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Conta where numero = :numero", Conta.class)
                    .setParameter("numero", numero)
                    .uniqueResult();
        }
    }

    public void depositar(Conta conta, BigDecimal valor) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            conta.setSaldo(conta.getSaldo().add(valor));

            Transacao transacao = new Transacao(TipoTransacao.DEPOSITO, valor);
            conta.adicionarTransacao(transacao);

            session.update(conta);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public boolean sacar(Conta conta, BigDecimal valor) {
        if (conta.getSaldo().compareTo(valor) < 0) {
            System.out.println("Saldo insuficiente!");
            return false;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            conta.setSaldo(conta.getSaldo().subtract(valor));

            Transacao transacao = new Transacao(TipoTransacao.SAQUE, valor);
            conta.adicionarTransacao(transacao);

            session.update(conta);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public boolean transferir(Conta origem, Conta destino, BigDecimal valor) {
        if (origem.getSaldo().compareTo(valor) < 0) {
            System.out.println("Saldo insuficiente!");
            return false;
        }

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            origem.setSaldo(origem.getSaldo().subtract(valor));
            destino.setSaldo(destino.getSaldo().add(valor));

            Transacao transacaoOrigem = new Transacao(TipoTransacao.TRANSFERENCIA_SAIDA, valor);
            Transacao transacaoDestino = new Transacao(TipoTransacao.TRANSACAO_ENTRADA, valor);

            origem.adicionarTransacao(transacaoOrigem);
            destino.adicionarTransacao(transacaoDestino);

            session.update(origem);
            session.update(destino);

            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }
    }

    public List<Transacao> extrato(Conta conta) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("from Transacao where conta = :conta", Transacao.class)
                    .setParameter("conta", conta)
                    .list();
        }
    }
}
