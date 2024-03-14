package org.example.cookercorner.dtos;

/**
 * DTO for {@link com.neobis.neoauth.entities.User}
 */

public record JwtRequestDto(String username, String password) {
}