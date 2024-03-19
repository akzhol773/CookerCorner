package org.example.cookercorner.repository;

import org.example.cookercorner.entities.Recipe;
import org.example.cookercorner.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    @Query("Select u From User u Where u.isEnabled = false")
    List<User> findNotEnabledUsers();

    @Query("SELECT c FROM User c WHERE c.name LIKE CONCAT('%', :query, '%')")
    List<User> searchUsers(@Param("query") String query);



}