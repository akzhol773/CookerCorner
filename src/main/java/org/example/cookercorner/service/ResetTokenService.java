package org.example.cookercorner.service;

import com.neobis.neoauth.entities.PasswordResetToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ResetTokenService {
    void saveResetToken(PasswordResetToken token);

    Optional<PasswordResetToken> getToken(String token);



}
