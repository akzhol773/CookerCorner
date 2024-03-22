package org.example.cookercorner.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.example.cookercorner.entities.User}
 */
public record UserDto(Long id, String name, String photoUrl) implements Serializable {
}