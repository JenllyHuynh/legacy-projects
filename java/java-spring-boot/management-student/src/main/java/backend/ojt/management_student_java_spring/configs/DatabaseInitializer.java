package backend.ojt.management_student_java_spring.configs;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import backend.ojt.management_student_java_spring.domain.entity.Course;
import backend.ojt.management_student_java_spring.domain.entity.Enrollment;
import backend.ojt.management_student_java_spring.domain.entity.Lecturer;
import backend.ojt.management_student_java_spring.domain.entity.Student;
import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.repositories.CourseRepository;
import backend.ojt.management_student_java_spring.repositories.EnrollmentRepository;
import backend.ojt.management_student_java_spring.repositories.LecturerRepository;
import backend.ojt.management_student_java_spring.repositories.StudentRepository;
import backend.ojt.management_student_java_spring.repositories.UserRepository;
import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import backend.ojt.management_student_java_spring.utils.constains.EnrollmentStatus;
import backend.ojt.management_student_java_spring.utils.constains.LecturerAcademicTitle;
import backend.ojt.management_student_java_spring.utils.constains.LecturerDepartment;
import backend.ojt.management_student_java_spring.utils.constains.StudentMajor;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DatabaseInitializer implements CommandLineRunner {

        final PasswordEncoder passwordEncoder;
        final UserRepository userRepository;
        final LecturerRepository lecturerRepository;
        final StudentRepository studentRepository;
        final CourseRepository courseRepository;
        final EnrollmentRepository enrollmentRepository;

        @Override
        @Transactional
        public void run(String... args) {

                System.out.println(">>> START INIT DATABASE <<<");

                if (userRepository.count() > 0) {
                        System.out.println(">>> SKIP INIT DATABASE <<<");
                        return;
                }

                // ================= USERS =================

                User admin = createUser(
                                "System Admin",
                                "admin@gmail.com",
                                "0900000001",
                                UserGender.MALE,
                                UserRole.ADMIN);

                User lecturerUser1 = createUser(
                                "Nguyen Van Lecturer",
                                "lecturer1@gmail.com",
                                "0900000002",
                                UserGender.MALE,
                                UserRole.LECTURER);

                User lecturerUser2 = createUser(
                                "Tran Thi Lecturer",
                                "lecturer2@gmail.com",
                                "0900000003",
                                UserGender.FEMALE,
                                UserRole.LECTURER);

                User studentUser1 = createUser(
                                "Student One",
                                "student1@gmail.com",
                                "0900000004",
                                UserGender.MALE,
                                UserRole.STUDENT);

                User studentUser2 = createUser(
                                "Student Two",
                                "student2@gmail.com",
                                "0900000005",
                                UserGender.FEMALE,
                                UserRole.STUDENT);

                User studentUser3 = createUser(
                                "Student Three",
                                "student3@gmail.com",
                                "0900000006",
                                UserGender.MALE,
                                UserRole.STUDENT);

                userRepository.saveAll(List.of(
                                admin,
                                lecturerUser1,
                                lecturerUser2,
                                studentUser1,
                                studentUser2,
                                studentUser3));

                // ================= LECTURERS =================

                Lecturer lecturer1 = new Lecturer();
                lecturer1.setUser(lecturerUser1);
                lecturer1.setLecturerCode("LEC001");
                lecturer1.setDepartment(LecturerDepartment.COMPUTER_SCIENCE);
                lecturer1.setAcademicTitle(LecturerAcademicTitle.MSc);

                Lecturer lecturer2 = new Lecturer();
                lecturer2.setUser(lecturerUser2);
                lecturer2.setLecturerCode("LEC002");
                lecturer2.setDepartment(LecturerDepartment.COMPUTER_SCIENCE);
                lecturer2.setAcademicTitle(LecturerAcademicTitle.PhD);

                lecturerRepository.saveAll(List.of(
                                lecturer1,
                                lecturer2));

                // ================= STUDENTS =================

                Student student1 = new Student();
                student1.setUser(studentUser1);
                student1.setStudentCode("ST001");
                student1.setMajor(StudentMajor.SOFTWARE_ENGINEERING);

                Student student2 = new Student();
                student2.setUser(studentUser2);
                student2.setStudentCode("ST002");
                student2.setMajor(StudentMajor.CYBER_SECURITY);

                Student student3 = new Student();
                student3.setUser(studentUser3);
                student3.setStudentCode("ST003");
                student3.setMajor(StudentMajor.ARTIFICIAL_INTELLIGENCE);

                studentRepository.saveAll(List.of(
                                student1,
                                student2,
                                student3));

                // ================= COURSES =================

                Course javaCourse = new Course();
                javaCourse.setCourseCode("JAVA101");
                javaCourse.setName("Java Programming");
                javaCourse.setDescription("Core Java Course");
                javaCourse.setCredits(3);
                javaCourse.setMaxStudents(2);
                javaCourse.setCurrentStudents(2);
                javaCourse.setLecturer(lecturer1);
                javaCourse.setSemester(CourseSemester.FALL);
                javaCourse.setYear(2026);
                javaCourse.setEnrollStartDate(LocalDateTime.now());
                javaCourse.setEnrollEndDate(LocalDateTime.now().plusMonths(1));
                Course springCourse = new Course();
                springCourse.setCourseCode("SPRING202");
                springCourse.setName("Spring Boot");
                springCourse.setDescription("Spring Boot Advanced");
                springCourse.setCredits(4);
                springCourse.setMaxStudents(3);
                springCourse.setCurrentStudents(0);
                springCourse.setLecturer(lecturer1);
                springCourse.setSemester(CourseSemester.SPRING);
                springCourse.setYear(2026);
                springCourse.setEnrollStartDate(LocalDateTime.now());
                springCourse.setEnrollEndDate(LocalDateTime.now().plusMonths(2));

                Course securityCourse = new Course();
                securityCourse.setCourseCode("SEC303");
                securityCourse.setName("Cyber Security");
                securityCourse.setDescription("Security Fundamentals");
                securityCourse.setCredits(3);
                securityCourse.setMaxStudents(2);
                securityCourse.setCurrentStudents(1);
                securityCourse.setLecturer(lecturer2);
                securityCourse.setSemester(CourseSemester.SUMMER);
                securityCourse.setYear(2026);
                securityCourse.setEnrollStartDate(LocalDateTime.now());
                securityCourse.setEnrollEndDate(LocalDateTime.now().plusMonths(3));
                courseRepository.saveAll(List.of(
                                javaCourse,
                                springCourse,
                                securityCourse));

                // ================= ENROLLMENTS =================

                Enrollment enrollment1 = new Enrollment();
                enrollment1.setStudent(student1);
                enrollment1.setCourse(javaCourse);
                enrollment1.setStatus(EnrollmentStatus.ENROLLED);

                Enrollment enrollment2 = new Enrollment();
                enrollment2.setStudent(student2);
                enrollment2.setCourse(javaCourse);
                enrollment2.setStatus(EnrollmentStatus.ENROLLED);

                Enrollment enrollment3 = new Enrollment();
                enrollment3.setStudent(student3);
                enrollment3.setCourse(securityCourse);
                enrollment3.setStatus(EnrollmentStatus.ENROLLED);

                enrollmentRepository.saveAll(List.of(
                                enrollment1,
                                enrollment2,
                                enrollment3));

                System.out.println(">>> INIT DATABASE SUCCESS <<<");
        }

        private User createUser(
                        String name,
                        String email,
                        String phone,
                        UserGender gender,
                        UserRole role) {

                User user = new User();

                user.setName(name);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode("123123"));
                user.setGender(gender);
                user.setPhone(phone);
                user.setRole(role);
                user.setStatus(UserStatus.ACTIVE);

                return user;
        }
}