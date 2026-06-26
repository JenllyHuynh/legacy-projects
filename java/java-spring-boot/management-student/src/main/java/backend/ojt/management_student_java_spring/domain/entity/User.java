package backend.ojt.management_student_java_spring.domain.entity;

import backend.ojt.management_student_java_spring.utils.constains.LoginWith;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Column(nullable = false)
    String name;
    @Column(unique = true, nullable = false)
    String email;
    @Enumerated(EnumType.STRING)
    UserGender gender;
    String password;
    @Column(length = 2000)
    String refreshToken;
    @Enumerated(EnumType.STRING)
    UserRole role;
    String phone;
    @Enumerated(EnumType.STRING)
    UserStatus status;
    @Enumerated(EnumType.STRING)
    LoginWith loginWith;
    String sessionId;
}
