package com.fu.news.controller;

import com.fu.news.dto.Dtos.NewsRequest;
import com.fu.news.dto.Dtos.NewsResponse;
import com.fu.news.security.AuthUser;
import com.fu.news.service.NewsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Staff: quản lý tin tức. */
@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public List<NewsResponse> search(@RequestParam(required = false) String keyword,
                                     @RequestParam(required = false) Integer categoryId,
                                     @RequestParam(required = false) Boolean status) {
        return newsService.search(keyword, categoryId, status);
    }

    @GetMapping("/history")
    public List<NewsResponse> history(@AuthenticationPrincipal AuthUser user,
                                      @RequestParam(required = false) String keyword) {
        return newsService.history(user.accountId(), keyword);
    }

    @GetMapping("/{id}")
    public NewsResponse get(@PathVariable Integer id) {
        return newsService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewsResponse create(@Valid @RequestBody NewsRequest request, @AuthenticationPrincipal AuthUser user) {
        return newsService.create(request, user.accountId());
    }

    @PutMapping("/{id}")
    public NewsResponse update(@PathVariable Integer id,
                               @Valid @RequestBody NewsRequest request,
                               @AuthenticationPrincipal AuthUser user) {
        return newsService.update(id, request, user.accountId());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        newsService.delete(id);
    }
}
