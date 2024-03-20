package org.example.cookercorner.dtos;

import jakarta.annotation.Nullable;
import lombok.Builder;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.cookercorner.entities.User}
 */
@Builder
public record UserProfileDto(@Nullable String imageUrl, String name, int recipeQuantity, int followerQuantity, int followingQuantity, @Nullable String biography, boolean isFollowed) implements Serializable {

}