package com.sherry.ecom.category;

import com.sherry.ecom.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category create(Category category) {
        return categoryRepository.save(category);
    }

    public Optional<Category> findById(Integer id){
        return categoryRepository.findById(id);
    }

    public List<Category> getTopLevelCategories() {
        return categoryRepository.findByLevel(1);
    }

    public List<Category> getSubcategoriesByParentId(Integer parentId) throws ResourceNotFoundException {
        Category parentCategory = categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return categoryRepository.findByParent(parentCategory);
    }


}
