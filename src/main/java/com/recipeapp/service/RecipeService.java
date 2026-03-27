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
        recipe.setName(req.name());
        recipe.setDescription(req.description());
        recipe.setDifficulty(req.difficulty());
        recipe.setServings(req.servings());
        recipe.setPrepTime(req.prepTime());
        recipe.setCookTime(req.cookTime());

        if (req.steps() != null) {
            for (RecipeRequest.Step s : req.steps()) {
                Step step = new Step();
                step.setRecipe(recipe);
                step.setStepNumber(s.stepNumber());
                step.setInstruction(s.instruction());
                recipe.getSteps().add(step);
            }
        }

        if (req.ingredients() != null) {
            for (RecipeRequest.Ingredient i : req.ingredients()) {
                Ingredient ingredient = ingredientRepository.findByNameIgnoreCase(i.name())
                        .orElseGet(() -> ingredientRepository.save(new Ingredient(i.name())));
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipe(recipe);
                ri.setIngredient(ingredient);
                ri.setQuantity(i.quantity());
                ri.setUnit(i.unit());
                recipe.getRecipeIngredients().add(ri);
            }
        }

        if (req.categories() != null) {
            recipe.getCategories().addAll(req.categories());
        }
    }

    private RecipeResponse toResponse(Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getDescription(),
                recipe.getDifficulty(),
                recipe.getServings(),
                recipe.getPrepTime(),
                recipe.getCookTime(),
                recipe.getImageFilename() != null ? "/api/recipes/" + recipe.getId() + "/image" : null,
                recipe.getCategories(),
                createIngredients(recipe),
                createSteps(recipe)
                );
    }

    private List<RecipeResponse.Step> createSteps(Recipe recipe){
       return recipe.getSteps().stream().map(s ->
               new RecipeResponse.Step(
                       s.getId(),
                       s.getStepNumber(),
                       s.getInstruction()))
               .toList();
    }

    private List<RecipeResponse.Ingredient> createIngredients(Recipe recipe) {
        return recipe.getRecipeIngredients().stream().map(ri ->
                new RecipeResponse.Ingredient(
                        ri.getId(),
                        ri.getIngredient().getName(),
                        ri.getQuantity(),
                        ri.getUnit()))
                .toList();
    }
}
