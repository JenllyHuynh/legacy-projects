package backend.ojt.management_student_java_spring.repositories;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import backend.ojt.management_student_java_spring.domain.entity.User;
import backend.ojt.management_student_java_spring.utils.constains.UserStatus;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @Query(value = "select exists(select 1 from User u where u.phone = :phone)")
    boolean existsByNumberPhone(@Param("phone") String phone);

    boolean existsByPhoneAndIdNot(String phone, Long id);

    @Query(value = "select exists(select 1 from User u where u.email = :email)")
    boolean existsByEmailAccount(@Param("email") String email);

    User findByEmailIgnoreCase(String email);

    /**
     * get user by email when login with credentials
     * 
     * @param email
     * @return optional user
     **/
    @Query(value = "select u from User u where u.email = :email and u.status = :status")
    Optional<User> findWithDetailByEmail(@Param("email") String email, @Param("status") UserStatus status);

    /**
     * update refresh token with user ID
     * 
     * @param userId
     * @param refreshToken
     * @return updated if updated <= 0 not found user
     **/
    @Modifying
    @Query(value = "update User u set u.refreshToken = :refreshToken where u.id = :userId")
    int updateRefreshTokenByUserId(@Param("userId") Long userId,
            @Param("refreshToken") String refreshToken);

    /**
     * get email already exists
     * 
     * @param emails
     * @return
     **/
    @Query(value = "select u.email from User u where u.email in :emails")
    Set<String> existEmails(@Param("emails") Set<String> emails);
}
