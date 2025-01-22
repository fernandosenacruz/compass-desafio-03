package br.com.compass;

import br.com.compass.dao.ContaDAO;
import br.com.compass.dao.TipoConta;
import br.com.compass.domain.Conta;

import java.math.BigDecimal;
import java.util.Random;
import java.util.Scanner;

public class App {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        mainMenu(scanner);
        scanner.close();
        System.out.println("Aplicação fechada!");
    }

    public static void mainMenu(Scanner scanner) {
        ContaDAO contaDAO = new ContaDAO();
        boolean running = true;

        while (running) {
            System.out.println("========= Menu Principal =========");
            System.out.println("|| 1. Login                     ||");
            System.out.println("|| 2. Abrir Conta               ||");
            System.out.println("|| 0. Sair                      ||");
            System.out.println("==================================");
            System.out.print("Opção: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Informe o número da sua conta: ");
                    String numero = scanner.next();
                    Conta conta = contaDAO.buscarPorNumero(numero);

                    if (conta == null) {
                        System.out.println("Conta não localizada!");
                    } else {
                        bankMenu(scanner, conta);
                    }
                    break;
                case 2:
                    abrirConta(scanner, contaDAO);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Opção inválida! Por favor tente novamente.");
            }
        }
    }

    public static void abrirConta(Scanner scanner, ContaDAO contaDAO) {
        String numero = gerarNumeroConta();

        System.out.println("Select account type:");
        System.out.println("1. Corrente");
        System.out.println("2. Poupança");
        System.out.println("3. Salário");
        System.out.println("4. Pagamentos");
        System.out.println("5. Empresarial");

        int tipoOption = scanner.nextInt();
        TipoConta tipoConta;

        switch (tipoOption) {
            case 1:
                tipoConta = TipoConta.CORRENTE;
                break;
            case 2:
                tipoConta = TipoConta.POUPANCA;
                break;
            case 3:
                tipoConta = TipoConta.SALARIO;
                break;
            case 4:
                tipoConta = TipoConta.PAGAMENTOS;
                break;
            case 5:
                tipoConta = TipoConta.EMPRESARIAL;
                break;
            default:
                System.out.println("Tipo de conta inválido!");
                return;
        }

        Conta conta = new Conta(numero, tipoConta);
        contaDAO.salvar(conta);
        System.out.println("Conta criada com sucesso!");
    }

    public static void bankMenu(Scanner scanner, Conta conta) {
        ContaDAO contaDAO = new ContaDAO();
        boolean running = true;

        while (running) {
            System.out.println("======== Menu da Conta =======");
            System.out.println("|| 1. Depósito              ||");
            System.out.println("|| 2. Saque                 ||");
            System.out.println("|| 3. Saldo                 ||");
            System.out.println("|| 4. Transferência         ||");
            System.out.println("|| 5. Extrato Bancário      ||");
            System.out.println("|| 0. Sair                  ||");
            System.out.println("=============================");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();

            switch (option) {
                case 1:
                    depositar(scanner, conta, contaDAO);
                    break;
                case 2:
                    sacar(scanner, conta, contaDAO);
                    break;
                case 3:
                    System.out.println("Saldo atual: " + conta.getSaldo());
                    break;
                case 4:
                    transferir(scanner, conta, contaDAO);
                    break;
                case 5:
                    mostrarExtrato(contaDAO, conta);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }
        }
    }

    public static void depositar(Scanner scanner, Conta conta, ContaDAO contaDAO) {
        System.out.print("Informe o valor a ser depositado: ");
        BigDecimal valor = scanner.nextBigDecimal();
        if (!valor.equals(BigDecimal.ZERO)) {
            contaDAO.depositar(conta, valor);
            System.out.println("Depósito realizado com sucesso!");
        }
    }

    public static void sacar(Scanner scanner, Conta conta, ContaDAO contaDAO) {
        System.out.print("Informe o valor a ser sacado: ");
        BigDecimal valor = scanner.nextBigDecimal();
        if (!contaDAO.sacar(conta, valor)) {
            System.out.println("Saldo insuficiente!");
        }
        System.out.println("Saque realizado com sucesso!");
    }

    public static void transferir(Scanner scanner, Conta origem, ContaDAO contaDAO) {
        System.out.print("Informe o número da conta de destino: ");
        String numeroDestino = scanner.next();
        Conta destino = contaDAO.buscarPorNumero(numeroDestino);

        if (destino == null) {
            System.out.println("Conta não encontrada!");
            return;
        }

        System.out.print("Informe o valor a ser transferido: ");
        BigDecimal valor = scanner.nextBigDecimal();

        if (!contaDAO.transferir(origem, destino, valor)) {
            System.out.println("Saldo insuficiente!");
        }
        System.out.println("Transferência realizada com sucesso!");
    }

    public static void mostrarExtrato(ContaDAO contaDAO, Conta conta) {
        System.out.println("Extrato bancário:");
        contaDAO.extrato(conta).forEach(transacao ->
                System.out.println(transacao.getDataHora() + " - " + transacao.getTipo() + ": " + transacao.getValor()));
    }
    
    public static String gerarNumeroConta() {
        StringBuilder numeroConta = new StringBuilder();
        Random r = new Random();
        
        for (int i = 0; i < 6; i++) {
            int num = r.nextInt(10);
            numeroConta.append(num);
        }
        
        return numeroConta.toString();
    }
}
