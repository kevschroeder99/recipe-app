package com.recipeapp.service;

import com.recipeapp.model.Recipe;
import com.recipeapp.repository.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    private final RecipeRepository repository;

    public RecipeService(RecipeRepository repository) {
        this.repository = repository;
    }

    public List<Recipe> getAllRecipes() {
        return repository.findAll();
    }

    public Optional<Recipe> getRecipeById(Long id) {
        return repository.findById(id);
    }

    public Recipe createRecipe(Recipe recipe) {
        return repository.save(recipe);
    }

    public Optional<Recipe> updateRecipe(Long id, Recipe updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setIngredients(updated.getIngredients());
            existing.setInstructions(updated.getInstructions());
            existing.setDifficulty(updated.getDifficulty());
            existing.setServings(updated.getServings());
            existing.setCategory(updated.getCategory());
            existing.setPrepTime(updated.getPrepTime());
            return repository.save(existing);
        });
    }

    public boolean deleteRecipe(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Recipe> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public List<Recipe> filterByCategory(String category) {
        return repository.findByCategory(category);
    }
}
