package com.project.parkrental.security;

import com.project.parkrental.security.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PwdEncorder implements CommandLineRunner {
    private final AdminService adminService;

    @Autowired
    public PwdEncorder(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    public void run (String[] args) throws Exception {
//        adminService.updateAdminPassword("admin", "Qwer1234!");
    }
}
