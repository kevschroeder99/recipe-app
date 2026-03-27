package com.recipeapp.controller;

import com.recipeapp.dto.RecipeRequest;
import com.recipeapp.dto.RecipeResponse;
import com.recipeapp.model.Category;
import com.recipeapp.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RecipeController {

    private final RecipeService service;

    public RecipeController(RecipeService service) {
        this.service = service;
    }

    @GetMapping("/api/categories")
    public Category[] getCategories() {
        return Category.values();
    }

    @GetMapping("/api/recipes")
    public List<RecipeResponse> getAll(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {
        if (search != null && !search.isBlank()) {
            return service.searchByName(search);
        }
        if (category != null && !category.isBlank()) {
            try {
                return service.filterByCategory(Category.fromLabel(category));
            } catch (IllegalArgumentException e) {
                return List.of();
            }
        }
        return service.getAllRecipes();
    }

    @GetMapping("/api/recipes/{id}")
    public ResponseEntity<RecipeResponse> getById(@PathVariable Long id) {
        return service.getRecipeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/recipes")
    public ResponseEntity<RecipeResponse> create(@RequestBody RecipeRequest request) {
        return ResponseEntity.ok(service.createRecipe(request));
    }

    @PutMapping("/api/recipes/{id}")
    public ResponseEntity<RecipeResponse> update(@PathVariable Long id, @RequestBody RecipeRequest request) {
        return service.updateRecipe(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/api/recipes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (service.deleteRecipe(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
