package org.example.cookercorner.service;


import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.enums.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface RecipeService {
    Recipe addRecipe(RecipeRequestDto requestDto, MultipartFile image, Long userId);

    ResponseEntity<?> getByCategory(Category category, Long userId);

    RecipeDto getRecipeById(Long recipeId, Long userId);


    ResponseEntity<?> getRecipesByUserId(Long userId, Long currentUserId);
     boolean isLiked(Long recipeId, Long userId);
     boolean isSaved(Long recipeId, Long userId);


}
