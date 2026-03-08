package com.edulanguage.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

/**
 * Trang chủ sau khi đăng nhập, hiển thị theo vai trò (role).
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Authentication auth, Model model) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        String username = auth.getName();
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.replace("ROLE_", ""))
                .collect(Collectors.joining(", "));

        model.addAttribute("username", username);
        model.addAttribute("role", role);
        model.addAttribute("isAdmin", role.contains("ADMIN"));
        model.addAttribute("isTeacher", role.contains("TEACHER"));
        model.addAttribute("isStudent", role.contains("STUDENT"));
        model.addAttribute("isStaff", role.contains("STAFF"));
        return "home";
    }
}
