package com.project.parkrental.security.Service;

import com.project.parkrental.security.DTO.Seller;
import com.project.parkrental.security.DTO.SellerDto;
import com.project.parkrental.security.Repository.SellerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SellerService {
    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerNewUser (Seller seller) {
        seller.setPassword(passwordEncoder.encode(seller.getPassword()));
        seller.setAuthority("ROLE_SELLER");
        System.out.println("seller register method called");
        sellerRepository.save(seller);
    }

    public boolean isBusinessNumTaken (String businessNum) {
        return sellerRepository.existsByBusinessNum(businessNum);
    }

    public boolean isUsernameTaken (String username) {
        return sellerRepository.existsByUsername(username);
    }

    public Seller findByUsername(String username) {
        return sellerRepository.findByUsername(username);
    }

    public Boolean isSellerPhoneNumTaken (String phoneNum) {
        return sellerRepository.existsByPhoneNum(phoneNum);
    }

    public Boolean isSellerEmailTaken (String email) {
        return sellerRepository.existsByEmail(email);
    }

    public SellerDto getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("사용자가 인증되지 않았습니다.");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Seller seller = sellerRepository.findByUsername(username);

        return new SellerDto(seller);
    }

    public void updateUser(Seller seller) {
        sellerRepository.save(seller);
    }

    public List<SellerDto> getAllSellers() {
        List<Seller> sellers = sellerRepository.findAllByAuthority("ROLE_SELLER");

        return sellers.stream()
                .map(seller -> new SellerDto(
                        seller.getIdx(),
                        seller.getBusinessNum(),
                        seller.getUsername(),
                        seller.getName(),
                        seller.getBusinessName(),
                        seller.getPhoneNum(),
                        seller.getEmail(),
                        seller.getPostcode(),
                        seller.getAddress(),
                        seller.getDetailAddress(),
                        seller.getRegidate(),
                        seller.getAuthority(),
                        seller.getEnabled(),
                        seller.getProvider(),
                        seller.getProviderId(),
                        seller.getIsLocked(),
                        seller.getFailCount(),
                        seller.getLockTimes()
                        ))
                .collect(Collectors.toList());
    }

    public SellerDto findSellerById(long idx) {
        Seller seller = sellerRepository.findById(idx);
        return new SellerDto(seller);
    }

    public String findUsernameByEmail(String email) {
        Seller seller = sellerRepository.findByEmail(email);
        return seller.getUsername();
    }

}
