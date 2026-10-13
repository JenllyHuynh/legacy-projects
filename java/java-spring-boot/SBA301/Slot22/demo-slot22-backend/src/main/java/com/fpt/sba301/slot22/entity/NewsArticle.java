package com.fpt.sba301.slot22.entity;

import jakarta.persistence.*;

/** Map tối thiểu cho bài tập 6.2: chỉ cần id, headline và quan hệ tới Category. */
@Entity
@Table(name = "NewsArticle")
public class NewsArticle {

    @Id
    @Column(name = "NewsArticleID", length = 20)
    private String newsArticleId;

    @Column(name = "Headline", length = 150)
    private String headline;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CategoryID")
    private Category category;

    public NewsArticle() {}

    public String getNewsArticleId() { return newsArticleId; }
    public void setNewsArticleId(String newsArticleId) { this.newsArticleId = newsArticleId; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
