package com.project.shopapp.controllers;

import com.project.shopapp.dtos.CategoryDTO;
import com.project.shopapp.exception.NotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.services.CategoryService;
import com.project.shopapp.services.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/categories")
@Validated
// Dependency Injection
@RequiredArgsConstructor
public class CategoryController {
    private final ICategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok("create category succesfull");
    }
    @GetMapping("")
    public ResponseEntity<List<Category>> getAllCategories( //http://localhost:8088/api/v1/categories?page=1&limit=10
    ) {
        List<Category> categories = categoryService.getAllCategory();
        return ResponseEntity.ok(categories);
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<String> updateCategory(@PathVariable("id") Long id,
                                                 @RequestBody CategoryDTO categoryDTO) throws NotFoundException {
        categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok("Update category with id = " + id + " successfully!");
    }
    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> deleteCategory(@PathVariable("id") Long id) throws NotFoundException {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("delete category with id = " + id + " successfully!");
    }
}
