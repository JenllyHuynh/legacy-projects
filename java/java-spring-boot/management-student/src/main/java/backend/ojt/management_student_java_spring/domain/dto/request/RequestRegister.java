package backend.ojt.management_student_java_spring.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

/**
 * Data request for feat register
 **/
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RequestRegister extends RequestAccount {
    Long id;
    @NotBlank(message = "Name empty!")
    String name;
    @Pattern(regexp = "^(0|\\+84)[0-9]{9}$", message = "Phone invalid format!")
    String phone;
    @NotNull(message = "Gender not empty!")
    String gender;
    @Size(min = 6, message = "The confirm password has fewer than 6 characters!")
    @NotBlank(message = "Confirm password empty!")
    @JsonProperty(value = "confirm_password")
    @NotNull(message = "Confirm password empty!")
    String confirmPassword;
    UserRole role;

}
