package com.fpt.sba301.slot22.controller;

import com.fpt.sba301.slot22.dto.CategoryResponse;
import com.fpt.sba301.slot22.exception.BusinessConflictException;
import com.fpt.sba301.slot22.exception.ResourceNotFoundException;
import com.fpt.sba301.slot22.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired MockMvc mvc;
    @MockBean CategoryService service;

    private static final String VALID_BODY = """
            {"categoryName":"Tech","categoryDescription":"Tech news","parentCategoryId":null,"active":true}
            """;

    @Test
    void getMissing_returns404() throws Exception {
        when(service.getById((short) 99)).thenThrow(new ResourceNotFoundException("Category 99 not found"));
        mvc.perform(get("/api/categories/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void getExisting_returns200() throws Exception {
        when(service.getById((short) 1))
                .thenReturn(new CategoryResponse((short) 1, "Academic", "desc", null, true));
        mvc.perform(get("/api/categories/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryName").value("Academic"));
    }

    @Test
    void postValid_returns201() throws Exception {
        when(service.create(any())).thenReturn(new CategoryResponse((short) 10, "Tech", "Tech news", null, true));
        mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.categoryId").value(10));
    }

    @Test
    void postBlankName_returns400() throws Exception {
        String body = """
                {"categoryName":"","categoryDescription":"","active":true}
                """;
        mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.fieldErrors.categoryName").exists());
    }

    @Test
    void postDuplicate_returns409() throws Exception {
        when(service.create(any())).thenThrow(new BusinessConflictException("Category name already exists"));
        mvc.perform(post("/api/categories").contentType(MediaType.APPLICATION_JSON).content(VALID_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void postTextPlain_returns415() throws Exception {
        mvc.perform(post("/api/categories").contentType(MediaType.TEXT_PLAIN).content("hello"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void deleteReferenced_returns409() throws Exception {
        doThrow(new BusinessConflictException("Category is used by one or more news articles"))
                .when(service).delete((short) 1);
        mvc.perform(delete("/api/categories/1")).andExpect(status().isConflict());
    }

    @Test
    void deleteOk_returns204() throws Exception {
        mvc.perform(delete("/api/categories/5")).andExpect(status().isNoContent());
    }
}
