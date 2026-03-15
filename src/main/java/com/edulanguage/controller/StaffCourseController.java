package com.edulanguage.controller;

import com.edulanguage.entity.Course;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/staff/courses")
public class StaffCourseController {

    private final CourseService courseService;

    public StaffCourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String listCourses(Model model) {
        model.addAttribute("currentPage", "staff-courses");
        model.addAttribute("courses", courseService.findAll());
        return "staff/courses/list";
    }

    @GetMapping("/new")
    public String newCourseForm(Model model) {
        model.addAttribute("currentPage", "staff-courses");
        model.addAttribute("course", new Course());
        model.addAttribute("statusValues", Status.values());
        return "staff/courses/form";
    }

    @GetMapping("/edit/{id}")
    public String editCourseForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Course> courseOpt = courseService.findById(id);
        if (courseOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy khóa học này.");
            return "redirect:/staff/courses";
        }
        model.addAttribute("currentPage", "staff-courses");
        model.addAttribute("course", courseOpt.get());
        model.addAttribute("statusValues", Status.values());
        return "staff/courses/form";
    }

    @PostMapping("/save")
    public String saveCourse(@ModelAttribute Course course, RedirectAttributes redirectAttributes) {
        try {
            // Fix createdAt for updating existing courses (mapped super class limitation handled here simply)
            if (course.getId() != null) {
                Optional<Course> existing = courseService.findById(course.getId());
                existing.ifPresent(c -> {
                    course.setCreatedAt(c.getCreatedAt());
                    // Keep original properties that shouldn't change
                });
            }
            
            courseService.save(course);
            redirectAttributes.addFlashAttribute("successMsg", "Khóa học đã được lưu thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra khi lưu: " + e.getMessage());
        }
        return "redirect:/staff/courses";
    }

    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            courseService.delete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa khóa học thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không thể xóa. Cần xóa các lớp liên kết trước. Chi tiết: " + e.getMessage());
        }
        return "redirect:/staff/courses";
    }
}
