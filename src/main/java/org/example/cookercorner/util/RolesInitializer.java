package org.example.cookercorner.util;


import org.example.cookercorner.entities.Role;
import org.example.cookercorner.repository.RoleRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class RolesInitializer implements ApplicationRunner {
    private final RoleRepository rolesRepository;
    public RolesInitializer(RoleRepository rolesRepository) {
        this.rolesRepository = rolesRepository;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (!rolesRepository.existsByName("ROLE_USER")) {

            Role roleUser = new Role(null, "This role is for users.", "ROLE_USER");
            rolesRepository.save(roleUser);
        }
        if (!rolesRepository.existsByName("ROLE_ADMIN")) {

            Role roleUser = new Role(null, "This role is for admins.", "ROLE_ADMIN");
            rolesRepository.save(roleUser);
        }
    }
}