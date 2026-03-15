package com.edulanguage.controller;

import com.edulanguage.entity.Invoice;
import com.edulanguage.service.*;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Dashboard tổng quát dành cho ADMIN.
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final FinanceService financeService;
    private final CourseService courseService;

    public AdminController(StudentService studentService,
                           EnrollmentService enrollmentService,
                           FinanceService financeService,
                           CourseService courseService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
        this.financeService = financeService;
        this.courseService = courseService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String adminDashboard(Model model) {
        model.addAttribute("currentPage", "admin-dashboard");
        model.addAttribute("totalStudents", studentService.findAll().size());
        model.addAttribute("totalEnrollments", enrollmentService.findAll().size());
        model.addAttribute("totalInvoices", financeService.findAllInvoices().size());
        model.addAttribute("totalCourses", courseService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("currentPage", "admin-reports");
        List<Invoice> invoices = financeService.findAllInvoices();
        long paidCount = invoices.stream().filter(i -> "PAID".equals(i.getStatus())).count();
        long unpaidCount = invoices.stream().filter(i -> "UNPAID".equals(i.getStatus())).count();
        model.addAttribute("invoices", invoices);
        model.addAttribute("paidCount", paidCount);
        model.addAttribute("unpaidCount", unpaidCount);
        return "admin/reports";
    }

    @GetMapping("/teachers")
    public String teachers(Model model) {
        model.addAttribute("currentPage", "admin-teachers");
        return "error/under_construction";
    }
}
