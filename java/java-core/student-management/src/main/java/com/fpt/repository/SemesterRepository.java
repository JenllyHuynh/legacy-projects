package com.fpt.repository;

import com.fpt.entity.Semester;
import com.fpt.util.JpaUtil;
import jakarta.persistence.EntityManager;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.fpt.entity.Semester;
import com.fpt.util.JpaUtil;

import jakarta.persistence.EntityManager;

public class SemesterRepository {

    public Semester findByCode(String semesterCode) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery(
                            "select s from Semester s " +
                                    "where upper(s.semesterCode) = upper(:semesterCode)",
                            Semester.class
                    )
                    .setParameter("semesterCode", semesterCode)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
    public Map<Integer, String> fetchAllDataForUI() {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery("SELECT s from Semester s", Semester.class)
                    .getResultList()
                    .stream()
                    .collect(Collectors.toMap(Semester::getId, Semester::getSemesterCode));

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        } finally {
            em.close();
        }
    }

    public Semester findActiveSemester() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select s from Semester s where s.isActive = true",
                            Semester.class
                    )
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
