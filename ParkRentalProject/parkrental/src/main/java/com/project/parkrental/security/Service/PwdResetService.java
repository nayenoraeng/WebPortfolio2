package com.project.parkrental.security.Service;

import com.project.parkrental.security.DTO.PwdResetToken;
import com.project.parkrental.security.DTO.User;
import com.project.parkrental.security.Repository.SellerRepository;
import com.project.parkrental.security.Repository.TokenRepository;
import com.project.parkrental.security.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;


@Service
public class PwdResetService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SellerRepository sellerRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private PasswordEncoder pwdEncoder;
    @Autowired
    private TokenRepository tokenRepository;

    public void sendResetEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user!=null) {
            String resetToken = tokenService.createResetToken(user);
            String resetLink = "http://localhost:8081/reset_password?token=" + resetToken;
            emailService.sendPwdResetEmail(user.getEmail(), "비밀번호 재설정 링크", resetLink);
        } else {
            throw new RuntimeException("해당 이메일로 등록된 사용자가 없습니다.");
        }
    }

    public void resetPwd(String token, String newPwd) {
        PwdResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken !=null && !resetToken.isExpired()) {
            User user = resetToken.getUser();
            user.setPassword(pwdEncoder.encode(newPwd));
            userRepository.save(user);
            tokenRepository.delete(resetToken);
        } else {
            throw new RuntimeException("유효하지 않은 토큰이거나 만료되었습니다.");
        }
    }

    public User validateResetToken(String token) {
        PwdResetToken resetToken = tokenRepository.findByToken(token);

        if (resetToken == null || resetToken.getExpirationDate().before(new Timestamp(System.currentTimeMillis()))) {
            throw new RuntimeException("유효하지 않거나 만료된 토큰입니다.");
        }
        return resetToken.getUser();
    }
}
