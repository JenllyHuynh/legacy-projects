package backend.ojt.management_student_java_spring.domain.dto.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.LecturerAcademicTitle;
import backend.ojt.management_student_java_spring.utils.constains.LecturerDepartment;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResLecturer {
    Long id;
    String name;
    @JsonProperty("lecturer_code")
    String lecturerCode;
    String email;
    UserGender gender;
    LecturerDepartment department;
    @JsonProperty("academic_title")
    LecturerAcademicTitle academicTitle;
    List<ResCourse> courses;

    @Getter
    @Setter
    @FieldDefaults(level = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @Builder
    @AllArgsConstructor
    public static class ResCourse {
        Long id;
        String name;
        @JsonProperty("course_code")
        String courseCode;
    }

}
