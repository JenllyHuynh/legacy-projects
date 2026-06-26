package com.fpt.repository;

import com.fpt.entity.ClassSection;
import com.fpt.entity.Schedule;
import com.fpt.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

import com.fpt.dto.ClassSectionDTO;
import com.fpt.entity.ClassSection;
import com.fpt.entity.Course;
import com.fpt.entity.Lecturer;
import com.fpt.entity.Semester;
import com.fpt.util.JpaUtil;

import jakarta.persistence.EntityManager;

/**
 * Feat: create class section
 *
 * @author Vo Anh Ben - CE190709
 */
public class ClassSectionRepository {


    public List<ClassSection> findByCourseCode(String courseCode) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select cs from ClassSection cs " +
                                    "join fetch cs.course c " +
                                    "join fetch cs.lecturer l " +
                                    "where upper(c.courseCode) = upper(:courseCode)",
                            ClassSection.class
                    )
                    .setParameter("courseCode", courseCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }


    public ClassSection findByClassCodeOrId(String input) {
        EntityManager em = JpaUtil.getEntityManager();
        try {

            try {
                Integer id = Integer.parseInt(input);
                ClassSection cs = em.find(ClassSection.class, id);
                if (cs != null) return cs;
            } catch (NumberFormatException ignored) {

            }


            return em.createQuery(
                            "select cs from ClassSection cs " +
                                    "join fetch cs.course c " +
                                    "join fetch cs.lecturer l " +
                                    "where upper(cs.classCode) = upper(:classCode)",
                            ClassSection.class
                    )
                    .setParameter("classCode", input)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }


    public void update(ClassSection classSection) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(classSection);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }


    public void delete(Integer id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ClassSection cs = em.find(ClassSection.class, id);
            if (cs != null) {
                em.remove(cs);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public List<ClassSection> findBySemesterAndCourseCode(
            String semesterCode,
            String courseCode
    ) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery(
                            "select cs from ClassSection cs " +
                                    "join fetch cs.course c " +
                                    "join fetch cs.semester s " +
                                    "join fetch cs.lecturer l " +
                                    "where upper(s.semesterCode) = upper(:semesterCode) " +
                                    "and upper(c.courseCode) = upper(:courseCode) " +
                                    "order by cs.classCode",
                            ClassSection.class
                    )
                    .setParameter("semesterCode", semesterCode)
                    .setParameter("courseCode", courseCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public ClassSection findBySemesterCourseAndClassCode(
            String semesterCode,
            String courseCode,
            String classCode
    ) {
        EntityManager em = JpaUtil.getEntityManager();

        try {
            return em.createQuery(
                            "select cs from ClassSection cs " +
                                    "join fetch cs.course c " +
                                    "join fetch cs.semester s " +
                                    "join fetch cs.lecturer l " +
                                    "where upper(s.semesterCode) = upper(:semesterCode) " +
                                    "and upper(c.courseCode) = upper(:courseCode) " +
                                    "and upper(cs.classCode) = upper(:classCode)",
                            ClassSection.class
                    )
                    .setParameter("semesterCode", semesterCode)
                    .setParameter("courseCode", courseCode)
                    .setParameter("classCode", classCode)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);
        } finally {
            em.close();
        }
    }

    public void create(ClassSectionDTO dto) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            // handle relationship
            var course = em.find(Course.class, dto.getCourseId());
            if (course == null) {
                System.out.println("Course ID not found!");
                return;
            }
            var semester = em.find(Semester.class, dto.getSemesterId());
            if (semester == null) {
                System.out.println("Semester ID not found!");
                return;
            }
            var lecturer = em.find(Lecturer.class, dto.getLecturerId());
            if (lecturer == null) {
                System.out.println("Lecturer ID not found!");
                return;
            }
            // core (save class section to database)
            em.getTransaction().begin();
            var dataSave = new ClassSection();
            dataSave.setClassCode(dto.getClassCode());
            dataSave.setCourse(course);
            dataSave.setSemester(semester);
            dataSave.setLecturer(lecturer);
            dataSave.setMaxStudents(dto.getMaxStudents());
            dataSave.setRoom(dto.getRoom());
            dataSave.setCurrentEnrolled(dto.getCurrentEnrolled());
            dataSave.setVersion(dto.getVersion());
            em.persist(dataSave);
            em.getTransaction().commit();
            System.out.println(">>> Create new ClassSection successfully!");

        } catch (Exception ex) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            ex.printStackTrace();
        } finally {
            em.close();
        }

    }

    public List<ClassSection> findAvailableBySemesterAndCourseCode(
            String semesterCode,
            String courseCode
    ) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select cs from ClassSection cs " +
                                    "join fetch cs.course c " +
                                    "join fetch cs.semester s " +
                                    "join fetch cs.lecturer l " +
                                    "where upper(s.semesterCode) = upper(:semesterCode) " +
                                    "and upper(c.courseCode) = upper(:courseCode) " +
                                    "and cs.currentEnrolled < cs.maxStudents " +
                                    "order by cs.classCode",
                            ClassSection.class
                    )
                    .setParameter("semesterCode", semesterCode)
                    .setParameter("courseCode", courseCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Schedule> findSchedulesByClassSectionId(int classSectionId) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select sc from Schedule sc " +
                                    "where sc.classSection.id = :csId",
                            Schedule.class
                    )
                    .setParameter("csId", classSectionId)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<com.fpt.entity.Course> findAvailableCoursesBySemester(String semesterCode) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "select distinct c from ClassSection cs " +
                                    "join cs.course c " +
                                    "join cs.semester s " +
                                    "where upper(s.semesterCode) = upper(:semesterCode) " +
                                    "and cs.currentEnrolled < cs.maxStudents " +
                                    "order by c.courseCode",
                            com.fpt.entity.Course.class
                    )
                    .setParameter("semesterCode", semesterCode)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
