package com.ankitsaahariya.controller;

import com.ankitsaahariya.Service.AdminCategoryService;
import com.ankitsaahariya.dto.request.CategoryRequest;
import com.ankitsaahariya.dto.response.CategoryResponse;
import com.ankitsaahariya.dto.response.MessageResponse;
import com.ankitsaahariya.entities.Category;
import jakarta.mail.event.MailEvent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    @PostMapping("/create")
    public ResponseEntity<CategoryResponse> createCategory(@Valid  @RequestBody CategoryRequest request){
        return ResponseEntity.ok(adminCategoryService.createCategory(request));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @PathVariable long id,
            @Valid @RequestBody CategoryRequest request){
        return ResponseEntity.ok(adminCategoryService.updateCategory(id,request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<MessageResponse> deleteCategory(@PathVariable Long id){
        return ResponseEntity.ok(adminCategoryService.deleteCategory(id));
    }


    @GetMapping
    public ResponseEntity<Page<CategoryResponse>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "displayOrder") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return ResponseEntity.ok(adminCategoryService.getAllCategories(pageable));
    }
}
