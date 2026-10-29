package com.fu.news.controller;

import com.fu.news.dto.Dtos.AccountRequest;
import com.fu.news.dto.Dtos.AccountResponse;
import com.fu.news.security.AuthUser;
import com.fu.news.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Admin: quản lý tài khoản. */
@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public List<AccountResponse> search(@RequestParam(required = false) String keyword) {
        return accountService.search(keyword);
    }

    @GetMapping("/{id}")
    public AccountResponse get(@PathVariable Integer id) {
        return accountService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return accountService.create(request);
    }

    @PutMapping("/{id}")
    public AccountResponse update(@PathVariable Integer id, @Valid @RequestBody AccountRequest request) {
        return accountService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id, @AuthenticationPrincipal AuthUser user) {
        accountService.delete(id, user.accountId());
    }
}
