package backend.ojt.management_student_java_spring.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data request for feat login
 **/
@Getter
@Setter
public class RequestAccount {
    @Email(message = "Email invalid format!")
    @NotBlank(message = "Email nempty!")
    String email;
    @NotBlank(message = "Password empty!")
    @Size(min = 6, message = "The password has fewer than 6 ccharacters!")
    String password;
}
