package org.example.cookercorner.service.Impl;

import jakarta.transaction.Transactional;
import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.exceptions.RecipeNotFoundException;
import org.example.cookercorner.repository.RecipeRepository;
import org.example.cookercorner.repository.UserRepository;
import org.example.cookercorner.service.ActionService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class ActionServiceImpl implements ActionService {
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public ActionServiceImpl(UserRepository userRepository, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    @Override
    public void putLikeIntoRecipe(Long recipeId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipe.getLikes().add(user);
        recipeRepository.save(recipe);

    }

    @Override
    public void removeLikeFromRecipe(Long recipeId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipe.getLikes().remove(user);
        recipeRepository.save(recipe);

    }

    @Override
    public void removeMarkFromRecipe(Long recipeId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));
        recipe.getSaves().remove(user);
        recipeRepository.save(recipe);

    }

    @Override
    public void putMarkIntoRecipe(Long recipeId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeNotFoundException("Recipe not found"));

        recipe.getSaves().add(user);
        recipeRepository.save(recipe);

    }

    @Override
    public void unfollowUser(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User to unfollow not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        List<User> followings = currentUser.getFollowings();
        List<User> followers = user.getFollowers();

        if (followings.remove(user)) {
            currentUser.setFollowings(followings);
            userRepository.save(currentUser);
        } else {
            throw new IllegalStateException("User was not being followed.");
        }

        if (followers.remove(currentUser)) {
            user.setFollowers(followers);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("Current user was not a follower of the user to unfollow.");
        }
    }





    @Override
    public void followUser(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User to follow not found"));
        User currentUser = userRepository.findById(currentUserId).orElseThrow(() -> new UsernameNotFoundException("Current user not found"));

        List<User> followings = currentUser.getFollowings();
        List<User> followers = user.getFollowers();

        if (!followings.contains(user)) {
            followings.add(user);
            currentUser.setFollowings(followings);
            userRepository.save(currentUser);
        } else {
            throw new IllegalStateException("User is already being followed.");
        }

        if (!followers.contains(currentUser)) {
            followers.add(currentUser);
            user.setFollowers(followers);
            userRepository.save(user);
        } else {
            throw new IllegalStateException("Current user is already a follower of the user to follow.");
        }
    }

}
