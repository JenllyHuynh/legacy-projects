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

import backend.ojt.management_student_java_spring.domain.dto.res.StudentProjection;
import backend.ojt.management_student_java_spring.domain.entity.Student;
import backend.ojt.management_student_java_spring.utils.constains.StudentMajor;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
        /**
         * update major student
         * 
         * @param studentId
         * @param major
         * @return
         **/
        @Modifying
        @Query(value = "update Student s set s.major = :major where s.id = :studentId and s.user.status = :status")
        int updateMajorStudentById(@Param("studentId") Long studentId, @Param("major") StudentMajor major,
                        @Param("status") UserStatus status);

        /**
         * get student and user
         * 
         * @param studentId
         * @return
         **/
        @EntityGraph(attributePaths = { "user" })
        @Query(value = "select s from Student s where s.id = :studentId")
        Optional<Student> findWithDetailById(@Param("studentId") Long studentId);

        /**
         * fetch student with interface
         * 
         * @param studentId
         * @return
         **/
        @Query(value = """
                                                    select s.userId as studentId, u.name as studentName,
                                                            s.studentCode as StudentCode, s.major as studentMajor,
                                                            u.gender as studentGender, u.email as studentEmail,
                                                                    s.countCredits as studentCredits
                                    from Student s join s.user u where s.userId = :studentId
                        """)
        Optional<StudentProjection> fetchStudentById(@Param("studentId") Long studentId);

        /**
         * 
         * @param pageable
         * @param q        (name or student code)
         * @return
         **/
        @Query(value = """
                                                    select s.userId as studentId, u.name as studentName,
                                                            s.studentCode as studentCode, s.major as studentMajor,
                                                            u.gender as studentGender, u.email as studentEmail,
                                                                    s.countCredits as studentCredits
                                    from Student s join s.user u where lower(u.email) like concat('%', :q, '%') or lower(s.studentCode) like concat('%', :q, '%')
                        """, countQuery = "select count(s) from Student s join s.user u")
        Page<StudentProjection> fetchAll(Pageable pageable, @Param("q") String q);
}
