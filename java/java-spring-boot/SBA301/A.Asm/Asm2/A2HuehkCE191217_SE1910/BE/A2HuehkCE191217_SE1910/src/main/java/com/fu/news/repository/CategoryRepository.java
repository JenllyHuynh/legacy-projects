package com.fu.news.repository;

import com.fu.news.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

    List<Category> findByCategoryNameContainingIgnoreCaseOrCategoryDescriptionContainingIgnoreCase(String name, String description);

    List<Category> findByIsActiveTrue();

    boolean existsByParentCategory_CategoryId(Integer parentId);
}
