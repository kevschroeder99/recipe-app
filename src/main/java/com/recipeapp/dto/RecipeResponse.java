package com.recipeapp.dto;

import com.recipeapp.model.Category;
import com.recipeapp.model.Difficulty;

import java.util.List;

public record RecipeResponse(
        Long id,
        String name,
        String description,
        Difficulty difficulty,
        Integer servings,
        Integer prepTime,
        Integer cookTime,
        String imageUrl,
        List<Category> categories,
        List<Ingredient> ingredients,
        List<Step> steps
) {
    public record Ingredient(Long id, String name, Double quantity, String unit) {}
    public record Step(Long id, Integer stepNumber, String instruction) {}
}
