package org.example.cookercorner.repository;

import org.example.cookercorner.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}