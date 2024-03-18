package org.example.cookercorner.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.cookercorner.entities.Image;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.User;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for {@link org.example.cookercorner.entities.Recipe}
 */
public record RecipeRequestDto(
                        @NotNull @NotEmpty
                        String recipeName,
                         @NotEmpty
                         String description,
                         @NotNull
                         String category,
                         @NotNull
                         String difficulty,
                         @Positive
                         int cookingTime,
                         List<Ingredient> ingredients
                         ) implements Serializable {
}