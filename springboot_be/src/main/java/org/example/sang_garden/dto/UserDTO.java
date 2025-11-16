package org.example.sang_garden.dto;

import org.example.sang_garden.util.UserRole;

public record UserDTO(String username, String email, UserRole userRole, boolean isPasswordLoginAllowed, boolean isEmailVerified) {
}
