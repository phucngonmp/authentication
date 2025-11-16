package org.example.sang_garden.service;

import org.example.sang_garden.dto.UserDTO;
import org.example.sang_garden.dto.request.RegisterRequest;
import org.example.sang_garden.entity.User;
import org.example.sang_garden.mapper.Mapper;
import org.example.sang_garden.repository.UserRepository;
import org.example.sang_garden.util.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Mapper mapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, Mapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public User saveUser(String email){
        User user = User.builder()
                .email(email)
                .userRole(UserRole.USER)
                .password(null)
                .emailVerified(false)
                .passwordLoginAllowed(false)
                .build();
        return userRepository.save(user);
    }

    public User saveUser(RegisterRequest registerRequest) {
        if (isUsernameExists(registerRequest.username())) {
            throw new RuntimeException("tên tài khoản đã tồn tại");
        }
        if(isEmailExists(registerRequest.email())) {
            throw new RuntimeException("email đã tồn tại");
        }
        User user = User.builder()
                .username(registerRequest.username())
                .password(passwordEncoder.encode(registerRequest.password()))
                .email(registerRequest.email())
                .userRole(UserRole.USER)
                .emailVerified(false)
                .passwordLoginAllowed(true)
                .build();
        return userRepository.save(user);
    }

    public UserDTO getUserDTOByIdentifier(String identifier) {
        if(isEmail(identifier)){
            return this.mapper.toUserDTO(userRepository.findByEmail(identifier).orElse(null));
        }
        return this.mapper.toUserDTO(userRepository.findByUsername(identifier).orElse(null));
    }

    public User getUserByEmail(String email) {
        return this.userRepository.findByEmail(email).orElse(null);
    }

    private boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    private boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    private boolean isEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
