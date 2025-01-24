package br.com.compass;

import io.github.cdimascio.dotenv.Dotenv;

import java.util.Objects;
import java.util.Scanner;

import static br.com.compass.handler.MenuHandler.mainMenu;

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
}
