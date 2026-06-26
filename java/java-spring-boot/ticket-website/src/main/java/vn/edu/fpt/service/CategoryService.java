package vn.edu.fpt.service;

import org.springframework.stereotype.Service;
import vn.edu.fpt.model.dto.CategoryDTO;
import vn.edu.fpt.model.dto.CategoryResponseDTO;
import vn.edu.fpt.model.entity.Category;
import vn.edu.fpt.repository.CategoryRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;

    public CategoryService(CategoryRepo categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public List<Category> getAllCategory() {
        return categoryRepo.findAll();
    }

    public Optional<Category> getCategoryById(int id) {
        return categoryRepo.findById(id);
    }



    //Note - N+1
    public List<CategoryResponseDTO> getAllCategoryResponse() {
        return categoryRepo.findAllWithEventCount().stream()
                .map(row -> new CategoryResponseDTO(
                        (Integer) row[0],   // id
                        (String)  row[1],   // name
                        (String)  row[2],   // iconName
                        ((Long)   row[3]).intValue()  // eventCount
                ))
                .collect(Collectors.toList());
    }

    public String createCategory(CategoryDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return "Category name is required";
        }
        if (categoryRepo.existsByName(dto.getName().trim())) {
            return "Category name already exists";
        }
        Category category = new Category();
        category.setName(dto.getName().trim());
        category.setIconName(dto.getIconName() != null ? dto.getIconName().trim() : null);
        categoryRepo.save(category);
        return null; // null = success
    }

    public String updateCategory(Integer id, CategoryDTO dto) {
        Optional<Category> opt = categoryRepo.findById(id);
        if (opt.isEmpty()) {
            return "Category not found";
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return "Category name is required";
        }
        if (categoryRepo.existsByNameAndIdNot(dto.getName().trim(), id)) {
            return "Category name already exists";
        }
        Category category = opt.get();
        category.setName(dto.getName().trim());
        category.setIconName(dto.getIconName() != null ? dto.getIconName().trim() : null);
        categoryRepo.save(category);
        return null; // null = success
    }

    public String deleteCategory(Integer id) {
        Optional<Category> opt = categoryRepo.findById(id);
        if (opt.isEmpty()) {
            return "Category not found";
        }
        if (!opt.get().getEvents().isEmpty()) {
            return "Cannot delete category that is linked to existing events";
        }
        categoryRepo.deleteById(id);
        return null; // null = success
    }
}