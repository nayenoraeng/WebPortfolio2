package com.project.parkrental.security.Repository;

import com.project.parkrental.security.DTO.PwdResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<PwdResetToken, Long> {
    PwdResetToken findByToken(String token);
}
