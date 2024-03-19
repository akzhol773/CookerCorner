package org.example.cookercorner.dtos;

import lombok.Builder;
import org.example.cookercorner.entities.Ingredient;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.cookercorner.entities.Recipe}
 */
@Builder
public record RecipeDto(
        String recipeName,
        String imageUrl,
        String author,
        String cookingTime,
        String difficulty,
        int likeQuantity,
        boolean isLiked,
        boolean isSaved,
        String description,
        List<Ingredient> ingredients


) implements Serializable {
}