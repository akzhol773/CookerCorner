package org.example.cookercorner.mapper;

import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.repository.RecipeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    private final RecipeRepository recipeRepository;

    public RecipeMapper(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public RecipeListDto toRecipeListDto(Recipe recipe, boolean isLikedByUser, boolean isSavedByUser, String userId) {
        int likesCount = recipe.getLikes().size();
        int savesCount = recipe.getSaves().size();
        String imageUrl = (recipe.getImage() != null) ? recipe.getImage().getUrl() : null;
        return new RecipeListDto(
                recipe.getId(),
                imageUrl,
                recipe.getRecipeName(),
                recipe.getCreatedBy().getName(),
                likesCount,
                savesCount,
                isSavedByUser,
                isLikedByUser
        );
    }

    public List<RecipeListDto> toRecipeListDtoList(List<Recipe> recipes, String userId) {
        return recipes.stream()
                .map(recipe -> toRecipeListDto(recipe,
                        isLiked(recipe.getId(), userId),
                        isSaved(recipe.getId(), userId),
                        userId))
                .collect(Collectors.toList());
    }

    private boolean isLiked(Long recipeId, String userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getLikes().stream().anyMatch(user -> user.getId().equals(userId));
    }

    private boolean isSaved(Long recipeId, String userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getSaves().stream().anyMatch(user -> user.getId().equals(userId));
    }
}