package org.example.cookercorner.controller;


import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.service.RecipeService;
import org.example.cookercorner.util.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/recipes")
public class RecipeController {
   private final RecipeService recipeService;
   private final JwtTokenUtils tokenUtils;

    public RecipeController(RecipeService recipeService, JwtTokenUtils tokenUtils) {
        this.recipeService = recipeService;
        this.tokenUtils = tokenUtils;
    }

    @GetMapping("/get_by_category")
    public ResponseEntity<List<RecipeListDto>> getRecipes(@RequestParam(value = "category") String category,
                                        Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        String upperCaseCategory = category.toUpperCase();
        try {
            Category categoryEnum = Category.valueOf(upperCaseCategory);
            return recipeService.getByCategory(categoryEnum, userId);
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(exception.getMessage());
        }
    }

    @GetMapping("/my_recipes")
    public ResponseEntity<List<RecipeListDto>> getMyRecipes(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);

        return recipeService.getMyRecipe(userId);
    }

    @GetMapping("/my_flagged_recipes")
    public ResponseEntity<List<RecipeListDto>> getMyFlaggedRecipes(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return recipeService.getMyFlaggedRecipe(userId);
    }

    @GetMapping("/get_by_userId/{userId}")
    public ResponseEntity<List<RecipeListDto>> getRecipesByUserId(Authentication authentication,  @PathVariable(name = "userId") Long userId) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long currentUserId = tokenUtils.getUserIdFromAuthentication(authentication);
        return recipeService.getRecipesByUserId(userId, currentUserId);
    }


    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long recipeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId, userId));
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

    @GetMapping("/search")
    public ResponseEntity<List<RecipeListDto>> search(@RequestParam(name = "query")String query, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(recipeService.searchRecipes(query, userId));
    }


}
