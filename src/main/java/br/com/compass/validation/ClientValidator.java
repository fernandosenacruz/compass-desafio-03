package br.com.compass.validation;

import br.com.compass.domain.Client;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ClientValidator {
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

        String name = validateName(newClient.getName());
        if (name == null) return null;

        String cpf = validateCPF(newClient.getCpf());
        if (cpf == null) return null;

        String birthDate = validateBirthDate(newClient.getBirthDate());
        if (birthDate == null) return null;

        String phoneNumber = validatePhoneNumber(newClient.getPhoneNumber());
        if (phoneNumber == null) return null;

        newClient.setName(name);
        newClient.setCpf(cpf);
        newClient.setBirthDate(birthDate);
        newClient.setPhoneNumber(phoneNumber);

        return newClient;
    }

    private static String validateName(String name) {
        name = name.replaceAll("\\d", "").trim();
        if (name.length() < 2 || name.length() > 200) {
            System.out.println("Name must be between 2 and 200 characters!");
            return null;
        }
        return name;
    }

    private static String validateCPF(String cpf) {
        cpf = cpf.replaceAll("\\D", "");
        if (cpf.length() != 11) {
            System.out.println("Invalid CPF! It must have exactly 11 digits.");
            return null;
        }
        return cpf;
    }

    private static String validateBirthDate(String birthDate) {
        try {
            LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return birthDate;
        } catch (DateTimeParseException e) {
            System.out.println("Invalid birth date! Use the format yyyy-MM-dd.");
            return null;
        }
    }

    private static String validatePhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.replaceAll("\\D", "");
        if (!phoneNumber.matches("^(\\d{10}|\\d{11})$")) {
            System.out.println("Invalid phone number! It must have 10 or 11 digits.");
            return null;
        }
        return phoneNumber;
    }
}
