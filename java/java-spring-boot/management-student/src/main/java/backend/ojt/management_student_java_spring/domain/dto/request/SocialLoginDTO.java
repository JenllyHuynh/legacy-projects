package backend.ojt.management_student_java_spring.domain.dto.request;

import backend.ojt.management_student_java_spring.utils.constains.LoginWith;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SocialLoginDTO {
    private String accessToken;
    private LoginWith type;
}
