package org.example.cookercorner.service;

import com.neobis.neoauth.entities.ConfirmationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service

public interface ConfirmationTokenService {

  void saveConfirmationToken(ConfirmationToken token);

   Optional <ConfirmationToken> getToken(String token);
}
