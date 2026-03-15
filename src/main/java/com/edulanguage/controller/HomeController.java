package com.edulanguage.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Trang chủ: sau khi đăng nhập, tự động chuyển hướng đến Dashboard phù hợp với vai trò.
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        String role = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(a -> a.startsWith("ROLE_"))
                .findFirst().orElse("");

        if (role.contains("ADMIN"))   return "redirect:/admin/dashboard";
        if (role.contains("STAFF"))   return "redirect:/staff/dashboard";
        if (role.contains("TEACHER")) return "redirect:/teacher/dashboard";
        if (role.contains("STUDENT")) return "redirect:/student/dashboard";
        return "redirect:/login";
    }
}
