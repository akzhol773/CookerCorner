package org.example.cookercorner.dtos;

import lombok.Builder;



@Builder
public record UserResponseDto(String status, String username) {
}