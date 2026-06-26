package backend.ojt.management_student_java_spring.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

/*
 * Manage session account
 */
@Service
@RequiredArgsConstructor
public class SessionManager {
    private final UserRepository userRepository;

    /**
     * Create new session for user
     * 
     */
    public String createNewSession(User user) {
        String newSessionId = UUID.randomUUID().toString();
        user.setSessionId(newSessionId);
        userRepository.save(user);
        return newSessionId;
    }

    /**
     * Check sesssion
     * 
     */
    public boolean isValidSession(String email, String sessionId) {
        User user = userRepository.findByEmailIgnoreCase(email);

        if (user == null || user.getSessionId() == null) {
            return false;
        }

        return user.getSessionId().equals(sessionId);
    }

    /**
     * Invalidate session user (logout)
     * 
     */
    public void invalidateSession(String email) {
        User user = userRepository.findByEmailIgnoreCase(email);
        if (user != null) {
            user.setSessionId(null);
            userRepository.save(user);
        }
    }
}
