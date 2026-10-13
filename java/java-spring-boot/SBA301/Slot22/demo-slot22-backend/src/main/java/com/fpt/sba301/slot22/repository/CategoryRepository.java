package com.fpt.sba301.slot22.repository;

import com.fpt.sba301.slot22.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Short> {

    boolean existsByCategoryNameIgnoreCase(String categoryName);

    // Dùng khi update: trùng tên với category KHÁC chính nó
    boolean existsByCategoryNameIgnoreCaseAndCategoryIdNot(String categoryName, Short categoryId);

    // Có category khác đang dùng category này làm parent (self-reference)
    boolean existsByParentCategoryId(Short parentCategoryId);

    Page<Category> findByCategoryNameContainingIgnoreCase(String keyword, Pageable pageable);
}
