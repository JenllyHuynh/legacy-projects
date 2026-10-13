package com.fpt.sba301.slot22.repository;

import com.fpt.sba301.slot22.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, String> {

    // property "category" -> property "categoryId" của Category
    boolean existsByCategory_CategoryId(Short categoryId);
}
