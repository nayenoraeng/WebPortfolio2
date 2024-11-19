package com.project.parkrental.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/guest")
public class LoginController {

    @GetMapping("/Login")
    public String login() {
        return "guest/Login";
    }

}
