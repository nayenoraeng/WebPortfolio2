package com.project.parkrental.security.Repository;

import com.project.parkrental.security.DTO.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
}
