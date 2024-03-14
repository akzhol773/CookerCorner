package org.example.cookercorner.service.Impl;

import com.neobis.neoauth.entities.PasswordResetToken;
import com.neobis.neoauth.repository.ResetTokenServiceRepository;
import com.neobis.neoauth.service.ResetTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ResetTokenServiceImpl implements ResetTokenService {

    private final ResetTokenServiceRepository repository;
    @Override
    public void saveResetToken(PasswordResetToken token) {
        repository.save(token);
    }

    @Override
    public Optional<PasswordResetToken> getToken(String token) {
        return repository.findByToken(token);
    }



}
