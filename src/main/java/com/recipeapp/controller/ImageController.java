package com.recipeapp.controller;

import com.recipeapp.model.Recipe;
import com.recipeapp.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipes/{id}/image")
public class ImageController {

    @Value("${app.image-storage-path}")
    private String storagePath;

    private final RecipeRepository recipeRepository;

    public ImageController(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @PostMapping
    public ResponseEntity<?> upload(@PathVariable Long id,
                                    @RequestParam("file") MultipartFile file) throws IOException {
        Optional<Recipe> opt = recipeRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Path dir = Path.of(storagePath);
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String ext = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf('.'))
                : ".jpg";

        // Delete old image if present
        Recipe recipe = opt.get();
        if (recipe.getImageFilename() != null) {
            Files.deleteIfExists(dir.resolve(recipe.getImageFilename()));
        }

        String filename = id + ext;
        file.transferTo(dir.resolve(filename));

        recipe.setImageFilename(filename);
        recipeRepository.save(recipe);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<byte[]> get(@PathVariable Long id) throws IOException {
        Optional<Recipe> opt = recipeRepository.findById(id);
        if (opt.isEmpty() || opt.get().getImageFilename() == null) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = Path.of(storagePath, opt.get().getImageFilename());
        if (!Files.exists(filePath)) return ResponseEntity.notFound().build();

        String filename = opt.get().getImageFilename().toLowerCase();
        MediaType mediaType = filename.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(Files.readAllBytes(filePath));
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@PathVariable Long id) throws IOException {
        Optional<Recipe> opt = recipeRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Recipe recipe = opt.get();
        if (recipe.getImageFilename() != null) {
            Files.deleteIfExists(Path.of(storagePath, recipe.getImageFilename()));
            recipe.setImageFilename(null);
            recipeRepository.save(recipe);
        }
        return ResponseEntity.noContent().build();
    }
}
