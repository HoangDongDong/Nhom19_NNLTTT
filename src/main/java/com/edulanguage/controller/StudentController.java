package com.edulanguage.controller;

import com.edulanguage.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Tầng Presentation: chỉ gọi StudentService (tầng nghiệp vụ), không gọi Repository.
 */
@Controller
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/student")
    public String studentPage(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "student";
    }
}
