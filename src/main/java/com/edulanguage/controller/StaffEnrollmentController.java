package com.edulanguage.controller;

import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Student;
import com.edulanguage.service.EnrollmentService;
import com.edulanguage.service.StudentService;
import com.edulanguage.dao.ClazzDao;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.edulanguage.entity.enums.Status;

import java.util.List;

@Controller
@RequestMapping("/staff/enrollments")
public class StaffEnrollmentController {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final ClazzDao clazzDao;

    public StaffEnrollmentController(EnrollmentService enrollmentService, 
                                     StudentService studentService, 
                                     ClazzDao clazzDao) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.clazzDao = clazzDao;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "staff-enrollments");
        model.addAttribute("enrollments", enrollmentService.findAll());
        return "staff/enrollments/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("currentPage", "staff-enrollments");
        List<Student> students = studentService.findAll();
        // Lọc các lớp ACTIVE (sắp khai giảng / đang mở)
        List<Clazz> activeClasses = clazzDao.findByStatus(Status.ACTIVE);
        
        model.addAttribute("students", students);
        model.addAttribute("classes", activeClasses);
        
        return "staff/enrollments/form";
    }

    @PostMapping("/save")
    public String enrollStudent(@RequestParam Long studentId, 
                                @RequestParam Long classId, 
                                RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.enrollStudent(studentId, classId);
            redirectAttributes.addFlashAttribute("message", "Ghi danh thành công. Hóa đơn đã được tạo và ở trạng thái CHƯA THANH TOÁN.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi ghi danh: " + e.getMessage());
            return "redirect:/staff/enrollments/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            return "redirect:/staff/enrollments/new";
        }
        return "redirect:/staff/enrollments";
    }
    
    @PostMapping("/cancel/{id}")
    public String cancelEnrollment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.cancelEnrollment(id);
            redirectAttributes.addFlashAttribute("message", "Đã hủy ghi danh thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/staff/enrollments";
    }
}
