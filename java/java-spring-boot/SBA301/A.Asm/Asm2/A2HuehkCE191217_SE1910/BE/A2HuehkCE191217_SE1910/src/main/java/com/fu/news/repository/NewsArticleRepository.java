package com.fu.news.repository;

import com.fu.news.entity.NewsArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface NewsArticleRepository extends JpaRepository<NewsArticle, Integer>, JpaSpecificationExecutor<NewsArticle> {

    boolean existsByCreatedBy_AccountId(Integer accountId);

    boolean existsByCategory_CategoryId(Integer categoryId);

    boolean existsByTags_TagId(Integer tagId);
}
