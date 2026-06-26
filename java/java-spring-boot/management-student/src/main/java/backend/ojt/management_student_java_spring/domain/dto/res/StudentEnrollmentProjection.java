package backend.ojt.management_student_java_spring.domain.dto.res;

import java.time.LocalDateTime;

import backend.ojt.management_student_java_spring.utils.constains.EnrollmentStatus;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;

public interface StudentEnrollmentProjection {
    Long getStudentId();

    String getStudentName();

    String getStudentCode();

    String getStudentEmail();

    UserGender getGender();

    EnrollmentStatus getStatus();

    LocalDateTime getEnrollAt();
}
