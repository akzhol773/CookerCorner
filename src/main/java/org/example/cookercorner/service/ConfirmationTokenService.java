package org.example.cookercorner.service;


import org.example.cookercorner.entities.ConfirmationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;



public interface ConfirmationTokenService {

  void saveConfirmationToken(ConfirmationToken token);

   Optional <ConfirmationToken> getToken(String token);
}
