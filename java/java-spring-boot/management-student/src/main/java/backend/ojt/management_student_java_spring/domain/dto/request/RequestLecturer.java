package backend.ojt.management_student_java_spring.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;

public record RequestLecturer(
                @NotNull(message = "Lecturer id null!") Long id,
                @JsonProperty("academic_title") String academicTitle,
                String department) {

}
