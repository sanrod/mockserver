package utils.generator;

import java.security.SecureRandom;

public class RandomGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generateString() {
        StringBuilder sb = new StringBuilder(20);
        char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        for (int i = 0; i < 20; i++) {
            char c = chars[RANDOM.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    public static Boolean getRandomBool() {
        return RANDOM.nextBoolean();
    }

    public static int getRandomNumber() {
        return RANDOM.nextInt(1000);
    }
}
