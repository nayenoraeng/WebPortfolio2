package com.project.parkrental.security.Repository;

import com.project.parkrental.security.DTO.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findById(long idx);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNum(String phoneNum);
    List<User> findAllByAuthority(String authority);
    User findByEmail(String email);

}
