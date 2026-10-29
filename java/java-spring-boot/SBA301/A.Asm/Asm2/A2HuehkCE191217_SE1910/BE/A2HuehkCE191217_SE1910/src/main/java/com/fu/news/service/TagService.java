package com.fu.news.service;

import com.fu.news.dto.Dtos.TagRequest;
import com.fu.news.dto.Dtos.TagResponse;
import com.fu.news.entity.Tag;
import com.fu.news.exception.ApiException;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {

    private final TagRepository tagRepository;
    private final NewsArticleRepository newsRepository;

    public TagService(TagRepository tagRepository, NewsArticleRepository newsRepository) {
        this.tagRepository = tagRepository;
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public List<TagResponse> search(String keyword) {
        List<Tag> list = (keyword == null || keyword.isBlank())
                ? tagRepository.findAll()
                : tagRepository.findByTagNameContainingIgnoreCase(keyword.trim());
        return list.stream().map(TagService::toResponse).toList();
    }

    @Transactional
    public TagResponse create(TagRequest req) {
        Tag t = new Tag();
        t.setTagName(req.tagName().trim());
        t.setNote(req.note());
        return toResponse(tagRepository.save(t));
    }

    @Transactional
    public TagResponse update(Integer id, TagRequest req) {
        Tag t = find(id);
        t.setTagName(req.tagName().trim());
        t.setNote(req.note());
        return toResponse(tagRepository.save(t));
    }

    @Transactional
    public void delete(Integer id) {
        Tag t = find(id);
        if (newsRepository.existsByTags_TagId(id)) {
            throw ApiException.conflict("Cannot delete: this tag is used by news articles");
        }
        tagRepository.delete(t);
    }

    private Tag find(Integer id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Tag not found: " + id));
    }

    static TagResponse toResponse(Tag t) {
        return new TagResponse(t.getTagId(), t.getTagName(), t.getNote());
    }
}
