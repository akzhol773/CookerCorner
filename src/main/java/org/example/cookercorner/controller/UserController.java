package org.example.cookercorner.controller;


import org.example.cookercorner.dtos.RecipeListDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("api/users/")
public class UserController {


//    @GetMapping("/get-user-recipes/{userId}")
//    public ResponseEntity<List<RecipeListDto>> getRecipesByUser(@PathVariable Long userId, Authentication authentication){
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
//        }
//        Long currentUserId = tokenUtils.getUserIdFromAuthentication(authentication);
//        return recipeService.getRecipesByUserId(userId, currentUserId);
//    }



}
