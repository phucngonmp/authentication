package org.example.sang_garden.mapper;

import org.example.sang_garden.dto.UserDTO;
import org.example.sang_garden.entity.User;
import org.springframework.stereotype.Component;

@Component
public class Mapper {
    public UserDTO toUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getUserRole(),
                user.isPasswordLoginAllowed(),
                user.isEmailVerified()
        );
    }
}
