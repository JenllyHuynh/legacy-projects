package com.fu.news.service;

import com.fu.news.dto.Dtos.NewsRequest;
import com.fu.news.dto.Dtos.NewsResponse;
import com.fu.news.dto.Dtos.TagResponse;
import com.fu.news.entity.Category;
import com.fu.news.entity.NewsArticle;
import com.fu.news.entity.SystemAccount;
import com.fu.news.entity.Tag;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.SystemAccountRepository;
import com.fu.news.repository.TagRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NewsService {

    private final NewsArticleRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final SystemAccountRepository accountRepository;

    public NewsService(NewsArticleRepository newsRepository,
                       CategoryRepository categoryRepository,
                       TagRepository tagRepository,
                       SystemAccountRepository accountRepository) {
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.accountRepository = accountRepository;
    }

    // ---------- Public (không cần đăng nhập, chỉ tin active) ----------
    @Transactional(readOnly = true)
    public List<NewsResponse> searchPublic(String keyword, Integer categoryId) {
        return query(keyword, categoryId, true, null);
    }

    @Transactional(readOnly = true)
    public NewsResponse getPublic(Integer id) {
        NewsArticle n = find(id);
        if (!Boolean.TRUE.equals(n.getNewsStatus())) {
            throw ApiException.notFound("News article not found: " + id);
        }
        return toResponse(n);
    }

    // ---------- Staff ----------
    @Transactional(readOnly = true)
    public List<NewsResponse> search(String keyword, Integer categoryId, Boolean status) {
        return query(keyword, categoryId, status, null);
    }

    /** Lịch sử tin do chính staff đó tạo. */
    @Transactional(readOnly = true)
    public List<NewsResponse> history(Integer accountId, String keyword) {
        return query(keyword, null, null, accountId);
    }

    @Transactional(readOnly = true)
    public NewsResponse getById(Integer id) {
        return toResponse(find(id));
    }

    @Transactional
    public NewsResponse create(NewsRequest req, Integer currentAccountId) {
        NewsArticle n = new NewsArticle();
        n.setCreatedBy(getAccount(currentAccountId));
        n.setCreatedDate(LocalDateTime.now());
        apply(n, req);
        return toResponse(newsRepository.save(n));
    }

    @Transactional
    public NewsResponse update(Integer id, NewsRequest req, Integer currentAccountId) {
        NewsArticle n = find(id);
        n.setUpdatedBy(getAccount(currentAccountId));
        n.setModifiedDate(LocalDateTime.now());
        apply(n, req);
        return toResponse(newsRepository.save(n));
    }

    @Transactional
    public void delete(Integer id) {
        NewsArticle n = find(id);
        n.getTags().clear(); // xoá dòng trong bảng NewsTag
        newsRepository.delete(n);
    }

    // ---------- helpers ----------
    private void apply(NewsArticle n, NewsRequest req) {
        n.setNewsTitle(req.newsTitle().trim());
        n.setHeadline(req.headline().trim());
        n.setNewsContent(req.newsContent().trim());
        n.setNewsSource(req.newsSource() == null ? null : req.newsSource().trim());
        n.setNewsStatus(req.newsStatus());

        Category category = categoryRepository.findById(req.categoryId())
                .orElseThrow(() -> ApiException.badRequest("Category not found: " + req.categoryId()));
        if (!Boolean.TRUE.equals(category.getIsActive())) {
            throw ApiException.badRequest("Category is inactive: " + category.getCategoryName());
        }
        n.setCategory(category);

        Set<Tag> tags = new HashSet<>();
        if (req.tagIds() != null) {
            for (Integer tagId : new HashSet<>(req.tagIds())) {
                tags.add(tagRepository.findById(tagId)
                        .orElseThrow(() -> ApiException.badRequest("Tag not found: " + tagId)));
            }
        }
        n.setTags(tags);
    }

    private List<NewsResponse> query(String keyword, Integer categoryId, Boolean status, Integer createdById) {
        Specification<NewsArticle> spec = (root, cq, cb) -> {
            List<Predicate> ps = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.trim().toLowerCase() + "%";
                ps.add(cb.or(
                        cb.like(cb.lower(root.get("newsTitle")), like),
                        cb.like(cb.lower(root.get("headline")), like),
                        cb.like(cb.lower(root.get("newsContent")), like)));
            }
            if (categoryId != null) {
                ps.add(cb.equal(root.get("category").get("categoryId"), categoryId));
            }
            if (status != null) {
                ps.add(cb.equal(root.get("newsStatus"), status));
            }
            if (createdById != null) {
                ps.add(cb.equal(root.get("createdBy").get("accountId"), createdById));
            }
            return cb.and(ps.toArray(new Predicate[0]));
        };
        return newsRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdDate"))
                .stream().map(NewsService::toResponse).toList();
    }

    private NewsArticle find(Integer id) {
        return newsRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("News article not found: " + id));
    }

    private SystemAccount getAccount(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Account not found: " + id));
    }

    static NewsResponse toResponse(NewsArticle n) {
        SystemAccount cb = n.getCreatedBy();
        SystemAccount ub = n.getUpdatedBy();
        List<TagResponse> tags = n.getTags().stream()
                .map(TagService::toResponse)
                .sorted(Comparator.comparing(TagResponse::tagName, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return new NewsResponse(
                n.getNewsArticleId(),
                n.getNewsTitle(),
                n.getHeadline(),
                n.getCreatedDate(),
                n.getNewsContent(),
                n.getNewsSource(),
                n.getCategory().getCategoryId(),
                n.getCategory().getCategoryName(),
                n.getNewsStatus(),
                cb.getAccountId(),
                cb.getAccountName(),
                ub == null ? null : ub.getAccountId(),
                ub == null ? null : ub.getAccountName(),
                n.getModifiedDate(),
                tags);
    }
}
