package me.tini.discordrewards.util;

import java.security.SecureRandom;
import java.util.UUID;

public class CodeGenerator {

    private final static SecureRandom RANDOM = new SecureRandom();

    public static String generateCode(int length) {
        if (length < 1) throw new IllegalArgumentException();

        String seed = UUID.randomUUID().toString().replace("-", "").toUpperCase();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {

            int index = RANDOM.nextInt(seed.length());
            char rand = seed.charAt(index);

            sb.append(rand);
        }

        return sb.toString();
    }
}
