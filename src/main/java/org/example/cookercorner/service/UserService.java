package org.example.cookercorner.service;

import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.ConfirmationToken;
import org.example.cookercorner.entities.User;
import org.springframework.http.ResponseEntity;



public interface UserService {
    ResponseEntity<UserResponseDto> createNewUser(UserRequestDto registrationUserDto);

    ResponseEntity<JwtResponseDto> authenticate(JwtRequestDto authRequest);

    ResponseEntity<JwtRefreshTokenDto> refreshToken(String refreshToken);

    ResponseEntity<String> confirmEmail(String token);

    ConfirmationToken generateConfirmToken(User user);

    ResponseEntity<String> resendConfirmation(ReconfirmEmailDto dto);

}
