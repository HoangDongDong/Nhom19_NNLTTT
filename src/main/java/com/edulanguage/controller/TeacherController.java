package com.edulanguage.controller;

import com.edulanguage.service.TeacherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Tầng Presentation: chỉ gọi TeacherService (tầng nghiệp vụ), không gọi Repository.
 */
@Controller
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @GetMapping("/teacher")
    public String teacherPage(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "teacher";
    }
}
