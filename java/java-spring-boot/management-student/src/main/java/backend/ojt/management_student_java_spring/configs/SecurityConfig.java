package backend.ojt.management_student_java_spring.configs;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import backend.ojt.management_student_java_spring.utils.SecurityUtils;

/*
* config security for suitable for project
*/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true) // PreAuthorze or Secured
public class SecurityConfig {
    // secret key
    @Value("${djnd.jwt.base64-secret}")
    private String jwtKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /*
     * config some api pass and limit other api
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource corsConfig,
            CustomJwtAuthenticationConverter customConverter) throws Exception {

        String[] whiteList = {
                "/api/v1/auth/**",
                "/storage/**",
                "/ws/**"
        };

        http
                .cors(cors -> cors.configurationSource(corsConfig))
                .csrf(c -> c.disable()) // disable session cookie cause use JWT stateless

                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(whiteList).permitAll()
                        .anyRequest().authenticated())

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter(customConverter))))

                .formLogin(f -> f.disable())
                // not save session, user login
                // any request included JWT
                // jwt included info user
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    /**
     * init JWT: jwtEncoder.encode
     * 
     * @return
     **/
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    /**
     * convert base64 -> secret key
     * 
     * @return
     **/
    private SecretKey getSecretKey() {
        // decode base 64
        byte[] keyBytes = Base64.from(jwtKey).decode();
        // create object key with al HMAC
        return new SecretKeySpec(keyBytes, 0, keyBytes.length,
                SecurityUtils.JWT_ALGORITHM.getName());
    }

    /**
     * verify JwtDecoder
     * signature
     * token fixed ?
     * token valid ?
     * 
     * @return
     **/
    @Bean
    public JwtDecoder jwtDecoder() {
        // al HS256
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(SecurityUtils.JWT_ALGORITHM).build();
        return token -> {
            try {
                // nếu token là "undefined" hoặc rác, ném lỗi để
                // spring Security biết đây không phải là một nỗ lực login hợp lệ
                return jwtDecoder.decode(token);
            } catch (Exception ex) {
                // unauthenticated
                System.out.println(">>> JWT Error: " + ex.getMessage());
                throw ex;
            }
        };
    }

    /**
     * convert JWT claims -> Authentication object (Authories)
     * ex: {"sub": "1", "role": "ADMIN"} -> ROLE_ADMIN
     * use for hasRole("...")
     * 
     * @param customConverter
     * @return
     **/
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(
            CustomJwtAuthenticationConverter customConverter) {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // convert and check role
        jwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(customConverter);

        return jwtAuthenticationConverter;
    }

}
