package com.project.parkrental.security.Controller;

import com.project.parkrental.security.JwtUtil;
import com.project.parkrental.security.Repository.UserRepository;
import com.project.parkrental.security.Service.SellerService;
import com.project.parkrental.security.Service.UserService;
import com.project.parkrental.security.DTO.User;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("guest")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SellerService sellerService;

    @GetMapping("/Signup")
    public String signup (Model model) {
        model.addAttribute("user", new User());
        return "guest/Signup";
    }

    @PostMapping("/checkUsername")
    @ResponseBody
    public String checkUsername(@RequestParam("username") String username) {
        boolean exist = userService.isUsernameTaken(username) || sellerService.isUsernameTaken(username);
        return exist? "이미 사용중인 아이디입니다.":"사용가능한 아이디입니다.";
    }

    @PostMapping("/checkEmail")
    @ResponseBody
    public String checkEmail(@RequestParam("email") String email) {
        boolean exist = userService.isEmailTaken(email);
        return exist? "이미 사용중인 이메일입니다.":"사용가능한 이메일입니다.";
    }

    @PostMapping("/checkPhoneNum")
    @ResponseBody
    public String checkPhoneNum(@RequestParam("phoneNum") String phoneNum) {
        boolean exist = userService.isPhoneNumTaken(phoneNum);
        return exist? "이미 사용중인 전화번호입니다.":"사용가능한 전화번호입니다.";
    }

    @PostMapping("/Signup")
    public String registerUser(@Valid @ModelAttribute User user, BindingResult result, Model model) {

        if (result.hasErrors()) {
            result.getAllErrors().forEach(violation -> {
                System.out.println("error: " + violation.getDefaultMessage());
            });
            return "guest/Signup";
        }

        if (!user.getPassword().equals(user.getConfirmPassword())) {
            model.addAttribute("passwordError", "비밀번호가 일치하지 않습니다.");
            return "guest/Signup";
        }

        userService.registerNewUser(user);
        return "redirect:/guest/Login";
    }


}
