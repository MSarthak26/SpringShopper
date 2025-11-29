package SpringBootEcom.controller;

import SpringBootEcom.config.AppConstants;
import SpringBootEcom.payload.CategoryDTO;
import SpringBootEcom.payload.CategoryResponse;
import SpringBootEcom.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/api/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(
            @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER) Integer pageNumber,
            @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_CATEGORIES_BY)String sortBy,
            @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR)String sortOrder

    ){
        CategoryResponse categoryResponse = categoryService.getAllCategories(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(categoryResponse,HttpStatus.OK);
    }

    @PostMapping("/api/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategoryDTO = categoryService.createCategory(categoryDTO);
        return new ResponseEntity<>(savedCategoryDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/api/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@PathVariable Long categoryId){
            CategoryDTO savedCategoryDTO = categoryService.deleteCategory(categoryId);
            return new ResponseEntity<>(savedCategoryDTO,HttpStatus.OK);
    }

    @PutMapping("/api/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO,@PathVariable Long categoryId){
            CategoryDTO updateCategoryDTO = categoryService.updateCategory(categoryDTO,categoryId);
            return new ResponseEntity<>(updateCategoryDTO,HttpStatus.OK);
    }
}
