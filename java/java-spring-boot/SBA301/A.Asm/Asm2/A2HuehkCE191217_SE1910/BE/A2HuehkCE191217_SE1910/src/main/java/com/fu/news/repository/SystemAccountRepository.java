package com.fu.news.repository;

import com.fu.news.entity.SystemAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SystemAccountRepository extends JpaRepository<SystemAccount, Integer> {

    Optional<SystemAccount> findByAccountEmailIgnoreCase(String email);

    boolean existsByAccountEmailIgnoreCase(String email);

    boolean existsByAccountEmailIgnoreCaseAndAccountIdNot(String email, Integer accountId);

    List<SystemAccount> findByAccountNameContainingIgnoreCaseOrAccountEmailContainingIgnoreCase(String name, String email);
}
