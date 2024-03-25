package org.example.cookercorner.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.cookercorner.dtos.RecipeDto;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.RecipeRequestDto;
import org.example.cookercorner.dtos.UserUpdateProfileDto;
import org.example.cookercorner.enums.Category;
import org.example.cookercorner.service.RecipeService;
import org.example.cookercorner.util.JsonValidator;
import org.example.cookercorner.util.JwtTokenUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/recipes")
public class RecipeController {
   private final RecipeService recipeService;
   private final JwtTokenUtils tokenUtils;
    private final ObjectMapper objectMapper;
    private final JsonValidator jsonValidator;

    public RecipeController(RecipeService recipeService, JwtTokenUtils tokenUtils, ObjectMapper objectMapper, JsonValidator jsonValidator) {
        this.recipeService = recipeService;
        this.tokenUtils = tokenUtils;
        this.objectMapper = objectMapper;
        this.jsonValidator = jsonValidator;
    }
    @Operation(
            summary = "Get recipes by category",
            description = "Using this endpoint it is possible to get recipes by category",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
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

    @Operation(
            summary = "Get recipes of the current user",
            description = "Using this endpoint it is possible to get recipes of the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/my_recipes")
    public ResponseEntity<List<RecipeListDto>> getMyRecipes(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);

        return recipeService.getMyRecipe(userId);
    }


    @Operation(
            summary = "Get saved recipes of the current user",
            description = "Using this endpoint it is possible to get saved recipes o the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )

    @GetMapping("/my_flagged_recipes")
    public ResponseEntity<List<RecipeListDto>> getMyFlaggedRecipes(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return recipeService.getMyFlaggedRecipe(userId);
    }

    @Operation(
            summary = "Get recipes by user id",
            description = "Using this endpoint it is possible to get recipes by user id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )

    @GetMapping("/get_recipes_by_userId/{userId}")
    public ResponseEntity<List<RecipeListDto>> getRecipesByUserId(Authentication authentication,  @PathVariable(name = "userId") Long userId) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long currentUserId = tokenUtils.getUserIdFromAuthentication(authentication);
        return recipeService.getRecipesByUserId(userId, currentUserId);
    }

    @Operation(
            summary = "Get detailed page of the recipe",
            description = "Using this endpoint it is possible to get detailed recipe",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable Long recipeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(recipeService.getRecipeById(recipeId, userId));
    }


    @Operation(
            summary = "Add recipe",
            description = "Whenever user wants to create a new recipe then he or she should to use this endpoint",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe successfully created"),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @PostMapping("/add_recipe")
    public ResponseEntity<String> addRecipe(@RequestPart("recipeDto") String recipeDto,
                                            @RequestPart("photo") MultipartFile image,
                                            Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication required");
            }
            Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
            RecipeRequestDto requestDto = objectMapper.readValue(recipeDto, RecipeRequestDto.class);
            jsonValidator.validateRecipeRequest(requestDto);
            if (image == null || image.isEmpty() || !isImageFile(image)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid image file");
            }
            recipeService.addRecipe(requestDto, image, userId);
            return ResponseEntity.ok("Recipe has been added successfully");
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid recipe DTO JSON: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to add recipe: " + e.getMessage());
        }
    }

    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }



    @Operation(
            summary = "Search recipe",
            description = "Search recipes based on user query ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recipe list"),
                    @ApiResponse(responseCode = "404", description = "Recipe not found", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Authentication required")
            }
    )
    @GetMapping("/search")
    public ResponseEntity<List<RecipeListDto>> search(@RequestParam(name = "query")String query, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long userId = tokenUtils.getUserIdFromAuthentication(authentication);
        return ResponseEntity.ok(recipeService.searchRecipes(query, userId));
    }
}
