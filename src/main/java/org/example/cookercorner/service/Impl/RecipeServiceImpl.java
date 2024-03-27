package org.example.cookercorner.service.Impl;
import jakarta.transaction.Transactional;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.Ingredient;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.enums.Difficulty;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.mapper.RecipeMapper;
import org.example.cookercorner.repository.RecipeRepository;
import org.example.cookercorner.repository.UserRepository;
import org.example.cookercorner.service.ImageService;
import org.example.cookercorner.service.RecipeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private final RecipeMapper recipeMapper;
    public RecipeServiceImpl(ImageService imageService, RecipeRepository recipeRepository, UserRepository userRepository, RecipeMapper recipeMapper) {
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
        this.userRepository = userRepository;
        this.recipeMapper = recipeMapper;
    }

    @Override
    @Transactional
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
        for(IngredientRequestDto ingredient: requestDto.ingredients()){
            Ingredient ingredient1 = new Ingredient();
            ingredient1.setRecipe(recipe);
            ingredient1.setName(ingredient.name());
            ingredient1.setAmount(ingredient.weight());
            ingredients.add(ingredient1);
        }
        recipe.setIngredients(ingredients);
        return recipeRepository.save(recipe);
    }

    @Override
    public ResponseEntity<List<RecipeListDto>> getByCategory(Category category, Long userId) {
        List<Recipe> recipes = recipeRepository.findPopularRecipes(category);
        return ResponseEntity.ok(recipeMapper.toRecipeListDtoList(recipes, userId));
    }




    @Override
    public RecipeDto getRecipeById(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        return recipeMapper.toRecipeDto(recipe, userId);
    }




    @Override
    public boolean isLiked(Long recipeId, Long userId){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getLikes().stream().anyMatch(user -> user.getId().equals(userId));
    }

    @Override
    public  boolean isSaved(Long recipeId, Long userId){
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        return recipe != null && recipe.getSaves().stream().anyMatch(user -> user.getId().equals(userId));
    }


    @Override
    public ResponseEntity<List<RecipeListDto>> searchRecipes(String query, Long currentUserId) {
        List<Recipe> recipes = recipeRepository.searchRecipes(query);
        return ResponseEntity.ok(recipeMapper.toRecipeListDtoList(recipes, currentUserId));

    }


    @Override
    public ResponseEntity<List<RecipeListDto>> getMyRecipe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes  = recipeRepository.findRecipesPageByUserId(user.getId());
        return ResponseEntity.ok(recipeMapper.toRecipeListDtoList(recipes, user.getId()));
    }


    @Override
    public ResponseEntity<List<RecipeListDto>> getMyFlaggedRecipe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes = recipeRepository.findFlaggedRecipes(user.getId());
        return ResponseEntity.ok(recipeMapper.toRecipeListDtoList(recipes, user.getId()));
    }

    @Override
    public ResponseEntity<List<RecipeListDto>> getRecipesByUserId(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes  = recipeRepository.findRecipesPageByUserId(user.getId());
        return ResponseEntity.ok(recipeMapper.toRecipeListDtoList(recipes, currentUserId));
    }


}

























