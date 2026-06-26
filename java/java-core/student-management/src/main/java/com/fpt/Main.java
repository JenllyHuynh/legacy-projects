package com.fpt;

import com.fpt.entity.Course;
import com.fpt.repository.CourseRepository;
import com.fpt.ui.AdminMenu;
import com.fpt.ui.LoginMenu;
import com.fpt.util.JpaUtil;
// import jakarta.persistence.EntityManager;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        // EntityManager em = JpaUtil.getEntityManager();
        System.out.println(
                ">>>> Connected successfully!");

        try {
            new LoginMenu().showFormLogin();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JpaUtil.shutdown();
            System.out.println("Application closed.");
        }

    }
}