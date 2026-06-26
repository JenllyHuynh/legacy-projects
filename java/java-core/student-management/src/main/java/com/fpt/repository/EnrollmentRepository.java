package com.fpt.repository;

import com.fpt.entity.ClassSection;
import com.fpt.entity.Enrollment;
import com.fpt.entity.Student;
import com.fpt.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class EnrollmentRepository {

    public List<Student> findActiveStudentsByClassSectionId(Integer classSectionId) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery(
                            "select s from Enrollment e " +
                                    "join e.student s " +
                                    "where e.classSection.id = :classSectionId " +
                                    "and lower(e.status) in ('pending', 'confirmed') " +
                                    "order by s.studentCode",
                            Student.class
                    )
                    .setParameter("classSectionId", classSectionId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean existsByStudentAndClassSection(int studentId, int classSectionId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            Long count = em.createQuery(
                            "select count(e) from Enrollment e " +
                                    "where e.student.id = :studentId " +
                                    "and e.classSection.id = :csId " +
                                    "and e.status in ('pending', 'confirmed')",
                            Long.class
                    )
                    .setParameter("studentId", studentId)
                    .setParameter("csId", classSectionId)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public List<Object[]> findScheduleByStudentAndSemester(int studentId, int semesterId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select sc.dayOfWeek, sc.startTime, sc.endTime, cs.classCode " +
                                    "from Schedule sc " +
                                    "join sc.classSection cs " +
                                    "join Enrollment e on e.classSection.id = cs.id " +
                                    "where e.student.id = :studentId " +
                                    "and cs.semester.id = :semesterId " +
                                    "and e.status in ('pending', 'confirmed')",
                            Object[].class
                    )
                    .setParameter("studentId", studentId)
                    .setParameter("semesterId", semesterId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public boolean enroll(int studentId, int classSectionId) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            ClassSection cs = em.find(ClassSection.class, classSectionId);

            if (cs == null || cs.getCurrentEnrolled() >= cs.getMaxStudents()) {
                tx.rollback();
                return false;
            }

            Enrollment enrollment = new Enrollment();
            enrollment.setStudent(em.getReference(Student.class, studentId));
            enrollment.setClassSection(cs);
            enrollment.setStatus("pending");

            cs.setCurrentEnrolled(cs.getCurrentEnrolled() + 1);

            em.persist(enrollment);
            em.merge(cs);

            tx.commit();
            return true;

        } catch (jakarta.persistence.OptimisticLockException |
                 jakarta.persistence.RollbackException e) {
            if (tx.isActive()) tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
    public List<Integer> findEnrolledClassSectionIdsByStudentAndSemester(
            int studentId,
            int semesterId
    ) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select cs.id from Enrollment e " +
                                    "join e.classSection cs " +
                                    "where e.student.id = :studentId " +
                                    "and cs.semester.id = :semesterId " +
                                    "and e.status in ('pending', 'confirmed')",
                            Integer.class
                    )
                    .setParameter("studentId", studentId)
                    .setParameter("semesterId", semesterId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Enrollment> getMyEnrollments(int studentId) {

        EntityManager em = JpaUtil.getEntityManager();

        try {

            return em.createQuery(
                            "SELECT DISTINCT e FROM Enrollment e " +
                                    "JOIN FETCH e.classSection cs " +
                                    "JOIN FETCH cs.course " +
                                    "JOIN FETCH cs.lecturer " +
                                    "LEFT JOIN FETCH cs.schedules " +
                                    "WHERE e.student.id = :studentId " +
                                    "AND e.status != 'cancelled'",
                            Enrollment.class
                    )
                    .setParameter("studentId", studentId)
                    .getResultList();

        } finally {
            em.close();
        }
    }
}
