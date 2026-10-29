package com.fu.news.service;

import com.fu.news.dto.Dtos.AccountRequest;
import com.fu.news.dto.Dtos.AccountResponse;
import com.fu.news.dto.Dtos.ProfileRequest;
import com.fu.news.entity.SystemAccount;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.SystemAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final SystemAccountRepository accountRepository;
    private final NewsArticleRepository newsRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(SystemAccountRepository accountRepository,
                          NewsArticleRepository newsRepository,
                          PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.newsRepository = newsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> search(String keyword) {
        List<SystemAccount> list = (keyword == null || keyword.isBlank())
                ? accountRepository.findAll()
                : accountRepository.findByAccountNameContainingIgnoreCaseOrAccountEmailContainingIgnoreCase(
                        keyword.trim(), keyword.trim());
        return list.stream().map(AccountService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse getById(Integer id) {
        return toResponse(find(id));
    }

    @Transactional
    public AccountResponse create(AccountRequest req) {
        if (req.accountPassword() == null || req.accountPassword().isBlank()) {
            throw ApiException.badRequest("Password is required");
        }
        if (accountRepository.existsByAccountEmailIgnoreCase(req.accountEmail().trim())) {
            throw ApiException.conflict("Email already exists");
        }
        SystemAccount a = new SystemAccount();
        a.setAccountName(req.accountName().trim());
        a.setAccountEmail(req.accountEmail().trim());
        a.setAccountRole(req.accountRole());
        a.setAccountPassword(passwordEncoder.encode(req.accountPassword()));
        return toResponse(accountRepository.save(a));
    }

    @Transactional
    public AccountResponse update(Integer id, AccountRequest req) {
        SystemAccount a = find(id);
        if (accountRepository.existsByAccountEmailIgnoreCaseAndAccountIdNot(req.accountEmail().trim(), id)) {
            throw ApiException.conflict("Email already exists");
        }
        a.setAccountName(req.accountName().trim());
        a.setAccountEmail(req.accountEmail().trim());
        a.setAccountRole(req.accountRole());
        if (req.accountPassword() != null && !req.accountPassword().isBlank()) {
            a.setAccountPassword(passwordEncoder.encode(req.accountPassword()));
        }
        return toResponse(accountRepository.save(a));
    }

    /** Chỉ xoá được khi tài khoản chưa tạo bài viết nào. */
    @Transactional
    public void delete(Integer id, Integer currentAccountId) {
        SystemAccount a = find(id);
        if (a.getAccountId().equals(currentAccountId)) {
            throw ApiException.conflict("You cannot delete your own account");
        }
        if (newsRepository.existsByCreatedBy_AccountId(id)) {
            throw ApiException.conflict("Cannot delete: this account has already created news articles");
        }
        accountRepository.delete(a);
    }

    // ---------- Profile (Staff) ----------
    @Transactional(readOnly = true)
    public AccountResponse getProfile(Integer currentAccountId) {
        return toResponse(find(currentAccountId));
    }

    @Transactional
    public AccountResponse updateProfile(Integer currentAccountId, ProfileRequest req) {
        SystemAccount a = find(currentAccountId);
        if (accountRepository.existsByAccountEmailIgnoreCaseAndAccountIdNot(req.accountEmail().trim(), currentAccountId)) {
            throw ApiException.conflict("Email already exists");
        }
        a.setAccountName(req.accountName().trim());
        a.setAccountEmail(req.accountEmail().trim());
        if (req.accountPassword() != null && !req.accountPassword().isBlank()) {
            a.setAccountPassword(passwordEncoder.encode(req.accountPassword()));
        }
        return toResponse(accountRepository.save(a));
    }

    private SystemAccount find(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Account not found: " + id));
    }

    static AccountResponse toResponse(SystemAccount a) {
        return new AccountResponse(a.getAccountId(), a.getAccountName(), a.getAccountEmail(), a.getAccountRole());
    }
}
