package backend.ojt.management_student_java_spring.configs;

import org.springframework.stereotype.Component;

import backend.ojt.management_student_java_spring.services.SessionManager;

import java.util.Collection;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * check token valid
 * check session invalid
 * convert role JWT to security permission
 * active in Security config
 * output: list authorities for user role
 * accept decode jwt-> return list authorize
 **/
@Component
public class CustomJwtAuthenticationConverter
        implements Converter<Jwt, Collection<GrantedAuthority>> {

    private final SessionManager sessionManager;

    public CustomJwtAuthenticationConverter(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {

        String email = jwt.getSubject();
        String sessionId = jwt.getClaimAsString("sessionId");

        if (email != null && sessionId != null) {

            boolean isValidSession = sessionManager.isValidSession(email, sessionId);

            if (!isValidSession) {
                throw new BadCredentialsException(
                        "Session expired or invalid");
            }
        }

        String role = jwt.getClaimAsString("role");

        if (role == null || role.isBlank()) {
            return List.of();
        }

        return List.of(
                new SimpleGrantedAuthority("ROLE_" + role));
    }
}