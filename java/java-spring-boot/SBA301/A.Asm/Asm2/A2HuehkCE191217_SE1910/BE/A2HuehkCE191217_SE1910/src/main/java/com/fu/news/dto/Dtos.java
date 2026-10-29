package com.fu.news.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

/** Tất cả DTO request/response gom vào một file cho gọn. */
public final class Dtos {

    private Dtos() {}

    // ---------- Auth ----------
    public record LoginRequest(
            @NotBlank(message = "Email is required")
            @Email(message = "Email is invalid")
            String email,
            @NotBlank(message = "Password is required")
            String password) {}

    public record LoginResponse(
            String token,
            Integer accountId,
            String accountName,
            String accountEmail,
            Integer accountRole) {}

    // ---------- Account ----------
    public record AccountRequest(
            @NotBlank(message = "Name is required")
            @Size(max = 100, message = "Name must be at most 100 characters")
            String accountName,
            @NotBlank(message = "Email is required")
            @Email(message = "Email is invalid")
            @Size(max = 100, message = "Email must be at most 100 characters")
            String accountEmail,
            @NotNull(message = "Role is required")
            @Min(value = 1, message = "Role must be 1 (Admin) or 2 (Staff)")
            @Max(value = 2, message = "Role must be 1 (Admin) or 2 (Staff)")
            Integer accountRole,
            /** Bắt buộc khi tạo mới; khi cập nhật để trống = giữ nguyên mật khẩu cũ. */
            @Size(min = 6, max = 50, message = "Password must be 6-50 characters")
            String accountPassword) {}

    public record ProfileRequest(
            @NotBlank(message = "Name is required")
            @Size(max = 100, message = "Name must be at most 100 characters")
            String accountName,
            @NotBlank(message = "Email is required")
            @Email(message = "Email is invalid")
            @Size(max = 100, message = "Email must be at most 100 characters")
            String accountEmail,
            @Size(min = 6, max = 50, message = "Password must be 6-50 characters")
            String accountPassword) {}

    public record AccountResponse(
            Integer accountId,
            String accountName,
            String accountEmail,
            Integer accountRole) {}

    // ---------- Category ----------
    public record CategoryRequest(
            @NotBlank(message = "Category name is required")
            @Size(max = 100, message = "Category name must be at most 100 characters")
            String categoryName,
            @NotBlank(message = "Description is required")
            @Size(max = 250, message = "Description must be at most 250 characters")
            String categoryDescription,
            Integer parentCategoryId,
            @NotNull(message = "Status is required")
            Boolean isActive) {}

    public record CategoryResponse(
            Integer categoryId,
            String categoryName,
            String categoryDescription,
            Integer parentCategoryId,
            String parentCategoryName,
            Boolean isActive) {}

    // ---------- Tag ----------
    public record TagRequest(
            @NotBlank(message = "Tag name is required")
            @Size(max = 50, message = "Tag name must be at most 50 characters")
            String tagName,
            @Size(max = 400, message = "Note must be at most 400 characters")
            String note) {}

    public record TagResponse(Integer tagId, String tagName, String note) {}

    // ---------- News ----------
    public record NewsRequest(
            @NotBlank(message = "Title is required")
            @Size(max = 400, message = "Title must be at most 400 characters")
            String newsTitle,
            @NotBlank(message = "Headline is required")
            @Size(max = 150, message = "Headline must be at most 150 characters")
            String headline,
            @NotBlank(message = "Content is required")
            @Size(max = 4000, message = "Content must be at most 4000 characters")
            String newsContent,
            @Size(max = 400, message = "Source must be at most 400 characters")
            String newsSource,
            @NotNull(message = "Category is required")
            Integer categoryId,
            @NotNull(message = "Status is required")
            Boolean newsStatus,
            List<Integer> tagIds) {}

    public record NewsResponse(
            Integer newsArticleId,
            String newsTitle,
            String headline,
            LocalDateTime createdDate,
            String newsContent,
            String newsSource,
            Integer categoryId,
            String categoryName,
            Boolean newsStatus,
            Integer createdById,
            String createdByName,
            Integer updatedById,
            String updatedByName,
            LocalDateTime modifiedDate,
            List<TagResponse> tags) {}

    // ---------- Error ----------
    public record ErrorResponse(int status, String message, java.util.Map<String, String> errors) {}
}
