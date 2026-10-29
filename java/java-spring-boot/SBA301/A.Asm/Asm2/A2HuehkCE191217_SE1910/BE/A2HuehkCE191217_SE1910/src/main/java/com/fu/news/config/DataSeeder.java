package com.fu.news.config;

import com.fu.news.entity.Category;
import com.fu.news.entity.NewsArticle;
import com.fu.news.entity.SystemAccount;
import com.fu.news.entity.Tag;
import com.fu.news.repository.CategoryRepository;
import com.fu.news.repository.NewsArticleRepository;
import com.fu.news.repository.SystemAccountRepository;
import com.fu.news.repository.TagRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

/** Tạo dữ liệu mẫu lần chạy đầu tiên (khi bảng còn trống). */
@Component
public class DataSeeder implements CommandLineRunner {

    private final SystemAccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final NewsArticleRepository newsRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-email}") private String adminEmail;
    @Value("${app.seed.admin-password}") private String adminPassword;
    @Value("${app.seed.staff-email}") private String staffEmail;
    @Value("${app.seed.staff-password}") private String staffPassword;

    public DataSeeder(SystemAccountRepository accountRepository, CategoryRepository categoryRepository,
                      TagRepository tagRepository, NewsArticleRepository newsRepository,
                      PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.newsRepository = newsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            return;
        }

        SystemAccount admin = account("Administrator", adminEmail, SystemAccount.ROLE_ADMIN, adminPassword);
        SystemAccount staff = account("Staff One", staffEmail, SystemAccount.ROLE_STAFF, staffPassword);

        Category campus = category("Campus Life", "News about life on campus", null);
        Category academic = category("Academic", "Academic announcements and schedules", null);
        category("Scholarships", "Scholarship opportunities", academic);

        Tag t1 = tag("announcement", "Official announcements");
        Tag t2 = tag("event", "Events and activities");
        tag("deadline", "Important deadlines");

        article("Welcome to the new semester", "Fall semester kicks off",
                "Classes begin next Monday. Please check your timetable on the student portal.",
                "FU Office", campus, staff, true, Set.of(t1, t2));
        article("Registration deadline extended", "Extra time to register",
                "The course registration deadline has been extended by three days.",
                "Academic Affairs", academic, staff, true, Set.of(t1));
        article("Draft: internal memo", "Not published yet",
                "This article is inactive and only visible to staff.",
                "Internal", academic, staff, false, Set.of());
    }

    private SystemAccount account(String name, String email, int role, String rawPassword) {
        SystemAccount a = new SystemAccount();
        a.setAccountName(name);
        a.setAccountEmail(email);
        a.setAccountRole(role);
        a.setAccountPassword(passwordEncoder.encode(rawPassword));
        return accountRepository.save(a);
    }

    private Category category(String name, String desc, Category parent) {
        Category c = new Category();
        c.setCategoryName(name);
        c.setCategoryDescription(desc);
        c.setParentCategory(parent);
        c.setIsActive(true);
        return categoryRepository.save(c);
    }

    private Tag tag(String name, String note) {
        Tag t = new Tag();
        t.setTagName(name);
        t.setNote(note);
        return tagRepository.save(t);
    }

    private void article(String title, String headline, String content, String source,
                         Category category, SystemAccount createdBy, boolean active, Set<Tag> tags) {
        NewsArticle n = new NewsArticle();
        n.setNewsTitle(title);
        n.setHeadline(headline);
        n.setNewsContent(content);
        n.setNewsSource(source);
        n.setCategory(category);
        n.setCreatedBy(createdBy);
        n.setCreatedDate(LocalDateTime.now());
        n.setNewsStatus(active);
        n.setTags(new java.util.HashSet<>(tags));
        newsRepository.save(n);
    }
}
