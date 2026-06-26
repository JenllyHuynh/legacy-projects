package com.fpt.ui;

import com.fpt.repository.CourseRepository;

import java.util.Scanner;

import com.fpt.entity.Course;


public class CreateCourseMenu {

    private final Scanner scanner;
    private final CourseRepository courseRepository;

    public CreateCourseMenu(Scanner scanner) {
        this.scanner = scanner;
        this.courseRepository = new CourseRepository();
    }


    public void show() {

        System.out.println("--- Tao Mon Hoc Moi ---");


        boolean continueCreating = true;

        while (continueCreating) {
            createOneCourse();

            System.out.print("\nBan co muon tao mon hoc moi nua khong ? (y/n): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            continueCreating = answer.equals("y");
        }

        System.out.println("\nQuay ve menu moi");
    }

    private void createOneCourse() {
        System.out.println("\n--- Nhap thong tin moi ---");

        String courseCode = inputCourseCode();
        if (courseCode == null) return;

        String courseName = inputCourseName();
        if (courseName == null) return;

        Integer credits = inputCredits();
        if (credits == null) return;

        System.out.println("\n--- Xac nhan thong tin ---");
        System.out.printf("  Ma mon hoc : %s%n", courseCode);
        System.out.printf("  Ten mon hoc: %s%n", courseName);
        System.out.printf("  So tin chi : %d%n", credits);
        System.out.print("Xac nhan tao mon hoc ? (y/n): ");

        String confirm = scanner.nextLine().trim().toLowerCase();
        if (!confirm.equals("y")) {
            System.out.println("Huy tao mon hoc");
            return;
        }

        // 5. Lưu vào DB
        saveCourse(courseCode, courseName, credits);
    }

    private String inputCourseCode() {
        while (true) {
            System.out.print("Nhap ma mon hoc (toi da 20 ky tu, go 'back' de quay lai): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                System.out.println("Quay lai");
                return null;
            }

            if (input.isEmpty()) {
                System.out.println("  Ma mon hoc khong duoc de trong. Vui long nhap lai.");
                continue;
            }

            if (input.length() > 20) {
                System.out.println("  Ma mon hoc khong duoc vuot qua 20 ky tu. Vui long nhap lai.");
                continue;
            }

            if (courseRepository.existsByCourseCode(input)) {
                System.out.printf("  Ma mon hoc '%s' da ton tai. Vui long nhap ma mon hoc khac.%n", input);
                continue;
            }

            return input;
        }
    }

    private String inputCourseName() {
        while (true) {
            System.out.print("Nhap ten mon hoc (toi da 200 ky tu, go 'back' de quay lai): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                System.out.println("Quay lai");
                return null;
            }

            if (input.isEmpty()) {
                System.out.println("  Ten mon hoc khong duoc de trong. Vui long nhap lai.");
                continue;
            }

            if (input.length() > 200) {
                System.out.println("  Ten mon hoc khong duoc vuot qua 200 ky tu. Vui long nhap lai.");
                continue;
            }

            return input;
        }
    }

    private Integer inputCredits() {
        while (true) {
            System.out.print("Nhap so tin chi 1-10, go 'back' de quay lai: ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("back")) {
                System.out.println("Quay lai");
                return null;
            }

            try {
                int credits = Integer.parseInt(input);

                if (credits < 1 || credits > 10) {
                    System.out.println(" So tin chi tu 1 - 10. Nhap lai");
                    continue;
                }

                return credits;

            } catch (NumberFormatException e) {
                System.out.println("  So tin chi phai la so nguyen. Nhap lai");
            }
        }
    }

    private void saveCourse(String courseCode, String courseName, int credits) {
        try {
            Course newCourse = new Course(courseCode, courseName, credits);
            Course saved = courseRepository.save(newCourse);

            System.out.printf("  Tao mon hoc thanh cong!%n");
            System.out.printf("  ID      : %d%n", saved.getId());
            System.out.printf("  Ma  MN  : %s%n", saved.getCourseCode());
            System.out.printf("  Ten MH  : %s%n", saved.getCourseName());
            System.out.printf("  Tin chi : %d%n", saved.getCredits());

        } catch (RuntimeException e) {
            System.out.println("Khong the luu mon hoc nay");

            if (e.getMessage() != null && e.getMessage().contains("unique")) {
                System.out.println("Ma mon hoc da ton tai trong he thong");
            } else {
                System.out.println("Ly do: " + e.getMessage());
            }
        }
    }
}

