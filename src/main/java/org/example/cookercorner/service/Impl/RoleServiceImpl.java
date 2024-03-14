package org.example.cookercorner.service.Impl;


import lombok.RequiredArgsConstructor;
import org.example.cookercorner.entities.Role;
import org.example.cookercorner.repository.RoleRepository;
import org.example.cookercorner.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
   private final RoleRepository roleRepository;

   @Override
   public Optional<Role> getUserRole() {
      return roleRepository.findByName("ROLE_USER");
   }
}
