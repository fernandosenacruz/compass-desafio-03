package br.com.compass.util;

import java.util.Random;

public class AccountNumberGeneratorUtil {
    private static final Random random = new Random();

    public static String generate() {
        return String.format("%07d-%01d", random.nextInt(1000000), random.nextInt(10));
    }
}
