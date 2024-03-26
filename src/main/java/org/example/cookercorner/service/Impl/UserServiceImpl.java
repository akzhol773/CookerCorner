package org.example.cookercorner.service.Impl;


import jakarta.transaction.Transactional;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.*;
import org.example.cookercorner.enums.TokenType;
import org.example.cookercorner.exceptions.*;
import org.example.cookercorner.repository.AccessTokenRepository;
import org.example.cookercorner.repository.ConfirmationTokenRepository;
import org.example.cookercorner.repository.RecipeRepository;
import org.example.cookercorner.repository.UserRepository;
import org.example.cookercorner.service.*;
import org.example.cookercorner.util.JwtTokenUtils;
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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ImageService imageService;
    private final AccessTokenRepository accessTokenRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private static final String CONFIRM_EMAIL_LINK = System.getenv("CONFIRM_EMAIL_LINK");
    private final RecipeRepository recipeRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ImageService imageService, AccessTokenRepository accessTokenRepository, RoleService roleService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils, ConfirmationTokenService confirmationTokenService, EmailService emailService, ConfirmationTokenRepository confirmationTokenRepository,
                           RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.imageService = imageService;
        this.accessTokenRepository = accessTokenRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
        this.confirmationTokenRepository = confirmationTokenRepository;
        this.recipeRepository = recipeRepository;
    }


    @Override
    @Transactional
    public ResponseEntity<UserResponseDto> createNewUser(UserRequestDto registrationUserDto) {

        if (userRepository.findByEmail(registrationUserDto.email()).isPresent()) {
            throw new EmailAlreadyExistException("Email already exist. Please, try to use another one.");
        }
        User user = new User();
        user.setEnabled(false);
        user.setEmail(registrationUserDto.email());
        user.setName(registrationUserDto.name());
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
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtTokenUtils.generateAccessToken(user);
            String refreshToken = jwtTokenUtils.generateRefreshToken(user);
            AccessToken token = AccessToken.builder()
                    .user(user)
                    .token(accessToken)
                    .tokenType(TokenType.BEARER)
                    .revoked(false)
                    .expired(false)
                    .build();
            revokeAllUserTokens(user);
            accessTokenRepository.save(token);
            return ResponseEntity.ok(new JwtResponseDto(accessToken, refreshToken));
        } catch (AuthenticationException exception) {
            if (exception instanceof BadCredentialsException) {
                throw new BadCredentialsException("Invalid email or password");
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

            String usernameFromRefreshToken = jwtTokenUtils.getEmailFromRefreshToken(refreshToken);
            if (usernameFromRefreshToken == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            User user = userRepository.findByEmail(usernameFromRefreshToken).orElseThrow(() ->
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
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        userRepository.saveAndFlush(user);
        confirmationTokenRepository.saveAndFlush(confirmationToken);

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
        User user = userRepository.findByEmail(dto.email()).orElseThrow(() ->
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

    public boolean isFollowed(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new UsernameNotFoundException("Current user not found"));
        List<User> followings = currentUser.getFollowings();
        return followings.contains(user);
    }


    @Override
    public ResponseEntity<UserProfileDto> getUserProfile(Long userId, Long currentUserId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<Recipe> recipeListDto = recipeRepository.findRecipesPageByUserId(userId);
        boolean isFollowed = isFollowed(userId, currentUserId);

        String photoUrl = (user.getPhoto() != null) ? user.getPhoto().getUrl() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";

        UserProfileDto userProfileDto = new UserProfileDto(
                user.getId(),
                photoUrl,
                user.getName(),
                recipeListDto.size(),
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getBiography(),
                isFollowed
        );
        return ResponseEntity.ok(userProfileDto);
    }


    @Override
    public List<UserDto> searchUser(String query) {
        List<User> users = userRepository.searchUsers(query);
        List<UserDto> userDto = new ArrayList<>();

        for(User user: users){
            String photoUrl = (user.getPhoto() != null) ? user.getPhoto().getUrl() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";
            UserDto dto = new UserDto(
                    user.getId(),
                    user.getName(),
                    photoUrl
            );
            userDto.add(dto);
        }
        return userDto;
    }
    @Override
    public ResponseEntity<MyProfileDto> getOwnProfile(Long currentUserId) {
        User user = userRepository.findById(currentUserId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        List<Recipe> recipeListDto = recipeRepository.findRecipesPageByUserId(currentUserId);
        String photoUrl = (user.getPhoto() != null) ? user.getPhoto().getUrl() : "https://t4.ftcdn.net/jpg/03/32/59/65/240_F_332596535_lAdLhf6KzbW6PWXBWeIFTovTii1drkbT.jpg";
        MyProfileDto userProfileDto = new MyProfileDto(
                photoUrl,
                user.getName(),
                recipeListDto.size(),
                user.getFollowers().size(),
                user.getFollowings().size(),
                user.getBiography()
        );
        return ResponseEntity.ok(userProfileDto);
    }

    @Override
    public void revokeAllUserTokens(User user) {
        List<AccessToken> validUserTokens = accessTokenRepository.findAllValidTokensByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(t ->{
            t.setExpired(true);
            t.setRevoked(true);
        });
        accessTokenRepository.saveAll(validUserTokens);
    }

    @Override
    @Transactional
    public String updateUser(UserUpdateProfileDto request, Long currentUserId, MultipartFile image) {
        User user = userRepository.findById(currentUserId).orElseThrow(()-> new UsernameNotFoundException("User not found"));
        user.setName(request.name());
        user.setBiography(request.biography());

        if(image!=null){
            user.setPhoto(imageService.saveImage(image));
        }
        userRepository.save(user);
        return "User profile successfully updated";
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
