package com.fpt.repository;

import com.fpt.entity.Lecturer;
import com.fpt.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fpt.entity.Lecturer;
import com.fpt.util.JpaUtil;

import jakarta.persistence.EntityManager;

public class LecturerRepository {
    public List<Lecturer> showListLecturer() {
        EntityManager em = JpaUtil.getEntityManager();

        try {

            return em.createQuery("select l from Lecturer l", Lecturer.class)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public Map<Integer, String> fetchAllDataForUI() {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery("SELECT l from Lecturer l", Lecturer.class)
                    .getResultList()
                    .stream()
                    .collect(Collectors.toMap(Lecturer::getId, l -> l.getFullName() + '-' + l.getLecturerCode()));

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        } finally {
            em.close();
        }
    }

    public Lecturer findByCode(String lecturerCode) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select l from Lecturer l " +
                                    "where upper(l.lecturerCode) = upper(:code)",
                            Lecturer.class
                    )
                    .setParameter("code", lecturerCode)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}



