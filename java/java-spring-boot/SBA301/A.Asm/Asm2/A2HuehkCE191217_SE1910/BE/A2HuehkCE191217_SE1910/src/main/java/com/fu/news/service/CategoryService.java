package com.fu.news.service;

import com.fu.news.dto.Dtos.CategoryRequest;
import com.fu.news.dto.Dtos.CategoryResponse;
import com.fu.news.entity.Category;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final NewsArticleRepository newsRepository;

    public CategoryService(CategoryRepository categoryRepository, NewsArticleRepository newsRepository) {
        this.categoryRepository = categoryRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> search(String keyword) {
        List<Category> list = (keyword == null || keyword.isBlank())
                ? categoryRepository.findAll()
                : categoryRepository.findByCategoryNameContainingIgnoreCaseOrCategoryDescriptionContainingIgnoreCase(
                        keyword.trim(), keyword.trim());
        return list.stream().map(CategoryService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listActive() {
        return categoryRepository.findByIsActiveTrue().stream().map(CategoryService::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getById(Integer id) {
        return toResponse(find(id));
    }

    @Transactional
    public CategoryResponse create(CategoryRequest req) {
        Category c = new Category();
        apply(c, req, null);
        return toResponse(categoryRepository.save(c));
    }

    @Transactional
    public CategoryResponse update(Integer id, CategoryRequest req) {
        Category c = find(id);
        apply(c, req, id);
        return toResponse(categoryRepository.save(c));
    }

    /** Chỉ xoá được khi category chưa có bài viết nào và không phải cha của category khác. */
    @Transactional
    public void delete(Integer id) {
        Category c = find(id);
        if (newsRepository.existsByCategory_CategoryId(id)) {
            throw ApiException.conflict("Cannot delete: this category is used by news articles");
        }
        if (categoryRepository.existsByParentCategory_CategoryId(id)) {
            throw ApiException.conflict("Cannot delete: this category has child categories");
        }
        categoryRepository.delete(c);
    }

    private void apply(Category c, CategoryRequest req, Integer selfId) {
        c.setCategoryName(req.categoryName().trim());
        c.setCategoryDescription(req.categoryDescription().trim());
        c.setIsActive(req.isActive());
        if (req.parentCategoryId() == null) {
            c.setParentCategory(null);
        } else {
            if (req.parentCategoryId().equals(selfId)) {
                throw ApiException.badRequest("A category cannot be its own parent");
            }
            c.setParentCategory(find(req.parentCategoryId()));
        }
    }

    private Category find(Integer id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Category not found: " + id));
    }

    static CategoryResponse toResponse(Category c) {
        Category p = c.getParentCategory();
        return new CategoryResponse(
                c.getCategoryId(),
                c.getCategoryName(),
                c.getCategoryDescription(),
                p == null ? null : p.getCategoryId(),
                p == null ? null : p.getCategoryName(),
                c.getIsActive());
    }
}
