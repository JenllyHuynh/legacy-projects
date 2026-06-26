package backend.ojt.management_student_java_spring.configs;

import java.util.HashSet;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import backend.ojt.management_student_java_spring.domain.entity.User;

/**
 * Custom data user detail for account response
 * 
 * 
 **/
public record CustomUserDetails(User user) implements UserDetails {
    /*
     * ROLE_ for check @Pre hasRole('ADMIN')
     * return for auth.getAuthorities()
     * 
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        var role = user.getRole().toString();
        var authSet = new HashSet<SimpleGrantedAuthority>();
        authSet.add(new SimpleGrantedAuthority("ROLE_" + role));
        return authSet;
    }

    public Long getId() {
        return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }
}
