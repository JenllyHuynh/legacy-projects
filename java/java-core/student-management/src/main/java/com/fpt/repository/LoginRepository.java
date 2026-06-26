package com.fpt.repository;

import com.fpt.util.InputUtil;
import com.fpt.util.PasswordUtil;

public class LoginRepository {
    private LoginRepository() {
    }

    public static boolean loginWithRetry(
            String correctPassword
    ) {

        int attempts = 0;

        while (attempts < 5) {

            String password =
                    InputUtil.getString("Password: ");
//          Fix this when it login ok. We fix to translate password to hash
//            if (PasswordUtil.verifyPassword(password, correctPassword)) {
            if (password.equals(correctPassword)) {
                return true;
            }

            attempts++;

//            System.out.println(
//                    "Wrong password! Remaining attempts: "
//                            + (5 - attempts)
//            );
        }

        System.out.println(
                "Too many failed attempts!"
        );

        return false;
    }



}
