package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.dto.CategoryResponse;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.InvalidRequestException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import com.fpt.sba301.slot22.repository.NewsArticleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;
    private final NewsArticleRepository newsArticleRepository;

    public CategoryServiceImpl(CategoryRepository repository, NewsArticleRepository newsArticleRepository) {
        this.repository = repository;
        this.newsArticleRepository = newsArticleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> findAll(String keyword, Pageable pageable) {
        Page<Category> page = (keyword == null || keyword.isBlank())
                ? repository.findAll(pageable)
                : repository.findByCategoryNameContainingIgnoreCase(keyword.trim(), pageable);
        return page.map(this::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getById(short id) {
        return toResponse(findEntity(id));
    }

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        String name = request.categoryName().trim();
        if (repository.existsByCategoryNameIgnoreCase(name)) {
            throw new BusinessConflictException("Category name already exists");
        }
        checkParent(null, request.parentCategoryId());
        Category c = new Category();            // id do DB sinh (IDENTITY), không nhận từ client
        apply(c, request);
        return toResponse(repository.save(c));  // transient -> persist -> INSERT
    }

    @Override
    @Transactional
    public CategoryResponse update(short id, CategoryRequest request) {
        Category c = findEntity(id);            // không tồn tại -> 404, KHÔNG để save() tạo row mới
        String name = request.categoryName().trim();
        if (repository.existsByCategoryNameIgnoreCaseAndCategoryIdNot(name, c.getCategoryId())) {
            throw new BusinessConflictException("Category name already exists");
        }
        checkParent(c.getCategoryId(), request.parentCategoryId());
        apply(c, request);                      // entity đang managed -> dirty checking
        return toResponse(repository.save(c));
    }

    @Override
    @Transactional
    public void delete(short id) {
        Category c = findEntity(id);
        // Business rule Assignment 02: không xóa category đang được NewsArticle tham chiếu
        if (newsArticleRepository.existsByCategory_CategoryId(c.getCategoryId())) {
            throw new BusinessConflictException("Category is used by one or more news articles");
        }
        // Self-reference: không xóa category đang là parent của category khác
        if (repository.existsByParentCategoryId(c.getCategoryId())) {
            throw new BusinessConflictException("Category is the parent of other categories");
        }
        repository.delete(c);
    }

    // ---------- helpers ----------
    private Category findEntity(short id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category " + id + " not found"));
    }

    private void checkParent(Short selfId, Short parentId) {
        if (parentId == null) return;
        if (parentId.equals(selfId)) {
            throw new InvalidRequestException("Category cannot be its own parent");
        }
        if (!repository.existsById(parentId)) {
            throw new InvalidRequestException("Parent category " + parentId + " does not exist");
        }
    }

    // Mapping tường minh: tránh mass assignment (không copy id từ request)
    private void apply(Category c, CategoryRequest r) {
        c.setCategoryName(r.categoryName().trim());
        c.setCategoryDescription(r.categoryDescription().trim());
        c.setParentCategoryId(r.parentCategoryId());
        c.setActive(r.active() == null ? Boolean.TRUE : r.active());
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getCategoryId(), c.getCategoryName(),
                c.getCategoryDescription(), c.getParentCategoryId(), c.getActive());
    }
}
