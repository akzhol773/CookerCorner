package org.example.cookercorner.service.Impl;
import jakarta.transaction.Transactional;
import org.example.cookercorner.dtos.RecipeDto;
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
import java.util.Optional;
import java.util.stream.Collectors;


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
        for(Ingredient ingredient: requestDto.ingredients()){
            Ingredient ingredient1 = new Ingredient();
            ingredient1.setRecipe(recipe);
            ingredient1.setName(ingredient.getName());
            ingredient1.setAmount(ingredient.getAmount());
            ingredients.add(ingredient1);
        }
        recipe.setIngredients(ingredients);
        return recipeRepository.save(recipe);
    }

    @Override
    public ResponseEntity<List<RecipeListDto>> getByCategory(Category category, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<User> followings  = user.getFollowings();
        List<Recipe> recipes = recipeRepository.findRecipesFromFollowings(category, followings);
        List<RecipeListDto> recipesDto = recipes.stream().map(recipe -> {
            int likesCount = recipe.getLikes().size();
            int savesCount = recipe.getSaves().size();
            boolean isLikedByUser = isLiked(recipe.getId(), userId);
            boolean isSavedByUser = isSaved(recipe.getId(), userId);

            return new RecipeListDto(
                    recipe.getRecipeName(),
                    recipe.getCreatedBy().getUsername(),
                    likesCount,
                    savesCount,
                    isLikedByUser,
                    isSavedByUser
            );
        }).collect(Collectors.toList());
        if (recipesDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(recipesDto);
        }
    }



    @Override
    public RecipeDto getRecipeById(Long recipeId, Long userId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(()-> new RecipeNotFoundException("Recipe not found"));
        List<Ingredient> ingredients = recipe.getIngredients();
        return new RecipeDto(
                recipe.getRecipeName(),
                recipe.getImage().getUrl(),
                recipe.getCreatedBy().getUsername(),
                recipe.getCookingTime(),
                recipe.getDifficulty().name(),
                (int) recipe.getLikes().stream().count(),
                isLiked(recipeId, userId),
                isSaved(recipeId, userId),
                recipe.getDescription(),
                ingredients
        );
    }




    @Override
    public boolean isLiked(Long recipeId, Long userId) {
        return getRecipeById(recipeId)
                .map(recipe -> recipe.getLikes().contains(userId))
                .orElse(false);
    }

    @Override
    public boolean isSaved(Long recipeId, Long userId) {
        return getRecipeById(recipeId)
                .map(recipe -> recipe.getSaves().contains(userId))
                .orElse(false);
    }

    private Optional<Recipe> getRecipeById(Long recipeId) {
        return  recipeRepository.findById(recipeId);

    }


    @Override
    public List<RecipeListDto> searchRecipes(String query, Long currentUserId) {
        List<Recipe> recipes = recipeRepository.searchRecipes(query);
        List<RecipeListDto> recipesDto = new ArrayList<>(recipes.size());
        for (Recipe recipe : recipes) {
            int likesCount = recipe.getLikes().size();
            int savesCount = recipe.getSaves().size();
            boolean isLiked = isLiked(recipe.getId(), currentUserId);
            boolean isSaved = isSaved(recipe.getId(), currentUserId);
            RecipeListDto dto = new RecipeListDto(
                    recipe.getRecipeName(),
                    recipe.getCreatedBy().getUsername(),
                    likesCount,
                    savesCount,
                    isLiked,
                    isSaved
            );
            recipesDto.add(dto);
        }
        return recipesDto;
    }


    @Override
    public ResponseEntity<List<RecipeListDto>> getMyRecipe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes = recipeRepository.findRecipesByUserId(user.getId());

        List<RecipeListDto> recipesDto = recipes.stream().map(recipe -> {
            int likesCount = recipe.getLikes().size();
            int savesCount = recipe.getSaves().size();
            boolean isLikedByUser = isLiked(recipe.getId(), userId);
            boolean isSavedByUser = isSaved(recipe.getId(), userId);

            return new RecipeListDto(
                    recipe.getRecipeName(),
                    recipe.getCreatedBy().getUsername(),
                    likesCount,
                    savesCount,
                    isLikedByUser,
                    isSavedByUser
            );
        }).collect(Collectors.toList());
        return recipesDto.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(recipesDto);
    }


    @Override
    public ResponseEntity<List<RecipeListDto>> getMyFlaggedRecipe(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes = recipeRepository.findFlaggedRecipes(user.getId());
        List<RecipeListDto> recipesDto = recipes.stream().map(recipe -> {
            int likesCount = recipe.getLikes().size();
            int savesCount = recipe.getSaves().size();
            boolean isLikedByUser = isLiked(recipe.getId(), userId);
            boolean isSavedByUser = isSaved(recipe.getId(), userId);

            return new RecipeListDto(
                    recipe.getRecipeName(),
                    recipe.getCreatedBy().getUsername(),
                    likesCount,
                    savesCount,
                    isLikedByUser,
                    isSavedByUser
            );
        }).collect(Collectors.toList());
        if (recipesDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(recipesDto);
        }

    }

    @Override
    public ResponseEntity<List<RecipeListDto>> getRecipesByUserId(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        List<Recipe> recipes = recipeRepository.findRecipesByUserId(user.getId());
        List<RecipeListDto> recipesDto = recipes.stream().map(recipe -> {
            int likesCount = recipe.getLikes().size();
            int savesCount = recipe.getSaves().size();
            boolean isLikedByUser = isLiked(recipe.getId(), currentUserId);
            boolean isSavedByUser = isSaved(recipe.getId(), currentUserId);

            return new RecipeListDto(
                    recipe.getRecipeName(),
                    recipe.getCreatedBy().getUsername(),
                    likesCount,
                    savesCount,
                    isLikedByUser,
                    isSavedByUser
            );
        }).collect(Collectors.toList());
        if (recipesDto.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(recipesDto);
        }
    }


}

























