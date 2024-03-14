package org.example.cookercorner.dtos;

import lombok.Builder;

@Builder
public record JwtRefreshTokenDto(String username, String newAccessToken) {
}
