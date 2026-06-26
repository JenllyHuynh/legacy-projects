package com.fpt.util;

import java.util.Scanner;

public class InputUtil {

    private static final Scanner scanner =
            new Scanner(System.in);

    private InputUtil() {
    }

    public static String getString(String message) {

        System.out.print(message);

        return scanner.nextLine().trim();
    }

    public static int getInt(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Integer.parseInt(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid integer. Please try again."
                );
            }
        }
    }

    public static double getDouble(String message) {

        while (true) {

            try {

                System.out.print(message);

                return Double.parseDouble(
                        scanner.nextLine().trim()
                );

            } catch (NumberFormatException e) {

                System.out.println(
                        "Invalid number. Please try again."
                );
            }
        }
    }

    public static boolean getBoolean(String message) {

        while (true) {

            System.out.print(message + " (y/n): ");

            String input =
                    scanner.nextLine()
                            .trim()
                            .toLowerCase();

            if (input.equals("y")
                    || input.equals("yes")) {

                return true;
            }

            if (input.equals("n")
                    || input.equals("no")) {

                return false;
            }

            System.out.println(
                    "Please enter y or n."
            );
        }
    }

    public static void closeScanner() {
        scanner.close();
    }

}
