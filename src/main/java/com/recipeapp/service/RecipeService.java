package com.recipeapp.service;

import com.recipeapp.dto.RecipeRequest;
import com.recipeapp.dto.RecipeResponse;
import com.recipeapp.model.*;
import com.recipeapp.repository.IngredientRepository;
import com.recipeapp.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {

    @Value("${app.image-storage-path}")
    private String storagePath;

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
    }

    public List<RecipeResponse> getAllRecipes() {
        return recipeRepository.findAll().stream().map(this::toResponse).toList();
    }

    public Optional<RecipeResponse> getRecipeById(Long id) {
        return recipeRepository.findById(id).map(this::toResponse);
    }

    @Transactional
    public RecipeResponse createRecipe(RecipeRequest request) {
        Recipe recipe = new Recipe();
        applyRequest(recipe, request);
        return toResponse(recipeRepository.save(recipe));
    }

    @Transactional
    public Optional<RecipeResponse> updateRecipe(Long id, RecipeRequest request) {
        return recipeRepository.findById(id).map(recipe -> {
            recipe.getSteps().clear();
            recipe.getRecipeIngredients().clear();
            recipe.getCategories().clear();
            applyRequest(recipe, request);
            return toResponse(recipeRepository.save(recipe));
        });
    }

    @Transactional
    public boolean deleteRecipe(Long id) {
        Optional<Recipe> opt = recipeRepository.findById(id);
        if (opt.isEmpty()) return false;

        Recipe recipe = opt.get();
        if (recipe.getImageFilename() != null) {
            try { Files.deleteIfExists(Path.of(storagePath, recipe.getImageFilename())); }
            catch (IOException ignored) {}
        }
        recipeRepository.deleteById(id);
        return true;
    }

    public List<RecipeResponse> searchByName(String name) {
        return recipeRepository.findByNameContainingIgnoreCase(name).stream().map(this::toResponse).toList();
    }

    public List<RecipeResponse> filterByCategory(Category category) {
        return recipeRepository.findByCategory(category).stream().map(this::toResponse).toList();
    }

    private void applyRequest(Recipe recipe, RecipeRequest req) {
        recipe.setName(req.getName());
        recipe.setDescription(req.getDescription());
        recipe.setDifficulty(req.getDifficulty());
        recipe.setServings(req.getServings());
        recipe.setPrepTime(req.getPrepTime());
        recipe.setCookTime(req.getCookTime());

        if (req.getSteps() != null) {
            for (RecipeRequest.StepDto s : req.getSteps()) {
                Step step = new Step();
                step.setRecipe(recipe);
                step.setStepNumber(s.getStepNumber());
                step.setInstruction(s.getInstruction());
                recipe.getSteps().add(step);
            }
        }

        if (req.getIngredients() != null) {
            for (RecipeRequest.IngredientDto i : req.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(i.getName())
                        .orElseGet(() -> ingredientRepository.save(new Ingredient(i.getName())));
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipe(recipe);
                ri.setIngredient(ingredient);
                ri.setQuantity(i.getQuantity());
                ri.setUnit(i.getUnit());
                recipe.getRecipeIngredients().add(ri);
            }
        }

        if (req.getCategories() != null) {
            recipe.getCategories().addAll(req.getCategories());
        }
    }

    private RecipeResponse toResponse(Recipe recipe) {
        RecipeResponse res = new RecipeResponse();
        res.setId(recipe.getId());
        res.setName(recipe.getName());
        res.setDescription(recipe.getDescription());
        res.setDifficulty(recipe.getDifficulty());
        res.setServings(recipe.getServings());
        res.setPrepTime(recipe.getPrepTime());
        res.setCookTime(recipe.getCookTime());
        res.setImageUrl(recipe.getImageFilename() != null ? "/api/recipes/" + recipe.getId() + "/image" : null);
        res.setCategories(recipe.getCategories());

        res.setIngredients(recipe.getRecipeIngredients().stream().map(ri -> {
            RecipeResponse.IngredientDto dto = new RecipeResponse.IngredientDto();
            dto.setId(ri.getId());
            dto.setName(ri.getIngredient().getName());
            dto.setQuantity(ri.getQuantity());
            dto.setUnit(ri.getUnit());
            return dto;
        }).toList());

        res.setSteps(recipe.getSteps().stream().map(s -> {
            RecipeResponse.StepDto dto = new RecipeResponse.StepDto();
            dto.setId(s.getId());
            dto.setStepNumber(s.getStepNumber());
            dto.setInstruction(s.getInstruction());
            return dto;
        }).toList());

        return res;
    }
}
