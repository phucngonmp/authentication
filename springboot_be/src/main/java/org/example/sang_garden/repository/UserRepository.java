package org.example.sang_garden.repository;

import jakarta.validation.constraints.Email;
import org.example.sang_garden.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(@Email(message = "invalid email") String email);

    Optional<User> findByEmail(String email);

}
