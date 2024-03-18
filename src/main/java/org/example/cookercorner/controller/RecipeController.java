package org.example.cookercorner.controller;


import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.service.RecipeService;
import org.example.cookercorner.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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


    @GetMapping("/get-by-category")
    public ResponseEntity<?> getRecipes(@RequestParam(value = "category") String category, Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
        }

        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return recipeService.getByCategory(category, userId);
    }



    @GetMapping("/{recipeId}")
    public ResponseEntity<Recipe> getRecipeById(Authentication authentication, @PathVariable Long recipeId) {
        Long userIdFromAuthToken = null;

        if (authentication != null) {
            userIdFromAuthToken = getUserIdFromAuthToken(authentication);
        }

        return ResponseEntity.ok(recipeService.getRecipeById(recipeId, userIdFromAuthToken));
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
