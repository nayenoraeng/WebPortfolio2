package com.project.parkrental.security.Repository;

import com.project.parkrental.security.DTO.Seller;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Seller findByUsername(String username);
    Seller findById(long idx);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNum(String phoneNum);
    boolean existsByBusinessNum(String businessNum);
    List<Seller> findAllByAuthority(String authority);
    Seller findByEmail(String email);
}
