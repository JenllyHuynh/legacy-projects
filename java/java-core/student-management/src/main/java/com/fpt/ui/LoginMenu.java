package com.fpt.ui;

import com.fpt.repository.AdminRepository;
import com.fpt.repository.StudentRepository;
import com.fpt.util.InputUtil;
import com.fpt.util.Session;

public class LoginMenu {

    public void showFormLogin() {
        while (true) {
            System.out.println("\n===== LOGIN SYSTEM =====");
            System.out.println("1. Admin");
            System.out.println("2. Student");
            System.out.println("0. Exit");

            int login = InputUtil.getInt("Select Role to login: ");

            switch (login) {
                case 1:
                    AdminRepository adminRepository = new AdminRepository();
                    if (!adminRepository.loginAdmin()) {
                        continue;
                    }
                    System.out.println("============Hello " + Session.getCurrentAdmin().getUsername()+ " ============");
                    new AdminMenu().showMenu();
                    break;
                case 2:
                    StudentRepository studentRepository = new StudentRepository();
                    if (!studentRepository.loginStudent()) {
                        continue;
                    }
                    System.out.println("============ Hello " + Session.getCurrentStudent().getFullName() + " ============");
                    System.out.println("Student menu is not implemented in this task.");
                    new StudentMenu().studentMenu();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
