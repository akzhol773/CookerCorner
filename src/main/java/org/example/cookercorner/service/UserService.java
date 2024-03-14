package org.example.cookercorner.service;

import com.neobis.neoauth.dtos.*;
import com.neobis.neoauth.entities.ConfirmationToken;
import com.neobis.neoauth.entities.PasswordResetToken;
import com.neobis.neoauth.entities.User;
import org.springframework.http.ResponseEntity;



public interface UserService {
    ResponseEntity<UserResponseDto> createNewUser(UserRequestDto registrationUserDto);

    ResponseEntity<JwtResponseDto> authenticate(JwtRequestDto authRequest);

    ResponseEntity<JwtRefreshTokenDto> refreshToken(String refreshToken);

    ResponseEntity<String> confirmEmail(String token);

    ConfirmationToken generateConfirmToken(User user);

    ResponseEntity<String> resendConfirmation(ReconfirmEmailDto dto);

    ResponseEntity<String> forgotPassword(ForgotPasswordDto dto);

    ResponseEntity<String> resetPassword(String resetToken, ResetPasswordDto dto);
    public PasswordResetToken generateResetToken(User user);
}
