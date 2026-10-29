package com.fu.news.controller;

import com.fu.news.dto.Dtos.CategoryResponse;
import com.fu.news.dto.Dtos.NewsResponse;
import com.fu.news.service.CategoryService;
import com.fu.news.service.NewsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Không cần đăng nhập: chỉ xem tin active. */
@RestController
@RequestMapping("/api/public")
public class PublicController {

    private final NewsService newsService;
    private final CategoryService categoryService;

    public PublicController(NewsService newsService, CategoryService categoryService) {
        this.newsService = newsService;
        this.categoryService = categoryService;
    }

    @GetMapping("/news")
    public List<NewsResponse> news(@RequestParam(required = false) String keyword,
                                   @RequestParam(required = false) Integer categoryId) {
        return newsService.searchPublic(keyword, categoryId);
    }

    @GetMapping("/news/{id}")
    public NewsResponse newsById(@PathVariable Integer id) {
        return newsService.getPublic(id);
    }

    @GetMapping("/categories")
    public List<CategoryResponse> categories() {
        return categoryService.listActive();
    }
}
