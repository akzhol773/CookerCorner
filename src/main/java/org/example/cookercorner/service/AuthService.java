package org.example.cookercorner.service;

import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.ConfirmationToken;
import org.example.cookercorner.entities.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<UserResponseDto> createNewUser(UserRequestDto registrationUserDto);
    ConfirmationToken generateConfirmToken(User user);
    ResponseEntity<JwtResponseDto> authenticate(JwtRequestDto authRequest);
    void revokeAllUserTokens(User user);
    ResponseEntity<JwtRefreshTokenDto> refreshToken(String refreshToken);
    ResponseEntity<String> confirmEmail(String token);
    ResponseEntity<String> resendConfirmation(ReconfirmEmailDto dto);
}
