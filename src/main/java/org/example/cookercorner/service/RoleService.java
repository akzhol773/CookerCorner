package org.example.cookercorner.service;


import org.example.cookercorner.entities.Role;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RoleService {
   Optional <Role> getUserRole();
}
