package org.example.cookercorner.dtos;

import java.io.Serializable;

/**
 * DTO for {@link org.example.cookercorner.entities.User}
 */
public record UserDto1(String name, String biography) implements Serializable {
}