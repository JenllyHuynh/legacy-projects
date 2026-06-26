package com.fpt.ui;

import com.fpt.entity.Student;
import com.fpt.util.InputUtil;
import com.fpt.util.Session;

public class StudentMenu {

    private final EnrollmentMenu enrollmentMenu =
            new EnrollmentMenu();

    public void studentMenu() {

        while (true) {
            Student student =
                    Session.getCurrentStudent();
            if (student == null) {
                System.out.println("Session expired. Please login again.");
                return;
            }
            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("Welcome, " + student.getFullName());
            System.out.println("1. View Schedule");
            System.out.println("2. Register Course");
            System.out.println("0. Logout");
            int choice =
                    InputUtil.getInt("Select option: ");
            switch (choice) {
                case 1:
//                    System.out.println("View schedule feature is not implemented yet.");
                    showMenu();
                    break;
                case 2:
                    enrollmentMenu.show();
                    break;
                case 0:
                    Session.logoutStudent();
                    System.out.println("Logged out successfully.");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void showMenu() {
        System.out.println("Hello");
        new ListCourseMenu().show(Session.getCurrentStudent());

    }
}