package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoryService implements ICategoryService {
    private final CategoryRepository categoryRepository;
    @Override
    public Category createCategory(CategoryDTO categoryDTO) {
        Category category = Category.builder().name(categoryDTO.getName()).build();
        return categoryRepository.save(category);
    }

    @Override
    public Category getCategoryById(Long id) throws NotFoundException {
        return categoryRepository.findById(id).orElseThrow(() -> {
            return new NotFoundException("Category with id = " + id + " not found!");
        });
    }

    @Override
    public List<Category> getAllCategory() {
        return categoryRepository.findAll();
    }

    @Override
    public Category updateCategory(Long categoryId, CategoryDTO categoryDTO) throws NotFoundException {
        Category currentCategory = getCategoryById(categoryId);
        currentCategory.setName(categoryDTO.getName());
        return categoryRepository.save(currentCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
