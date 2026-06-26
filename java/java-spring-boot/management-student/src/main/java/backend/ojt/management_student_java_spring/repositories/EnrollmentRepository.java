package backend.ojt.management_student_java_spring.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.ojt.management_student_java_spring.domain.dto.res.StudentEnrollmentProjection;
import backend.ojt.management_student_java_spring.domain.entity.Enrollment;
import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import backend.ojt.management_student_java_spring.utils.constains.EnrollmentStatus;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>, JpaSpecificationExecutor<Enrollment> {
        boolean existsByStudentUserIdAndCourseId(Long studentId, Long courseId);

        /**
         * sum credits on semester
         * coalesce return 0 instead of null value
         * 
         * @param studentId
         * @param semester
         * @param year
         * @param enrollStatus
         * @return
         */
        @Query("""
                            select coalesce(sum(c.credits), 0)
                            from Enrollment e
                                join e.course c
                                join e.student s
                            where s.userId = :studentId
                                and c.semester = :semester
                                and c.year = :year
                                and e.status = :enrollStatus
                        """)
        Integer totalCredits(
                        @Param("studentId") Long studentId,
                        @Param("semester") CourseSemester semester,
                        @Param("year") Integer year,
                        @Param("enrollStatus") EnrollmentStatus enrollStatus);

        /**
         * get student enrollment with pagination
         * 
         * @param courseId
         * @param status
         * @param pageable
         * @return
         **/
        @Query(value = """
                        select u.id as studentId, u.name as studentName, u.email as studentEmail,
                                s.studentCode as studentCode, e.status as status,
                                        u.gender as gender, e.createdAt as enrollAt
                        from Enrollment e
                        join e.student s
                        join s.user u
                        where e.course.id = :courseId and e.status = :status
                        """)
        Page<StudentEnrollmentProjection> findStudentEnrollmentByCourseId(@Param("courseId") Long courseId,
                        @Param("status") EnrollmentStatus status, Pageable pageable);

        /**
         * exists student enroll course
         * 
         * @param studentId
         * @return
         **/
        @Query(value = "select exists(select 1 from Enrollment e join e.student s where s.userId = :studentId)")
        boolean existsByStudentId(Long studentId);
}
