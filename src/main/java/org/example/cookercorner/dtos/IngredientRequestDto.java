package org.example.cookercorner.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.example.cookercorner.entities.Ingredient}
 */
public record IngredientRequestDto(String name, String weight) implements Serializable {
}