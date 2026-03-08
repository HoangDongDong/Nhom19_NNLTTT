package com.edulanguage.controller;

import com.edulanguage.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Tầng Presentation: chỉ gọi CourseService (tầng nghiệp vụ).
 */
@Controller
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public String listCourses(Model model) {
        model.addAttribute("courses", courseService.findAll());
        return "courses";
    }
}
