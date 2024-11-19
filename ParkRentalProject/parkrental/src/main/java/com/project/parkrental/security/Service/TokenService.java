package com.project.parkrental.security.Service;

import com.project.parkrental.security.DTO.PwdResetToken;
import com.project.parkrental.security.DTO.User;
import com.project.parkrental.security.Repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

@Service
public class TokenService {
    @Autowired
    private TokenRepository tokenRepository;

    public String createResetToken(User user) {
        String resetToken = UUID.randomUUID().toString();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        PwdResetToken token = new PwdResetToken();
        token.setUser(user);
        token.setToken(resetToken);
        token.setExpirationDate(new Timestamp(now.getTime() + 30*60*1000)); //만료시간 30분

        tokenRepository.save(token);

        return resetToken;
    }
}
