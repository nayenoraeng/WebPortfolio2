package com.project.parkrental.security.Service;

import com.project.parkrental.security.DTO.User;
import com.project.parkrental.security.DTO.UserDto;
import com.project.parkrental.security.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ResponseEntity<?> registerNewUser (User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 동일한 이름의 사용자가 있습니다. 로그인 해 주세요.");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 동일한 이름의 사용자가 있습니다. 로그인 해 주세요.");
        }
        if (userRepository.existsByPhoneNum(user.getPhoneNum())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("이미 동일한 이름의 사용자가 있습니다. 로그인 해 주세요.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setAuthority("ROLE_USER");
        System.out.println("save method called");
        userRepository.save(user);
        return ResponseEntity.ok("성공적으로 회원가입 되셨습니다.");
    }

    public boolean isUsernameTaken (String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    public  boolean isPhoneNumTaken(String phoneNum) {
        return userRepository.existsByPhoneNum(phoneNum);
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public UserDto getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("사용자가 인증되지 않았습니다.");
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username);

        return new UserDto(user);
    }

    public void updateUser(User user) {
        userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAllByAuthority("ROLE_USER");
        return users.stream()
                .map(user -> new UserDto(
                        user.getIdx(),
                        user.getUsername(),
                        user.getName(),
                        user.getPhoneNum(),
                        user.getEmail(),
                        user.getPostcode(),
                        user.getAddress(),
                        user.getDetailAddress(),
                        user.getRegidate(),
                        user.getAuthority(),
                        user.getEnabled(),
                        user.getProvider(),
                        user.getProviderId(),
                        user.getIsLocked(),
                        user.getFailCount(),
                        user.getLockTimes()
                ))
                .collect(Collectors.toList());
    }

    public UserDto findUserById(long idx) {
        User user = userRepository.findById(idx);
        return new UserDto(user);
    }

    public String findUsernameByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user.getUsername();
    }
}
