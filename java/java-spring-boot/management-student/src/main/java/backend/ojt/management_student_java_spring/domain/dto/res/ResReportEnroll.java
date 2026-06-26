package backend.ojt.management_student_java_spring.domain.dto.res;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import backend.ojt.management_student_java_spring.utils.constains.CourseStatus;
import backend.ojt.management_student_java_spring.utils.constains.EnrollmentStatus;
import backend.ojt.management_student_java_spring.utils.constains.LecturerAcademicTitle;
import backend.ojt.management_student_java_spring.utils.constains.LecturerDepartment;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResReportEnroll {
    @JsonProperty("course_id")
    Long courseId;
    @JsonProperty("course_name")
    String courseName;
    @JsonProperty("course_code")
    String courseCode;
    @JsonProperty("max_students")
    Integer maxStudents;
    @JsonProperty("current_students")
    Integer currentStudents;
    @JsonProperty("course_status")
    CourseStatus courseStatus;
    @JsonProperty("enroll_start_date")
    LocalDateTime enrollStartDate;
    @JsonProperty("enroll_end_date")
    LocalDateTime enrollEndDate;
    CourseSemester semester;
    String description;
    Integer credits;
    Integer year;
    LecturerInfo lecturer;
    Pagination pagination;
    List<StudentEnrollment> enrollments;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class StudentEnrollment {
        @JsonProperty("student_id")
        Long studentId;
        @JsonProperty("student_name")
        String studentName;
        @JsonProperty("student_code")
        String studentCode;
        @JsonProperty("student_email")
        String studentEmail;
        UserGender gender;
        EnrollmentStatus status;
        @JsonProperty("enroll_at")
        LocalDateTime enrollAt;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class LecturerInfo {
        Long id;
        String code;
        String name;
        String email;
        @JsonProperty("academic_title")
        LecturerAcademicTitle academicTitle;
        LecturerDepartment department;
        UserGender gender;
    }

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Pagination {
        int page;
        int pageSize;
        long total;
        int pages;
    }

}
