package org.example.cookercorner.dtos;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.cookercorner.entities.User}
 */
public record UserProfileDto(String imageUrl, String name, int recipeQuantity, int followerQuantity, int followingQuantity, boolean isFollowed, List<RecipeListDto> recipes) implements Serializable {
}