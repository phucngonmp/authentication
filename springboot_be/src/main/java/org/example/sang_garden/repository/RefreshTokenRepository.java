package org.example.sang_garden.repository;

import org.example.sang_garden.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Override
    void deleteById(UUID tokenId);
}
