package org.example.cookercorner.dtos;


import lombok.Builder;

/**
 * DTO for {@link com.neobis.neoauth.entities.User}
 */
@Builder
public record JwtResponseDto(String username, String accessToken, String refreshToken){
}