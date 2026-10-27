package fu.se.lab06jwt.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, Object>> conflict(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", ex.getMessage()));
    }

    // Race condition: 2 request signup cùng email cùng lúc -> UNIQUE constraint của DB chặn
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, Object>> dataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already exists"));
    }

    // Dùng message chung cho cả "sai password" và "email không tồn tại" để tránh account enumeration
    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    ResponseEntity<Map<String, Object>> badCredentials(Exception ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid email or password"));
    }
}
