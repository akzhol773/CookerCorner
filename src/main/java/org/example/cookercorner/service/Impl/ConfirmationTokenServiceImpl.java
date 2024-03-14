package org.example.cookercorner.service.Impl;


import lombok.RequiredArgsConstructor;
import org.example.cookercorner.entities.ConfirmationToken;
import org.example.cookercorner.repository.ConfirmationTokenRepository;
import org.example.cookercorner.service.ConfirmationTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {
    private final ConfirmationTokenRepository confirmationTokenRepository;
    @Override
    public void saveConfirmationToken(ConfirmationToken token) {
        confirmationTokenRepository.save(token);
    }

    @Override
    public Optional<ConfirmationToken> getToken(String token) {
        return confirmationTokenRepository.findByToken(token);
    }


}
