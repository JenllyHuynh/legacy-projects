package backend.ojt.management_student_java_spring.domain.dto.request;

public record ImportUserRow(
        int rowNum,
        String email,
        String name,
        String phone,
        String gender,
        String password,
        String confirmPassword,
        String role) {

}
