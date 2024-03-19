package org.example.cookercorner.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.cookercorner.dtos.RecipeListDto;
import org.example.cookercorner.dtos.UserDto;
import org.example.cookercorner.dtos.UserProfileDto;
import org.example.cookercorner.service.UserService;
import org.example.cookercorner.util.JwtTokenUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @GetMapping("/search")
    public ResponseEntity<List<UserDto>> search(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(userService.searchUser(query));
    }

    @PutMapping("/update-profile")
    public ResponseEntity<String> changeProfile(@RequestPart("dto") String dto) {


    }






}
