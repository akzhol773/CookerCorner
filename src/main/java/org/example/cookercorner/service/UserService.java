package org.example.cookercorner.service;

import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.ConfirmationToken;
import org.example.cookercorner.entities.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface UserService {
    ResponseEntity<UserResponseDto> createNewUser(UserRequestDto registrationUserDto);

    ResponseEntity<JwtResponseDto> authenticate(JwtRequestDto authRequest);

    ResponseEntity<JwtRefreshTokenDto> refreshToken(String refreshToken);

    ResponseEntity<String> confirmEmail(String token);

    ConfirmationToken generateConfirmToken(User user);

    ResponseEntity<String> resendConfirmation(ReconfirmEmailDto dto);

    boolean isFollowed(Long userId, Long currentUserId);

    ResponseEntity<UserProfileDto> getUserProfile(Long userId, Long currentUserId);

    List<UserDto> searchUser(String query);

    ResponseEntity<MyProfileDto> getOwnProfile(Long currentUserId);
    void revokeAllUserTokens(User user);

    String updateUser(UserUpdateProfileDto request, Long currentUserId, MultipartFile image);
}
