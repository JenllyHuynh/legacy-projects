package com.fu.news.controller;

import com.fu.news.dto.Dtos.AccountResponse;
import com.fu.news.dto.Dtos.ProfileRequest;
import com.fu.news.security.AuthUser;
import com.fu.news.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Staff: quản lý hồ sơ của chính mình. */
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AccountService accountService;

    public ProfileController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public AccountResponse get(@AuthenticationPrincipal AuthUser user) {
        return accountService.getProfile(user.accountId());
    }

    @PutMapping
    public AccountResponse update(@AuthenticationPrincipal AuthUser user,
                                  @Valid @RequestBody ProfileRequest request) {
        return accountService.updateProfile(user.accountId(), request);
    }
}
