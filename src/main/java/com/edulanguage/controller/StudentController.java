package com.edulanguage.controller;

import com.edulanguage.entity.Student;
import com.edulanguage.entity.enums.Gender;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.PlacementTestService;
import com.edulanguage.service.StudentService;
import com.edulanguage.service.UserAccountService;
import com.edulanguage.entity.enums.Role;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Tầng Presentation: CRUD Học viên + Placement Test (Web MVC).
 * Gọi StudentService & PlacementTestService (tầng nghiệp vụ), không gọi Repository.
 */
@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final PlacementTestService placementTestService;
    private final UserAccountService userAccountService;

    public StudentController(StudentService studentService, PlacementTestService placementTestService, UserAccountService userAccountService) {
        this.studentService = studentService;
        this.placementTestService = placementTestService;
        this.userAccountService = userAccountService;
    }

    /* ===== CRUD Học viên ===== */

    @GetMapping
    public String list(Model model) {
        model.addAttribute("students", studentService.findAll());
        return "students/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("genderList", Gender.values());
        model.addAttribute("statusList", Status.values());
        return "students/form";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return studentService.findById(id)
                .map(s -> {
                    model.addAttribute("student", s);
                    model.addAttribute("genderList", Gender.values());
                    model.addAttribute("statusList", Status.values());
                    
                    userAccountService.findByRoleAndRelatedId(Role.STUDENT, s.getId())
                            .ifPresent(account -> model.addAttribute("currentUsername", account.getUsername()));
                            
                    return "students/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy học viên.");
                    return "redirect:/students";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("student") Student student,
                       @RequestParam(value = "username", required = false) String username,
                       @RequestParam(value = "password", required = false) String password,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        // Kiểm tra trùng email
        if (student.getEmail() != null && !student.getEmail().isBlank()) {
            boolean emailExists = student.getId() == null
                    ? studentService.existsByEmail(student.getEmail())
                    : studentService.existsByEmailExcluding(student.getEmail(), student.getId());
            if (emailExists) {
                result.rejectValue("email", "duplicate", "Email đã tồn tại.");
            }
        }

        // Kiểm tra trùng phone
        if (student.getPhone() != null && !student.getPhone().isBlank()) {
            boolean phoneExists = student.getId() == null
                    ? studentService.existsByPhone(student.getPhone())
                    : studentService.existsByPhoneExcluding(student.getPhone(), student.getId());
            if (phoneExists) {
                result.rejectValue("phone", "duplicate", "Số điện thoại đã tồn tại.");
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("genderList", Gender.values());
            model.addAttribute("statusList", Status.values());
            return "students/form";
        }

        // Gán mặc định cho bản ghi mới
        if (student.getId() == null) {
            if (student.getStatus() == null) student.setStatus(Status.ACTIVE);
            if (student.getRegistrationDate() == null) student.setRegistrationDate(LocalDateTime.now());
        }

        Student savedStudent = studentService.save(student);
        
        try {
            userAccountService.createOrUpdateAccount(Role.STUDENT, savedStudent.getId(), username, password);
            redirect.addFlashAttribute("message", "Đã lưu học viên và tài khoản đăng nhập thành công.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", "Đã lưu học viên nhưng cấu hình tài khoản thất bại: " + e.getMessage());
        }
        
        return "redirect:/students";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            // Xóa tài khoản liên quan trước để tránh vi phạm FK
            userAccountService.deleteByRoleAndRelatedId(Role.STUDENT, id);
            studentService.deleteById(id);
            redirect.addFlashAttribute("message", "Đã xóa học viên và tài khoản liên quan.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/students";
    }

    /* ===== Placement Test ===== */

    @GetMapping("/placement-test/{studentId}")
    public String placementTestForm(@PathVariable Long studentId, Model model, RedirectAttributes redirect) {
        return studentService.findById(studentId)
                .map(s -> {
                    model.addAttribute("student", s);
                    placementTestService.getLatestResult(studentId)
                            .ifPresent(r -> {
                                model.addAttribute("lastResult", r);
                                model.addAttribute("lastLevel", placementTestService.determineLevel(r.getGrade()));
                            });
                    return "students/placement-test";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy học viên.");
                    return "redirect:/students";
                });
    }

    @PostMapping("/placement-test/{studentId}")
    public String submitPlacementTest(@PathVariable Long studentId,
                                      @RequestParam BigDecimal score,
                                      @RequestParam(required = false) String comment,
                                      RedirectAttributes redirect) {
        try {
            placementTestService.submitTest(studentId, score, comment);
            redirect.addFlashAttribute("message", "Đã lưu kết quả test đầu vào.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/students/placement-test/" + studentId;
    }
}
