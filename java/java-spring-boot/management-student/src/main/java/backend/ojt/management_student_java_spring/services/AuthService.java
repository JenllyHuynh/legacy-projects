package backend.ojt.management_student_java_spring.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import backend.ojt.management_student_java_spring.domain.dto.request.RequestRegister;
import backend.ojt.management_student_java_spring.domain.dto.res.ResLogin;
import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.repositories.UserRepository;
import backend.ojt.management_student_java_spring.utils.SecurityUtils;
import backend.ojt.management_student_java_spring.utils.constains.LoginWith;
import backend.ojt.management_student_java_spring.utils.constains.UserGender;
import backend.ojt.management_student_java_spring.utils.constains.UserRole;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;
import backend.ojt.management_student_java_spring.utils.exceptions.ConflictDataException;
import backend.ojt.management_student_java_spring.utils.exceptions.ResourceNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Transactional

public class AuthService {
    final UserRepository userRepository;
    final PasswordEncoder passwordEncoder;
    final SessionManager sessionManager;
    final SecurityUtils securityUtils;
    final StudentService studentService;
    @Value("${djnd.jwt.access-token-validity-in-seconds}")
    private Long expiresIn;

    /**
     * Student create account
     * 
     * @param request
     * @return
     **/
    public long register(RequestRegister request) {
        var email = request.getEmail().toLowerCase();
        if (this.userRepository.existsByEmailAccount(email)) {
            throw new ConflictDataException("Email already exist!");
        }
        if (this.userRepository.existsByNumberPhone(request.getPhone())) {
            throw new ConflictDataException("Phone already exist!");
        }
        var user = this.userRepository.save(User.builder()
                .email(email)
                .gender(UserGender.valueOf(request.getGender()))
                .name(request.getName())
                .password(this.passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .status(UserStatus.ACTIVE)
                .role(UserRole.STUDENT)
                .build());
        this.studentService.createStudent(user);
        return user.getId();

    }

    /*
     * Return reponse information account and manage token
     */
    @Transactional
    public ResLogin generateLoginResponse(User user) {
        var res = new ResLogin();
        var userLogin = new ResLogin.UserLogin();
        var email = user.getEmail();

        userLogin.setEmail(email);
        userLogin.setId(user.getId());
        userLogin.setName(user.getName());
        userLogin.setPhone(user.getPhone());
        userLogin.setRole(user.getRole().toString());

        res.setUser(userLogin);
        var sessionID = this.sessionManager.createNewSession(user);
        var accessToken = this.securityUtils.createAccessToken(email, res, sessionID, user.getRole());
        var refreshToken = this.securityUtils.createRefreshToken(email, res);
        var updated = this.userRepository.updateRefreshTokenByUserId(user.getId(), refreshToken);
        if (updated <= 0) {
            throw new ResourceNotFoundException("User not found!");
        }
        res.setAccessToken(accessToken);
        res.setExpiresIn(expiresIn);
        res.setRefreshToken(refreshToken);
        return res;

    }

    public Map<String, Object> getInfoUser(String token, String link) {
        RestTemplate restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<Map> res = restTemplate.exchange(link, HttpMethod.GET, httpEntity, Map.class);
        return res.getBody();
    }

    public ResLogin generateUserLoginWithSocialMedia(String token, LoginWith loginWith) {
        if (loginWith.equals(LoginWith.GOOGLE)) {
            Map<String, Object> userInfo = this.getInfoUser(token, "https://www.googleapis.com/oauth2/v3/userinfo");
            var email = (String) userInfo.get("email");
             var name = (String) userInfo.get("name");
            boolean isEmailVerified = (Boolean) userInfo.get("email_verified");
            if (!isEmailVerified) {
                throw new BadCredentialsException("Google email not verified!");
            }
            return this.handleLoginSocialMedia(email, name, loginWith);


        }
        throw new BadCredentialsException("Login failure!");
    }

    public ResLogin handleLoginSocialMedia(String email, String name, LoginWith type) {
        var user = userRepository.findByEmailIgnoreCase(email);
        if (user != null) {
            return this.generateLoginResponse(user);
        } else {
            var newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            var encodePassword = passwordEncoder.encode(UUID.randomUUID().toString());
            newUser.setPassword(encodePassword);
            newUser.setLoginWith(type);
            newUser.setRole(UserRole.STUDENT);
            userRepository.save(newUser);
            return this.generateLoginResponse(newUser);
        }
    }

}
