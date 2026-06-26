package com.fpt.repository;

import com.fpt.entity.Course;
import com.fpt.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseRepository {

    public List<Course> findAll() {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery("SELECT c from Course c", Course.class).getResultList();
        } finally {
            em.close();
        }
    }


    public List<Course> findByCourseName(String courseName) {
        EntityManager em = JpaUtil.getEntityManager();
        try {

            String jpql = "select c from Course c where c.courseName = :courseName";

            return em.createQuery(jpql, Course.class)
                    .setParameter("courseName", courseName)
                    .getResultList();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    public List<Course> findBySemesterCode(String semesterCode) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery(
                            "select distinct c from ClassSection cs " +
                                    "join cs.course c " +
                                    "join cs.semester s " +
                                    "where upper(s.semesterCode) = upper(:semesterCode) " +
                                    "order by c.courseCode",
                            Course.class
                    )
                    .setParameter("semesterCode", semesterCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Course findById(Long id) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.find(Course.class, id);
        } finally {
            em.close();
        }
    }

    public Course save(Course course) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(course);
            tx.commit();
            return course;
        } catch (Exception e) {
            tx.rollback();
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Lỗi khi lưu môn học: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Course course = em.find(Course.class, id);

            if (course != null) {
                em.remove(course);
            }

            tx.commit();

        } catch (Exception e) {
            tx.rollback();
            throw e;

        } finally {
            em.close();
        }
    }

    public Map<Integer, String> fetchAllDataForUI() {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery("SELECT c from Course c", Course.class)
                    .getResultList()
                    .stream()
                    .collect(Collectors.toMap(Course::getId, c -> c.getCourseName() + '-' + c.getCourseCode()));

        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        } finally {
            em.close();
        }
    }

    public boolean existsByCourseCode(String courseCode) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            Long count = em.createQuery(
                            "select count(c) from Course c" +
                                    " where upper(c.courseCode) = upper(:code) ", Long.class
                    )
                    .setParameter("code", courseCode)
                    .getSingleResult();

            return count > 0;
        } finally {
            em.close();
        }
    }

    public Course findByCourseCode(String courseCode) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery(
                            "select c from Course c" +
                                    " where upper(c.courseCode) = upper(:code)", Course.class
                    )
                    .setParameter("code", courseCode)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }
}
