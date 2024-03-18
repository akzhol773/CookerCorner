package org.example.cookercorner.dtos;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public record RecipeListDto(String recipeName, String author, int likesQuantity, int savesQuantity, boolean isSaved, boolean isLiked) implements Serializable {


}