package org.example.cookercorner.dtos;

import lombok.Builder;

import java.io.Serializable;

/**
 * DTO for {@link org.example.cookercorner.entities.User}
 */
@Builder
public record MyProfileDto(String imageUrl, String name, int recipeQuantity, int followerQuantity, int followingQuantity, String biography) implements Serializable {

}