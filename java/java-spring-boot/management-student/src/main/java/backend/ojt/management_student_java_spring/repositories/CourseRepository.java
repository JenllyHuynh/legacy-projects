package backend.ojt.management_student_java_spring.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.ojt.management_student_java_spring.domain.dto.res.CourseProjection;
import backend.ojt.management_student_java_spring.domain.dto.res.LecturerCourseProjection;
import backend.ojt.management_student_java_spring.domain.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
        @Query(value = "select exists(select 1 from Course c join c.lecturer l where l.userId = :lecturerId)")
        boolean existsByLecturerId(@Param("lecturerId") Long lecturerId);

        boolean existsByCourseCodeIgnoreCase(String courseCode);

        @EntityGraph(attributePaths = { "lecturer", "lecturer.user" })
        @Query(value = "select c from Course c where c.id = :courseId")
        Optional<Course> findWithDetailById(@Param("courseId") Long courseId);

        /**
         * get response data with course ID
         * 
         * @param courseId
         * @return
         **/
        @Query(value = """
                        select
                        c.id as courseId,
                        c.name as courseName,
                        c.description as courseDescription,
                        c.courseCode as courseCode,
                        c.credits as courseCredits,
                        c.maxStudents as courseMaxStudents,
                        c.semester as courseSemester,
                        c.year as courseYear,
                        c.currentStudents as courseCurrentStudents,
                        c.enrollStartDate as courseEnrollStartDate,
                        c.enrollEndDate as courseEnrollEndDate,
                                        c.status as courseStatus
                        from Course c
                        where c.id = :courseId

                        """)
        Optional<CourseProjection> findResById(@Param("courseId") Long courseId);

        /**
         * update course student enrollment
         * 
         * @param courseId
         * @param version
         * @return
         **/
        @Modifying
        @Query(value = "update Course c set c.currentStudents = c.currentStudents + 1, c.version = c.version + 1 where c.id = :courseId and c.currentStudents < c.maxStudents and c.version = :version")
        Integer updateSlotStudents(@Param("courseId") Long courseId,
                        @Param("version") Long version);

        /**
         * find data for report lecturer manage course
         * 
         * @param courseId
         * @return
         **/
        @Query(value = """
                        select
                        c.id as courseId,
                        c.name as courseName,
                        c.courseCode as courseCode,
                        c.currentStudents as courseCurrentStudents,
                        c.maxStudents as courseMaxStudents,
                        c.enrollStartDate as courseEnrollStartDate,
                        c.enrollEndDate as courseEnrollEndDate,
                        c.description as courseDescription,
                        c.credits as courseCredits,
                        c.semester as courseSemester,
                        c.year as courseYear,
                        c.status as courseStatus,
                        u.gender as lecturerGender,
                        u.id as lecturerId,
                        u.name as lecturerName,
                        u.email as lecturerEmail,
                        l.lecturerCode as lecturerCode,
                        l.department as lecturerDepartment,
                        l.academicTitle as lecturerAcademicTitle
                        from Course c
                        join c.lecturer l
                        join l.user u
                        where c.id = :courseId
                        """)
        Optional<LecturerCourseProjection> findResLecturerCourseById(@Param("courseId") Long courseId);

        @Query(value = """
                        select
                        c.id as courseId,
                        c.name as courseName,
                        c.description as courseDescription,
                        c.courseCode as courseCode,
                        c.credits as courseCredits,
                        c.maxStudents as courseMaxStudents,
                        c.semester as courseSemester,
                        c.year as courseYear,
                        c.currentStudents as courseCurrentStudents,
                        c.enrollStartDate as courseEnrollStartDate,
                        c.enrollEndDate as courseEnrollEndDate,
                                        c.status as courseStatus
                        from Course c
                        where lower(c.courseCode) like concat('%', :q, '%') or lower(c.name) like concat('%', :q, '%')
                        """, countQuery = "select count(c) from Course c")
        Page<CourseProjection> fetchAll(Pageable pageable, @Param("q") String q);
}
