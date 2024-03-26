package org.example.cookercorner.service.Impl;


import jakarta.transaction.Transactional;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.*;
import org.example.cookercorner.enums.TokenType;
import org.example.cookercorner.exceptions.*;
import org.example.cookercorner.repository.AccessTokenRepository;
import org.example.cookercorner.repository.ConfirmationTokenRepository;
import org.example.cookercorner.repository.RecipeRepository;
import org.example.cookercorner.repository.UserRepository;
import org.example.cookercorner.service.*;
import org.example.cookercorner.util.JwtTokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ImageService imageService;
    private final RecipeRepository recipeRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ImageService imageService,
                           RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public ResponseEntity<UserProfileDto> getUserProfile(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Recipe> recipeListDto = recipeRepository.findRecipesPageByUserId(userId);
        boolean isFollowed = isFollowed(userId, currentUserId);

        String photoUrl = (user.getPhoto() != null) ? user.getPhoto().getUrl() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";

        UserProfileDto userProfileDto = new UserProfileDto(
                user.getId(),
                photoUrl,
                user.getName(),
                recipeListDto.size(),
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getBiography(),
                isFollowed
        );
        return ResponseEntity.ok(userProfileDto);
    }
    @Override
    public List<UserDto> searchUser(String query) {
        List<User> users = userRepository.searchUsers(query);
        List<UserDto> userDto = new ArrayList<>();

        for(User user: users){
            String photoUrl = (user.getPhoto() != null) ? user.getPhoto().getUrl() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";
            UserDto dto = new UserDto(
                    user.getId(),
                    user.getName(),
                    photoUrl
            );
            userDto.add(dto);
        }
        return userDto;
    }
    @Override
    public ResponseEntity<MyProfileDto> getOwnProfile(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        List<Recipe> recipeListDto = recipeRepository.findRecipesPageByUserId(currentUserId);
        String photoUrl = (user.getPhoto() != null) ? user.getPhoto().getUrl() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";
        MyProfileDto userProfileDto = new MyProfileDto(
                photoUrl,
                user.getName(),
                recipeListDto.size(),
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getBiography()
        );
        return ResponseEntity.ok(userProfileDto);
    }
    @Override
    @Transactional
    public String updateUser(UserUpdateProfileDto request, Long currentUserId, MultipartFile image) {
        User user = userRepository.findById(currentUserId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setName(request.name());
        user.setBiography(request.biography());

        if(image!=null){
            user.setPhoto(imageService.saveImage(image));
        }
        userRepository.save(user);
        return "User profile successfully updated";
    }
    public boolean isFollowed(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        List<User> followings = currentUser.getFollowings();
        return followings.contains(user);
    }
}
