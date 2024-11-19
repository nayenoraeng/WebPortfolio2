package com.project.parkrental.security.Service;

import com.project.parkrental.security.DTO.Admin;
import com.project.parkrental.security.Repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void updateAdminPassword(String adminName, String newPassword) {
        Admin admin = adminRepository.findByUsername(adminName);
        String encondedPwd = passwordEncoder.encode(newPassword);
        admin.setPassword(encondedPwd);
        adminRepository.save(admin);
    }
}
