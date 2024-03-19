package org.example.cookercorner.controller;


import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.service.RecipeService;
import org.example.cookercorner.util.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/recipes")
public class RecipeController {
   private final RecipeService recipeService;
   private final JwtTokenUtils tokenUtils;

    public RecipeController(RecipeService recipeService, JwtTokenUtils tokenUtils) {
        this.recipeService = recipeService;
        this.tokenUtils = tokenUtils;
    }


    @GetMapping("/get-by-category")
    public ResponseEntity<?> getRecipes(@RequestParam(value = "category") String category,
                                        Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        String upperCaseCategory = category.toUpperCase();
        try {
            Category categoryEnum = Category.valueOf(upperCaseCategory);
            return recipeService.getByCategory(categoryEnum, userId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid category provided");
        }
    }


    @GetMapping("/{recipeId}")
    public ResponseEntity<?> getRecipeById(@PathVariable Long recipeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId, userId));
    }


    @GetMapping("/get-user-recipe/{userId}")
    public ResponseEntity<?> getRecipesByUser(@PathVariable Long userId, Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }
        Long currentUserId = tokenUtils.getUserIdFromAuthentication(authentication);
        return recipeService.getRecipesByUserId(userId, currentUserId);
    }


    @PostMapping("/addRecipe")
    public ResponseEntity<String> addRecipe(@RequestPart("recipeDto") RecipeRequestDto requestDto, @RequestPart ("photo") MultipartFile image, Authentication authentication){
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        recipeService.addRecipe(requestDto, image, userId);
        return ResponseEntity.ok("Recipe has been added successfully");

    }


}
