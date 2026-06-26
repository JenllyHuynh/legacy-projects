package backend.ojt.management_student_java_spring.domain.dto.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.CourseSemester;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestCourse {
    Long id;
    @NotBlank(message = "Name empty!")
    String name;
    @NotBlank(message = "Course code empty!")
    @JsonProperty("course_code")
    String courseCode;
    String description;
    @NotNull(message = "Credits null")
    @Min(value = 1, message = "Credits must be greater than 0")
    Integer credits;
    @NotNull(message = "Max student null!")
    @Min(value = 1, message = "Max students must be greater than 0")
    @JsonProperty(value = "max_students")
    Integer maxStudents;
    CourseSemester semester;
    Integer year;
    @JsonProperty("lecturer_id")
    Long lecturerId;
    @NotNull(message = "Start date null!")
    @JsonProperty("enroll_start_date")
    LocalDateTime enrollStartDate;
    @NotNull(message = "End date null!")
    @JsonProperty("enroll_end_date")
    LocalDateTime enrollEndDate;
    @JsonProperty("status")
    String status;

}
