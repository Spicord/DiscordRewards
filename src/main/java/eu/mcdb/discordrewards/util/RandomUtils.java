package eu.mcdb.discordrewards.util;

import java.security.SecureRandom;

public class RandomUtils {

    private final static SecureRandom random = new SecureRandom();

    public static String randomString(int length, String seed) {
        if (length < 1) throw new IllegalArgumentException();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            int index = random.nextInt(seed.length());
            char rand = seed.charAt(index);

            sb.append(rand);
        }

        return sb.toString();
    }
}
