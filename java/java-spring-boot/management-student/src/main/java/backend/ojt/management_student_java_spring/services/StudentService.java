package backend.ojt.management_student_java_spring.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.ojt.management_student_java_spring.domain.dto.res.ResultPagination;
import backend.ojt.management_student_java_spring.domain.dto.res.StudentProjection;
import backend.ojt.management_student_java_spring.domain.entity.Student;
import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.repositories.EnrollmentRepository;
import backend.ojt.management_student_java_spring.repositories.StudentRepository;
import backend.ojt.management_student_java_spring.repositories.UserRepository;
import backend.ojt.management_student_java_spring.utils.SecurityUtils;
import backend.ojt.management_student_java_spring.utils.constains.StudentMajor;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;
import backend.ojt.management_student_java_spring.utils.exceptions.UnauthorizedException;
import backend.ojt.management_student_java_spring.utils.exceptions.AccessToResourceException;
import backend.ojt.management_student_java_spring.utils.exceptions.RequestErrorException;
import backend.ojt.management_student_java_spring.utils.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class StudentService {
    final StudentRepository studentRepository;
    final EnrollmentRepository enrollmentRepository;
    final UserRepository userRepository;

    /**
     * init student code
     * 
     * @param user
     **/
    public String generateStudentCode(Long userId) {
        String year = String.format("%02d",
                LocalDateTime.now().getYear() % 100);
        String studentCode = "S"
                + year
                + String.format("%06d", userId);
        return studentCode;
    }

    /**
     * init student when register account
     * 
     * @param user
     **/
    public void createStudent(User user) {

        this.studentRepository.save(
                Student.builder()
                        .studentCode(this.generateStudentCode(user.getId()))
                        .user(user)
                        .build());
    }

    /**
     * student register major
     * 
     * @param major
     **/
    @Transactional
    public void registerMajor(String major) {
        var userId = SecurityUtils.getCurrentUserIdOrNull();
        if (userId == null) {
            throw new UnauthorizedException("Not logged in!");
        }
        var student = this.studentRepository.findWithDetailById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found!"));
        if (student.getUser().getStatus().equals(UserStatus.INACTIVE)) {
            throw new AccessToResourceException("You do not have permission!");

        }
        if (!student.getUser().getRole().equals(UserRole.STUDENT)) {
            throw new RequestErrorException("Student not found!");
            // throw new AccessToResourceException("You do not have permission!");
        }
        if (major != null) {
            student.setMajor(StudentMajor.valueOf(major));

        }
    }

    /**
     * delete student by id
     * 
     * @param id
     **/
    public void delete(Long id) {
        var student = this.studentRepository.findWithDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found!"));
        if (this.enrollmentRepository.existsByStudentId(id)) {
            throw new RequestErrorException("It is not possible to delete a student who already join course!");
        }
        if (!student.getUser().getRole().equals(UserRole.STUDENT)) {
            throw new AccessToResourceException("You do not have permission!");
        }
        this.studentRepository.delete(student);
        this.userRepository.delete(student.getUser());
    }

    /**
     * find student by id
     * 
     * @param studentId
     * @return
     **/
    public StudentProjection fetchById(Long studentId) {
        return this.studentRepository.fetchStudentById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found!"));
    }

    /**
     * 
     * @param pageable
     * @param q        search key email or student code
     * @return
     */
    public ResultPagination fetchAll(Pageable pageable, String q) {
        var res = new ResultPagination();
        var meta = new ResultPagination.Meta();
        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        var page = this.studentRepository.fetchAll(pageable, q);
        meta.setPages(page.getTotalPages());
        meta.setTotal(page.getTotalElements());
        res.setMeta(meta);
        res.setResult(page.getContent());
        return res;
    }

}
