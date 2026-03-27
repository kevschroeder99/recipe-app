package com.recipeapp.dto;

import com.recipeapp.model.Category;
import com.recipeapp.model.Difficulty;

import java.util.List;

public record RecipeRequest (String name, String description, Difficulty difficulty, Integer servings, Integer prepTime, Integer cookTime, List<Category> categories, List<Ingredient> ingredients, List<Step> steps) {

    public record Ingredient (String name, Double quantity, String unit) {
    }

    public record Step (Integer stepNumber, String instruction) {
    }


}
