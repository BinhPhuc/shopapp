package com.project.shopapp.services;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Category;
import org.aspectj.weaver.ast.Not;

import java.util.List;

public interface ICategoryService {
    Category createCategory(CategoryDTO categoryDTO);
    Category getCategoryById(Long id) throws NotFoundException;
    List<Category> getAllCategory();

    Category updateCategory(Long id, CategoryDTO categoryDTO) throws NotFoundException;

    void deleteCategory(Long id);
}
