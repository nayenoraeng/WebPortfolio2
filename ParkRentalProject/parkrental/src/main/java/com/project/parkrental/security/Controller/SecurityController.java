package com.project.parkrental.security.Controller;

import com.project.parkrental.security.DTO.User;
import com.project.parkrental.security.JwtUtil;
import com.project.parkrental.security.Service.PwdResetService;
import com.project.parkrental.security.Service.SellerService;
import com.project.parkrental.security.Service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/guest")
public class SecurityController {
    @Autowired
    private UserService userService;
    @Autowired
    private SellerService sellerService;
    @Autowired
    private AuthenticationManager authManager;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PwdResetService pwdResetService;

    @PostMapping("/Login")
    public ResponseEntity<?> authUser(@RequestParam("username") String username, @RequestParam("password") String password,
                                      HttpServletRequest req, HttpServletResponse res) {
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwtToken = jwtUtil.generateToken(username);
            Cookie cookie = new Cookie("JWT", jwtToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Only set to true if using HTTPS
            cookie.setPath("/");
            res.addCookie(cookie);

            return ResponseEntity.ok().body("{\"success\": true, \"message\": \"로그인되었습니다.\"}");
        } catch (AuthenticationException e) {
            return ResponseEntity.ok().body("{\"success\": false, \"message\": \"로그인 실패. 아이디와 비밀번호를 확인해주세요.\"}");
        }
    }

    @GetMapping("/Logout")
    public String logout (HttpServletResponse res) {
        SecurityContextHolder.clearContext();
        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        res.addCookie(cookie);

        return "redirect:/";
    }

    @GetMapping("/findIdPw")
    public String findpage() {
        return "guest/findIdPw";
    }

    @GetMapping("/findId")
    public String findIdPage() {
        return "guest/findId";
    }

    @PostMapping("/findId")
    public String findUsername(@RequestParam("email") String email, Model model) {
        String username = userService.findUsernameByEmail(email);
        if (username==null) {
            username = sellerService.findUsernameByEmail(email);
            if (username==null) {
                model.addAttribute("error", "해당 이메일로 등록된 아이디가 없습니다.");
            } else {
                model.addAttribute("message", "아이디는 " + username + "입니다.");
            }
        } else {
            model.addAttribute("message", "아이디는 " + username + "입니다.");
        }
        return "guest/findId";
    }

    @GetMapping("/sendResetLink")
    public String findPwPage() {
        return "guest/sendResetLink";
    }

    @PostMapping("/SendResetEmail")
    public String sendResetLink(@RequestParam("email") String email, Model model) {
        try {
            pwdResetService.sendResetEmail(email);
            model.addAttribute("message", "비밀번호 재설정 링크가 이메일로 전송되었습니다.");
        } catch (Exception e) {
            model.addAttribute("error", "해당 이메일로 등록된 계정이 없습니다.");
        }
        return "guest/sendResetLink";
    }

    @GetMapping("/resetPwd")
    public String resetPwdForm(@PathVariable("token") String token, Model model) {
        User user = pwdResetService.validateResetToken(token);
        if (user == null) {
            model.addAttribute("error", "유효하지 않은 토큰입니다.");
            return "guest/resetPwd";
        }
        model.addAttribute("email", user);
        return "guest/resetPwd";
    }

    @PostMapping("/resetPwd")
    public String resetPwd(@RequestParam("token") String token,
                           @RequestParam("newPwd") String newPwd,
                           Model model) {
        try {
            pwdResetService.resetPwd(token, newPwd);
            model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");

        } catch (Exception e) {
            model.addAttribute("error", "비밀번호 변경에 실패했습니다");
        }
        return "guest/resetPwd";
    }

}
