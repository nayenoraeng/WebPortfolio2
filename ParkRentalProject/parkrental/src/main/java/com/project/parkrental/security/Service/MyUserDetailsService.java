package com.project.parkrental.security.Service;

import com.project.parkrental.security.DTO.Admin;
import com.project.parkrental.security.DTO.Seller;
import com.project.parkrental.security.Repository.AdminRepository;
import com.project.parkrental.security.DTO.User;
import com.project.parkrental.security.Repository.SellerRepository;
import com.project.parkrental.security.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            System.out.println("user userdetail called");
            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    getAuthorities("ROLE_USER")
            );
        }

        Admin admin = adminRepository.findByUsername(username);

        if (admin != null) {
            System.out.println("admin userdetail called");
            return new org.springframework.security.core.userdetails.User(
                    admin.getUsername(),
                    admin.getPassword(),
                    getAuthorities("ROLE_ADMIN"));
        }

        Seller seller = sellerRepository.findByUsername(username);
        if (seller != null) {
            System.out.println("seller userdetail called");
            return new org.springframework.security.core.userdetails.User(
                    seller.getUsername(),
                    seller.getPassword(),
                    getAuthorities("ROLE_SELLER")
            );
        }

        throw new RuntimeException("User not found with username: " + username);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
