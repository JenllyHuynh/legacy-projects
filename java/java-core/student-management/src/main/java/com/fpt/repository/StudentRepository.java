package com.fpt.repository;


import com.fpt.entity.Student;
import com.fpt.util.InputUtil;
import com.fpt.util.JpaUtil;
import com.fpt.util.Session;
import jakarta.persistence.EntityManager;

import java.util.List;

public class StudentRepository {

    public Student findByEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.
                    createQuery("select s from Student s where s.email = :email", Student.class)
                    .setParameter("email", email).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean loginStudent() {
        String email = InputUtil.getString("Email: ");
        Student student = findByEmail(email);

        if (student == null) {
            System.out.println("Student not found!");
            return false;
        }

        boolean success = LoginRepository.loginWithRetry(student.getPasswordHash());

        if (success) {
            Session.setLoginStudent(student);
        }

        return success;
    }

    public List<Student> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {

            List<Student> students = em.createQuery("select s from Student s", Student.class)
                    .getResultList();


            for (Student s : students) {
                s.setPasswordHash(null);
            }

            return students;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }





}
