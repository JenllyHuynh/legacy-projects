package com.fpt.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    private static final int SALT_ROUNDS = 10;

    private PasswordUtil() {
    }

    public static String hashPassword(String rawPassword) {

        return BCrypt.hashpw(
                rawPassword,
                BCrypt.gensalt(SALT_ROUNDS)
        );
    }

    public static boolean verifyPassword(
            String rawPassword,
            String hashedPassword
    ) {

        return BCrypt.checkpw(
                rawPassword,
                hashedPassword
        );
    }
}
