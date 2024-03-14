package org.example.cookercorner.service.Impl;

import com.neobis.neoauth.entities.Role;
import com.neobis.neoauth.repository.RoleRepository;
import com.neobis.neoauth.service.RoleService;
import lombok.RequiredArgsConstructor;
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
