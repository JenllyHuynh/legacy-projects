package com.fpt.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {
    private static final EntityManagerFactory emf;

    static {

        try {
            emf = Persistence.createEntityManagerFactory("default");
        } catch (Exception e) {
            System.err.println(
                    "Failed to create EntityManagerFactory"
            );

            throw new RuntimeException(e);
        }

    }

    private JpaUtil() {
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public static void shutdown() {

        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
