package com.ssh.password_practice.controller;

import com.ssh.password_practice.model.User;
import com.ssh.password_practice.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;

    }

    @GetMapping("/")
    public String home() {
        return "home";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {

        try {
        User user = userService.registerUser(username, password, email);

            redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해주세요");
        redirectAttributes.addFlashAttribute("messageType", "success");

//        flash : 일회용 속성 추가

        return "redirect:/login";
    } catch(IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", e.getMessage());
        redirectAttributes.addFlashAttribute("username", username);
        redirectAttributes.addFlashAttribute("email", email);

        return "redirect:/register";
    }
}

    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }

    @GetMapping("/password-test/form")
    public String passwordTestForm() {
        return "password-test";
    }


}
