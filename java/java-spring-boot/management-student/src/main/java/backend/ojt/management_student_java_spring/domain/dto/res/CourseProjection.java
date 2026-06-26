package backend.ojt.management_student_java_spring.domain.dto.res;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import backend.ojt.management_student_java_spring.utils.constains.CourseStatus;

public interface CourseProjection {
    @JsonProperty("course_id")
    Long getCourseId();

    @JsonProperty("course_name")
    String getCourseName();

    @JsonProperty("course_code")
    String getCourseCode();

    @JsonProperty("course_current_students")
    Integer getCourseCurrentStudents();

    @JsonProperty("course_max_students")
    Integer getCourseMaxStudents();

    @JsonProperty("course_enroll_start_date")
    LocalDateTime getCourseEnrollStartDate();

    @JsonProperty("course_enroll_end_date")
    LocalDateTime getCourseEnrollEndDate();

    @JsonProperty("course_description")
    String getCourseDescription();

    @JsonProperty("course_credits")
    Integer getCourseCredits();

    @JsonProperty("course_semester")
    CourseSemester getCourseSemester();

    @JsonProperty("course_year")
    Integer getCourseYear();

    @JsonProperty("course_status")
    CourseStatus getCourseStatus();

}
