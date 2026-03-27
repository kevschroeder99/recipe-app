package com.recipeapp.repository;

import com.recipeapp.model.Category;
import com.recipeapp.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String name);

    @Query("SELECT r FROM Recipe r JOIN r.categories c WHERE c = :category")
    List<Recipe> findByCategory(@Param("category") Category category);
}
