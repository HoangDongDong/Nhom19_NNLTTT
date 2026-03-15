package com.edulanguage.controller;

import com.edulanguage.entity.Teacher;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.TeacherService;
import com.edulanguage.service.UserAccountService;
import com.edulanguage.entity.enums.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/staff/teachers")
public class StaffTeacherController {

    private final TeacherService teacherService;
    private final UserAccountService userAccountService;

    public StaffTeacherController(TeacherService teacherService, UserAccountService userAccountService) {
        this.teacherService = teacherService;
        this.userAccountService = userAccountService;
    }

    @GetMapping
    public String listTeachers(Model model) {
        model.addAttribute("currentPage", "staff-teachers");
        model.addAttribute("teachers", teacherService.findAll());
        return "staff/teachers/list";
    }

    @GetMapping("/new")
    public String newTeacherForm(Model model) {
        model.addAttribute("currentPage", "staff-teachers");
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("statusValues", Status.values());
        return "staff/teachers/form";
    }

    @GetMapping("/edit/{id}")
    public String editTeacherForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Teacher> teacherOpt = teacherService.findById(id);
        if (teacherOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không tìm thấy giáo viên này.");
            return "redirect:/staff/teachers";
        }
        model.addAttribute("currentPage", "staff-teachers");
        model.addAttribute("teacher", teacherOpt.get());
        model.addAttribute("statusValues", Status.values());
        
        userAccountService.findByRoleAndRelatedId(Role.TEACHER, id)
                .ifPresent(account -> model.addAttribute("currentUsername", account.getUsername()));
                
        return "staff/teachers/form";
    }

    @PostMapping("/save")
    public String saveTeacher(@ModelAttribute Teacher teacher, 
                              @RequestParam(value = "username", required = false) String username,
                              @RequestParam(value = "password", required = false) String password,
                              RedirectAttributes redirectAttributes) {
        try {
            if (teacher.getId() != null) {
                Optional<Teacher> existing = teacherService.findById(teacher.getId());
                existing.ifPresent(t -> {
                    teacher.setCreatedAt(t.getCreatedAt());
                    // Copy existing classes connection if not submitted in form
                    teacher.setClasses(t.getClasses());
                });
            }
            Teacher savedTeacher = teacherService.save(teacher);
            
            try {
                userAccountService.createOrUpdateAccount(Role.TEACHER, savedTeacher.getId(), username, password);
                redirectAttributes.addFlashAttribute("successMsg", "Thông tin giáo viên và tài khoản cấu hình đã được lưu thành công.");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Đã lưu giáo viên nhưng cấu hình tài khoản thất bại: " + e.getMessage());
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lưu thất bại: " + e.getMessage());
        }
        return "redirect:/staff/teachers";
    }

    @GetMapping("/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Xóa tài khoản liên quan trước để tránh vi phạm FK
            userAccountService.deleteByRoleAndRelatedId(Role.TEACHER, id);
            teacherService.delete(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa giáo viên và tài khoản liên quan thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Không thể xóa do vướng điều kiện ràng buộc dữ liệu (Giáo viên đang dạy lớp học).");
        }
        return "redirect:/staff/teachers";
    }
}
