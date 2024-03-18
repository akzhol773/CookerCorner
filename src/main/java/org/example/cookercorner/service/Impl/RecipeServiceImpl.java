package org.example.cookercorner.service.Impl;

import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.repository.RecipeRepository;
import org.example.cookercorner.repository.UserRepository;
import org.example.cookercorner.service.ImageService;
import org.example.cookercorner.service.RecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;



@Service
public class RecipeServiceImpl implements RecipeService {

    private final ImageService imageService;
    private  final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public RecipeServiceImpl(ImageService imageService, RecipeRepository recipeRepository, UserRepository userRepository) {
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Recipe addRecipe(RecipeRequestDto requestDto, MultipartFile image, Long userId) {
        Recipe recipe = new Recipe();
        recipe.setRecipeName(requestDto.recipeName());
        recipe.setCategory(Category.valueOf(requestDto.category().toUpperCase()));
        recipe.setDifficulty(Difficulty.valueOf(requestDto.difficulty().toUpperCase()));
        recipe.setDescription(requestDto.description());
        recipe.setImage(imageService.saveImage(image));
        recipe.setCookingTime(requestDto.cookingTime());
        User user = userRepository.findById(userId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        recipe.setCreatedBy(user);
        List<Ingredient> ingredients = new ArrayList<>();
        for(Ingredient ingredient: requestDto.ingredients()){
            Ingredient ingredient1 = new Ingredient();
            ingredient1.setRecipe(recipe);
            ingredient1.setName(ingredient.getName());
            ingredient1.setAmount(ingredient.getAmount());
            ingredient1.setMeasurement(ingredient.getMeasurement());
            ingredients.add(ingredient1);
        }
        recipe.setIngredients(ingredients);

           return recipeRepository.save(recipe);

    }

    @Override
    public ResponseEntity<?> getByCategory(String category, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<User> followings  = user.getFollowings();
        List<Recipe> recipes = recipeRepository.findRecipesFromFollowings(category, followings);
        List<RecipeListDto> recipesDto= new ArrayList<>();

        for(Recipe recipe: recipes){
            RecipeListDto dto = new RecipeListDto(
                    recipe.getRecipeName(),
                    recipe.getCreatedBy().getUsername(),
                    (int) recipe.getLikes().stream().count(),
                    (int) recipe.getSaves().stream().count(),
                    isLiked(recipe.getId(), user.getId()),
                    isSaved(recipe.getId(), user.getId())

                    );
            recipesDto.add(dto);
        }
        if (recipesDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(recipes);
        }


    }


    public boolean isLiked(Long recipeId, Long userId){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getLikes().stream().anyMatch(user -> user.getId().equals(userId));
    }

    public  boolean isSaved(Long recipeId, Long userId){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getSaves().stream().anyMatch(user -> user.getId().equals(userId));
    }

}

























