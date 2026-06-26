package com.fpt.repository;

import com.fpt.entity.Admin;
import com.fpt.util.InputUtil;
import com.fpt.util.JpaUtil;
import com.fpt.util.PasswordUtil;
import com.fpt.util.Session;
import jakarta.persistence.EntityManager;

public class AdminRepository {

    public Admin findByUsername(String username) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery("select a from Admin a where a.username = :username", Admin.class)
                    .setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public boolean loginAdmin() {
        String username = InputUtil.getString("Username: ");
        Admin admin = findByUsername(username);

        if (admin == null) {

            System.out.println("Admin not found!");

            return false;
        }

        boolean success = LoginRepository.loginWithRetry(admin.getPasswordHash());

        if (success) {
            Session.setLoginAdmin(admin);
        }

        return success;

    }
}
