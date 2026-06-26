package backend.ojt.management_student_java_spring.domain.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.LecturerAcademicTitle;
import backend.ojt.management_student_java_spring.utils.constains.LecturerDepartment;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;

public interface LecturerProjection {
    @JsonProperty("lecturer_id")
    Long getLecturerId();

    @JsonProperty("lecturer_name")
    String getLecturerName();

    @JsonProperty("lecturer_code")
    String getLecturerCode();

    @JsonProperty("lecturer_email")
    String getLecturerEmail();

    @JsonProperty("lecturer_gender")
    UserGender getLecturerGender();

    @JsonProperty("lecturer_department")
    LecturerDepartment getLecturerDepartment();

    @JsonProperty("lecturer_academic_title")
    LecturerAcademicTitle getLecturerAcademicTitle();

}
