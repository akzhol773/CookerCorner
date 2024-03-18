package org.example.cookercorner.service;


import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Recipe;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RecipeService {
    Recipe addRecipe(RecipeRequestDto requestDto, MultipartFile image, Long userId);

    ResponseEntity<?> getByCategory(String category, Long userId);

    RecipeDto getRecipeById(Long recipeId, Long userId);
}
