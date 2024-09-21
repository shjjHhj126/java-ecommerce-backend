package com.sherry.ecom.category;

import com.sherry.ecom.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest request) {

        Category parentCategory = null;
        if (request.getParentId() != null) {
            parentCategory = categoryService.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent category not found"));
        }

        Category category = Category.builder()
                .parent(parentCategory)
                .level(request.getLevel())
                .name(request.getName())
                .build();

        Category savedCategory = categoryService.create(category);

        CategoryResponse categoryResponse = CategoryResponse.builder()
                .id(savedCategory.getId())
                .level(savedCategory.getLevel())
                .name(savedCategory.getName())
                .build();

        if(savedCategory.getParent() != null){
            categoryResponse.setParentId(savedCategory.getParent().getId());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponse);
    }

    @GetMapping("/top")
    public ResponseEntity<List<Category>> getTopLevelCategories() {
        List<Category> topCategories = categoryService.getTopLevelCategories();
        return ResponseEntity.status(HttpStatus.OK).body(topCategories);
    }

    @GetMapping("/{parentId}/subcategories")
    public ResponseEntity<List<Category>> getSubcategories(@PathVariable Integer parentId)throws ResourceNotFoundException {
        List<Category> subcategories = categoryService.getSubcategoriesByParentId(parentId);
        return ResponseEntity.ok(subcategories);
    }

}
