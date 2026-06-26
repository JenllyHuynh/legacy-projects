package com.fpt.util;

import com.fpt.entity.Admin;
import com.fpt.entity.Student;

public class Session {
    private static Admin currentAdmin;

    private static Student currentStudent;

    private Session() {
    }

    // ===== ADMIN =====

    public static void setLoginAdmin(Admin admin) {
        currentAdmin = admin;
    }

    public static Admin getCurrentAdmin() {
        return currentAdmin;
    }

    public static boolean isAdminLoggedIn() {
        return currentAdmin != null;
    }

    public static void logoutAdmin() {
        currentAdmin = null;
    }

    // ===== STUDENT =====

    public static void setLoginStudent(Student student) {
        currentStudent = student;
    }

    public static Student getCurrentStudent() {
        return currentStudent;
    }

    public static boolean isStudentLoggedIn() {
        return currentStudent != null;
    }

    public static void logoutStudent() {
        currentStudent = null;
    }

    // ===== LOGOUT ALL =====

    public static void clear() {
        currentAdmin = null;
        currentStudent = null;
    }
}
