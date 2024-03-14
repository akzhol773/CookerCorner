package org.example.cookercorner.service.Impl;

import com.neobis.neoauth.dtos.*;
import com.neobis.neoauth.entities.ConfirmationToken;
import com.neobis.neoauth.entities.PasswordResetToken;
import com.neobis.neoauth.entities.Role;
import com.neobis.neoauth.entities.User;
import com.neobis.neoauth.exceptions.*;
import com.neobis.neoauth.repository.ConfirmationTokenRepository;
import com.neobis.neoauth.repository.ResetTokenServiceRepository;
import com.neobis.neoauth.repository.UserRepository;
import com.neobis.neoauth.service.*;
import com.neobis.neoauth.util.JwtTokenUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils, ConfirmationTokenService confirmationTokenService, EmailService emailService, ConfirmationTokenRepository confirmationTokenRepository, ResetTokenService resetTokenService, ResetTokenServiceRepository resetTokenServiceRepository) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.resetTokenService = resetTokenService;
        this.resetTokenServiceRepository = resetTokenServiceRepository;
    }

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final ResetTokenService resetTokenService;
    private final ResetTokenServiceRepository resetTokenServiceRepository;
    private static final String CONFIRM_EMAIL_LINK = System.getenv("CONFIRM_EMAIL_LINK");
    private static final String RESET_PASSWORD_EMAIL_LINK = System.getenv("RESET_PASSWORD_EMAIL_LINK");


    @Override
    public ResponseEntity<UserResponseDto> createNewUser(UserRequestDto registrationUserDto) {

        if (userRepository.findByUsername(registrationUserDto.username()).isPresent()) {
            throw new UsernameAlreadyTakenException("Username is already taken. Please, try to use another one.");
        }
        if (userRepository.findByEmailOrUsername(registrationUserDto.email()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exist. Please, try to use another one.");
        }
        User user = new User();
        user.setEnabled(false);
        user.setEmail(registrationUserDto.email());
        user.setUsername(registrationUserDto.username());
        Role userRole = roleService.getUserRole()
                .orElseThrow(() -> new UserRoleNotFoundException("Role not found."));
        user.setRoles(Collections.singletonList(userRole));
        String password = registrationUserDto.password();
        String confirmPassword = registrationUserDto.confirmPassword();
        if (!password.equals(confirmPassword)) {
            throw new PasswordDontMatchException("Passwords do not match.");
        }
        user.setPassword(passwordEncoder.encode(registrationUserDto.password()));
        userRepository.save(user);



        ConfirmationToken confirmationToken = generateConfirmToken(user);


        confirmationTokenService.saveConfirmationToken(confirmationToken);

        String link = CONFIRM_EMAIL_LINK + confirmationToken.getToken();
        emailService.sendConfirmationMail(link, user);

        return ResponseEntity.ok(new UserResponseDto("Success! Please, check your email for the confirmation", user.getUsername()));
    }



    @Override
    public ResponseEntity<JwtResponseDto> authenticate(JwtRequestDto authRequest) {

        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtTokenUtils.generateAccessToken(user);
            String refreshToken = jwtTokenUtils.generateRefreshToken(user);
            return ResponseEntity.ok(new JwtResponseDto(authRequest.username(), accessToken, refreshToken));


        } catch (AuthenticationException exception) {
            if (exception instanceof BadCredentialsException) {
                throw new BadCredentialsException("Invalid username or password");
            } else {
                throw new DisabledException("User is not enabled yet");
            }
        }
    }


    @Override
    public ResponseEntity<JwtRefreshTokenDto> refreshToken(String refreshToken) {

        try {
            if (refreshToken == null) {
                return ResponseEntity.badRequest().build();
            }

            String usernameFromRefreshToken = jwtTokenUtils.getUsernameFromRefreshToken(refreshToken);
            if (usernameFromRefreshToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            User user = userRepository.findByUsername(usernameFromRefreshToken).orElseThrow(() ->
                    new UsernameNotFoundException("User not found"));


            String accessToken = jwtTokenUtils.generateAccessToken(user);
            return ResponseEntity.ok(new JwtRefreshTokenDto(usernameFromRefreshToken, accessToken));

        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ResponseEntity<String> confirmEmail(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.getToken(token).orElseThrow(()->new TokenNotFoundException("Token not found"));
        if (confirmationToken.getConfirmedAt() != null) {
            throw new EmailAlreadyConfirmedException("Email already confirmed");
        }
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token has expired");

        }
        confirmationToken.setConfirmedAt(LocalDateTime.now());
        confirmationToken.getUser().setEnabled(true);

        return ResponseEntity.ok().body("Email successfully confirmed. Go back to your login page");
    }

    @Override
    public ConfirmationToken generateConfirmToken(User user) {
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                null,
                user);
        return confirmationToken;
    }

    @Override
    public ResponseEntity<String> resendConfirmation(ReconfirmEmailDto dto) {
        User user = userRepository.findByEmailOrUsername(dto.email()).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
        if(user.isEnabled()){
            throw new EmailAlreadyConfirmedException("Email already confirmed");
        }

        List<ConfirmationToken> confirmationTokens = confirmationTokenRepository.findByUser(user);
        for(ConfirmationToken confirmationToken : confirmationTokens){
            confirmationToken.setToken(null);
            confirmationTokenRepository.save(confirmationToken);
        }


        ConfirmationToken newConfirmationToken = generateConfirmToken(user);
        confirmationTokenRepository.save(newConfirmationToken);
        String link = CONFIRM_EMAIL_LINK + newConfirmationToken.getToken();
        emailService.sendConfirmationMail(link, user);
        return ResponseEntity.ok("Success! Please, check your email for the re-confirmation");
    }




    @Override
    public ResponseEntity<String> forgotPassword(ForgotPasswordDto dto) {
        User user = userRepository.findByEmailOrUsername(dto.emailOrUsername()).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));

        List<PasswordResetToken> confirmationTokens = resetTokenServiceRepository.findByUser(user);
        for(PasswordResetToken confirmationToken : confirmationTokens){
            confirmationToken.setToken(null);
            resetTokenServiceRepository.save(confirmationToken);
        }

        PasswordResetToken confirmationToken = generateResetToken(user);
        resetTokenService.saveResetToken(confirmationToken);

        String link = RESET_PASSWORD_EMAIL_LINK + confirmationToken.getToken();

        emailService.sendForgotPasswordMail(link, user);


        return ResponseEntity.ok().body("Email sent to reset your password");
    }


    @Override
    public ResponseEntity<String> resetPassword(String resetToken, ResetPasswordDto dto) {
        PasswordResetToken confirmationToken = resetTokenService.getToken(resetToken).orElseThrow(()->new TokenNotFoundException("Token not found"));
        LocalDateTime expiredAt = confirmationToken.getExpiresAt();
        if(expiredAt.isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token has expired");
        }
        confirmationToken.setResetAt(LocalDateTime.now());

        String password = dto.newPassword();
        String confirmPassword = dto.confirmNewPassword();

        if (!password.equals(confirmPassword)) {
            throw new PasswordDontMatchException("Passwords do not match.");
        }


        User user = confirmationToken.getUser();
        user.setPassword(passwordEncoder.encode(dto.newPassword()));
        userRepository.save(user);
        return ResponseEntity.ok().body("Password has been changed successfully");
    }

    @Override
    public PasswordResetToken generateResetToken(User user) {
        String token = UUID.randomUUID().toString();
        PasswordResetToken confirmationToken = new PasswordResetToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5),
                null,
                user);
        return confirmationToken;
    }



    @Scheduled(cron = "0 0 12 * * MON")
    private void sendWeeklyConfirmEmail() {
        List<User> users = userRepository.findNotEnabledUsers();
        for(User user: users){
            ConfirmationToken confirmationToken = generateConfirmToken(user);
            confirmationTokenService.saveConfirmationToken(confirmationToken);

            String link = CONFIRM_EMAIL_LINK + confirmationToken.getToken();
            emailService.sendConfirmationMail(link, user);
        }

    }
}
