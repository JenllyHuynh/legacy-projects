package backend.ojt.management_student_java_spring.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.ojt.management_student_java_spring.domain.dto.res.LecturerProjection;
import backend.ojt.management_student_java_spring.domain.entity.Lecturer;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;

@Repository
public interface LecturerRepository extends JpaRepository<Lecturer, Long>, JpaSpecificationExecutor<Lecturer> {
        @EntityGraph(attributePaths = { "user" })
        @Query(value = "select l from Lecturer l where l.id = :lecturerId")
        Optional<Lecturer> findWithDetailById(@Param("lecturerId") Long lecturerId);

        @EntityGraph(attributePaths = { "user", "courses" })
        @Query(value = "select l from Lecturer l where l.id = :lecturerId and l.user.status = :status and l.user.role = :role")
        Optional<Lecturer> findWithDetailIncludeCourseById(@Param("lecturerId") Long id,
                        @Param("status") UserStatus status, @Param("role") UserRole role);

        @Query(value = """
                        select l.userId as lecturerId, u.name as lecturerName,
                        l.lecturerCode as lecturerCode, u.email as lecturerEmail,
                        u.gender as lecturerGender, l.department as lecturerDepartment,
                        l.academicTitle as lecturerAcademicTitle
                        from Lecturer l
                        join l.user u
                        where lower(u.email) like lower(concat('%', :q, '%'))
                        or lower(l.lecturerCode) like lower(concat('%', :q, '%'))
                        """, countQuery = "select count(l) from Lecturer l join l.user u")
        Page<LecturerProjection> fetchAll(Pageable pageable, @Param("q") String q);
}
