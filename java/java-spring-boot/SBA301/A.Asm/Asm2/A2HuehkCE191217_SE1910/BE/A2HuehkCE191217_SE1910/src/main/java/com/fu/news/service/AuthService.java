package com.fu.news.service;

import com.fu.news.dto.Dtos.LoginRequest;
import com.fu.news.dto.Dtos.LoginResponse;
import com.fu.news.entity.SystemAccount;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.SystemAccountRepository;
import com.fu.news.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SystemAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(SystemAccountRepository accountRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        SystemAccount account = accountRepository.findByAccountEmailIgnoreCase(request.email().trim())
                .filter(a -> passwordEncoder.matches(request.password(), a.getAccountPassword()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));
        return new LoginResponse(
                jwtService.generateToken(account),
                account.getAccountId(),
                account.getAccountName(),
                account.getAccountEmail(),
                account.getAccountRole());
    }
}
