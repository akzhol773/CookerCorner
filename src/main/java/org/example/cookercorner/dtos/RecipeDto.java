package org.example.cookercorner.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.example.cookercorner.entities.Recipe}
 */
public record RecipeDto(
                        Long id,
                        String recipeName,
                        String imageUrl,
                        String author,
                        int likeQuantity,
                        int saveQuantity,
                        boolean isLiked,
                        boolean isSaved


) implements Serializable {
}