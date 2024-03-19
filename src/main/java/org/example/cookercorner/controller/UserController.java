package org.example.cookercorner.controller;


import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.UserProfileDto;
import org.example.cookercorner.service.UserService;
import org.example.cookercorner.util.JwtTokenUtils;
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
    private final JwtTokenUtils tokenUtils;
    private final UserService userService;

    public UserController(JwtTokenUtils tokenUtils, UserService userService) {
        this.tokenUtils = tokenUtils;
        this.userService = userService;
    }


    @GetMapping("/get-user-profile/{userId}")
    public ResponseEntity<UserProfileDto> getRecipesByUser(@PathVariable Long userId, Authentication authentication){

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        Long currentUserId = tokenUtils.getUserIdFromAuthentication(authentication);
        return userService.getUserProfile(userId, currentUserId);
    }



}
