package org.example.cookercorner.controller;



import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.cookercorner.dtos.*;
import org.example.cookercorner.entities.User;
import org.example.cookercorner.repository.UserRepository;
import org.example.cookercorner.service.AuthService;
import org.example.cookercorner.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/auth/")
public class AuthController {
    private final UserRepository userRepository;
    private final AuthService authService;
    @Autowired
    public AuthController(UserRepository userRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.authService = authService;
    }


    @Operation(
            summary = "Login",
            description = "Endpoint for getting tokens after login"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned a token"),
            @ApiResponse(responseCode = "403", description = "Username or password is invalid"),
            @ApiResponse(responseCode = "403", description = "Username is enabled")
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody JwtRequestDto authRequest){
       return  authService.authenticate(authRequest);

    }

    @Operation(
            summary = "Registration",
            description = "Endpoint for customer to register a new account. Requires a body"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "409", description = "The provided username is already taken"),
            @ApiResponse(responseCode = "409", description = "The provided email is already taken")
    })

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<UserResponseDto> register(@Valid @RequestBody UserRequestDto registrationUserDto){
        return  authService.createNewUser(registrationUserDto);}


    @Operation(
            summary = "Refresh the token",
            description = "If the token is expired then it is possible to generate a new access token using refresh token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned a new access token"),
            @ApiResponse(responseCode = "403", description = "Token has expired"),
            @ApiResponse(responseCode = "403", description = "Token not found"),

    })

    @Hidden
    @PostMapping("/refresh_token")
    public ResponseEntity<JwtRefreshTokenDto> refreshToken(@RequestParam("refreshToken") String refreshToken){
         return  authService.refreshToken(refreshToken);

    }

    @Operation(
            summary = "Confirm the email",
            description = "Whenever a user is registered he or she gets email containing link to activate his or her account"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "403", description = "Token has expired"),
            @ApiResponse(responseCode = "403", description = "Token not found")


    })
    @Hidden
    @GetMapping("/confirm_email")
    public ResponseEntity<String> confirm(@RequestParam("token") String token){
        return authService.confirmEmail(token);
    }


    @Operation(
            summary = "Reconfirm the email",
            description = "User can get another link to confirm their email"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email successfully confirmed"),
            @ApiResponse(responseCode = "403", description = "Token has expired"),
            @ApiResponse(responseCode = "403", description = "Token not found")

    })
    @Hidden
    @PostMapping("/re_confirm_email")
    public ResponseEntity<String> reconfirm(@RequestBody ReconfirmEmailDto dto) {
        return  authService.resendConfirmation(dto);
    }

}
