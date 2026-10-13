package com.fpt.sba301.slot22.service;

import com.fpt.sba301.slot22.dto.CategoryRequest;
import com.fpt.sba301.slot22.dto.CategoryResponse;
import com.fpt.sba301.slot22.entity.Category;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.repository.CategoryRepository;
import com.fpt.sba301.slot22.repository.NewsArticleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock CategoryRepository repository;
    @Mock NewsArticleRepository newsArticleRepository;
    @InjectMocks CategoryServiceImpl service;

    private CategoryRequest req(String name) {
        return new CategoryRequest(name, "desc", null, true);
    }

    private Category entity(short id, String name) {
        Category c = new Category();
        c.setCategoryId(id);
        c.setCategoryName(name);
        c.setCategoryDescription("old");
        c.setActive(true);
        return c;
    }

    @Test
    void create_duplicateName_throwsConflict_andDoesNotSave() {
        when(repository.existsByCategoryNameIgnoreCase("Tech")).thenReturn(true);
        assertThatThrownBy(() -> service.create(req("Tech"))).isInstanceOf(BusinessConflictException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void create_valid_savesAndReturnsResponse() {
        when(repository.existsByCategoryNameIgnoreCase("Tech")).thenReturn(false);
        when(repository.save(any(Category.class))).thenAnswer(inv -> {
            Category c = inv.getArgument(0);
            c.setCategoryId((short) 7);
            return c;
        });
        CategoryResponse r = service.create(req("  Tech  "));
        assertThat(r.categoryId()).isEqualTo((short) 7);
        assertThat(r.categoryName()).isEqualTo("Tech"); // đã trim
    }

    @Test
    void getById_missing_throwsNotFound() {
        when(repository.findById((short) 99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById((short) 99)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void update_missing_throwsNotFound_andNeverSaves() {
        when(repository.findById((short) 99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.update((short) 99, req("X"))).isInstanceOf(ResourceNotFoundException.class);
        verify(repository, never()).save(any());
    }

    @Test
    void update_existing_changesFields() {
        Category c = entity((short) 1, "Old");
        when(repository.findById((short) 1)).thenReturn(Optional.of(c));
        when(repository.existsByCategoryNameIgnoreCaseAndCategoryIdNot("New", (short) 1)).thenReturn(false);
        when(repository.save(c)).thenReturn(c);

        CategoryResponse r = service.update((short) 1, req("New"));

        assertThat(r.categoryName()).isEqualTo("New");
        assertThat(c.getCategoryDescription()).isEqualTo("desc");
        verify(repository).save(c);
    }

    @Test
    void delete_referencedByNews_throwsConflict_andNeverDeletes() {
        Category c = entity((short) 1, "Academic");
        when(repository.findById((short) 1)).thenReturn(Optional.of(c));
        when(newsArticleRepository.existsByCategory_CategoryId((short) 1)).thenReturn(true);

        assertThatThrownBy(() -> service.delete((short) 1)).isInstanceOf(BusinessConflictException.class);
        verify(repository, never()).delete(any());
    }

    @Test
    void delete_free_deletes() {
        Category c = entity((short) 5, "Free");
        when(repository.findById((short) 5)).thenReturn(Optional.of(c));
        when(newsArticleRepository.existsByCategory_CategoryId((short) 5)).thenReturn(false);
        when(repository.existsByParentCategoryId((short) 5)).thenReturn(false);

        service.delete((short) 5);

        verify(repository).delete(c);
    }
}
