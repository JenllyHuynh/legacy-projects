package backend.ojt.management_student_java_spring.domain.dto.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.StudentMajor;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;

public interface StudentProjection {
    @JsonProperty("student_id")
    Long getStudentId();

    @JsonProperty("student_name")
    String getStudentName();

    @JsonProperty("student_code")
    String getStudentCode();

    @JsonProperty("student_major")
    StudentMajor getStudentMajor();

    @JsonProperty("student_gender")
    UserGender getStudentGender();

    @JsonProperty("student_email")
    String getStudentEmail();

    @JsonProperty("student_credits")
    Integer getStudentCredits();
}
