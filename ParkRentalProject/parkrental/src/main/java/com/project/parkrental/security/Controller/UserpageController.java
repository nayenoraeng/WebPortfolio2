package com.project.parkrental.security.Controller;

import com.google.rpc.context.AttributeContext;
import com.project.parkrental.security.DTO.Seller;
import com.project.parkrental.security.DTO.SellerDto;
import com.project.parkrental.security.Repository.SellerRepository;
import com.project.parkrental.security.Repository.UserRepository;
import com.project.parkrental.security.Service.SellerService;
import com.project.parkrental.security.Service.UserService;
import com.project.parkrental.security.DTO.User;
import com.project.parkrental.security.DTO.UserDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping("/user")
public class UserpageController {

    @Autowired
    private SellerService sellerService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @Autowired
    private PasswordEncoder pwdEncoder;



    @GetMapping("/userpage")
    public String userpage(Model model){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_USER")) {
            UserDto user = userService.getUserDetails();
            model.addAttribute("user", user);
        }
        if (role.equals("ROLE_SELLER")) {
            SellerDto user = sellerService.getUserDetails();
            model.addAttribute("user", user);
        }
        return "user/userpage";
    }

    @GetMapping("/userpageEdit")
    public String userpageEdit(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_USER")) {
            UserDto user = userService.getUserDetails();
            model.addAttribute("user", user);
        }
        if (role.equals("ROLE_SELLER")) {
            SellerDto user = sellerService.getUserDetails();
            model.addAttribute("user", user);
        }
        return "user/userpageEdit";
    }

    @PostMapping("/userpageEdit")
    public String editUser(@ModelAttribute("user") UserDto userDto, @ModelAttribute("seller") SellerDto sellerDto,
                           BindingResult result, Principal principal) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (principal == null) {
            System.out.println("principal is null");
        }
        if (result.hasErrors()) {
            return "user/userpageEdit";
        }
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if (role.equals("ROLE_USER")) {
            User existingUser = userService.findByUsername(username);
            if (existingUser == null) {
                throw new RuntimeException("로그인된 사용자를 찾을 수 없습니다.");
            }

            existingUser.setName(userDto.getName());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setPhoneNum(userDto.getPhoneNum());
            existingUser.setPostcode(userDto.getPostcode());
            existingUser.setAddress(userDto.getAddress());
            existingUser.setDetailAddress(userDto.getDetailAddress());

            userService.updateUser(existingUser);
        }
        if (role.equals("ROLE_SELLER")) {
            Seller existingSeller = sellerService.findByUsername(username);
            if (existingSeller == null) {
                throw new RuntimeException("로그인된 사용자를 찾을 수 없습니다.");
            }

            existingSeller.setName(sellerDto.getName());
            existingSeller.setEmail(sellerDto.getEmail());
            existingSeller.setPhoneNum(sellerDto.getPhoneNum());
            existingSeller.setPostcode(sellerDto.getPostcode());
            existingSeller.setAddress(sellerDto.getAddress());
            existingSeller.setDetailAddress(sellerDto.getDetailAddress());

            sellerService.updateUser(existingSeller);
        }

        return "redirect:/user/userpage";
    }

    @GetMapping("/userpagePwd")
    public String changePwd () {
        return "user/userpagePwd";
    }

    @PostMapping("/userpagePwd")
    public ResponseEntity<?> changePwd(@RequestParam("oldPwd") String oldPwd, @RequestParam("newPwd") String newPwd) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String pattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

        if (!newPwd.matches(pattern)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"비밀번호는 영문 대소문자, 숫자, 특수문자가 포함되어야 합니다. \"}");
        }

        if (role.equals("ROLE_USER")) {
            User user = userRepository.findByUsername(username);

            if (!pwdEncoder.matches(oldPwd, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"현재 비밀번호와 일치하지 않습니다.\"}");
            }

            user.setPassword(pwdEncoder.encode(newPwd));
            userRepository.save(user);
        }
        if (role.equals("ROLE_SELLER")) {
            Seller seller = sellerRepository.findByUsername(username);

            if (!pwdEncoder.matches(oldPwd, seller.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"현재 비밀번호와 일치하지 않습니다.\"}");
            }

            seller.setPassword(pwdEncoder.encode(newPwd));
            sellerRepository.save(seller);
        }


        return ResponseEntity.ok("{\"message\": \"비밀번호 변경이 완료되었습니다\"}");
    }

    @PostMapping("/deleteAccount")
    public ResponseEntity<?> deleteAccount(HttpServletRequest req, HttpServletResponse res) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();

        if (role.equals("ROLE_USER")) {
            User user = userRepository.findByUsername(username);
            if(user != null) {
                userRepository.delete(user);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"사용자를 찾을 수 없습니다.\"}");
            }
        } else if (role.equals("ROLE_SELLER")) {
            Seller seller = sellerRepository.findByUsername(username);
            if (seller != null) {
                sellerRepository.delete(seller);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"message\": \"판매자를 찾을 수 없습니다.\"}");
            }
        }
        SecurityContextHolder.clearContext();
        req.getSession().invalidate();

        Cookie cookie = new Cookie("JWT", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 쿠키 삭제를 위해 Max-Age를 0으로 설정
        res.addCookie(cookie);

        return ResponseEntity.ok("{\"message\": \"성공적으로 회원 탈퇴가 완료되었습니다.\"}");
    }
}
