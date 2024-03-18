package org.example.cookercorner.repository;

import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {


    @Query("SELECT DISTINCT r FROM Recipe r JOIN r.createdBy u WHERE u IN :followings AND r.category = :category")
    List<Recipe> findRecipesFromFollowings(@Param("category") String category, @Param("followings") List<User> followings);

}