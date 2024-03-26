package org.example.cookercorner.service.Impl;


import lombok.RequiredArgsConstructor;
import org.example.cookercorner.entities.Role;
import org.example.cookercorner.repository.RoleRepository;
import org.example.cookercorner.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public class RoleServiceImpl implements RoleService {
   private final RoleRepository roleRepository;
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }
    @Override
   public Optional<Role> getUserRole() {
      return roleRepository.findByName("ROLE_USER");
   }
}
