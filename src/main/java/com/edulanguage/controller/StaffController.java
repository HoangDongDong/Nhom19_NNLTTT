package com.edulanguage.controller;

import com.edulanguage.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Dashboard dành cho STAFF (nhân viên tư vấn/vận hành).
 */
@Controller
@RequestMapping("/staff")
public class StaffController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final FinanceService financeService;

    public StaffController(StudentService studentService,
                           EnrollmentService enrollmentService,
                           FinanceService financeService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
        this.financeService = financeService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String staffDashboard(Model model) {
        model.addAttribute("currentPage", "staff-dashboard");
        model.addAttribute("totalStudents", studentService.findAll().size());
        model.addAttribute("totalEnrollments", enrollmentService.findAll().size());
        model.addAttribute("pendingInvoices", financeService.findAllInvoices().stream()
                .filter(inv -> "UNPAID".equals(inv.getStatus())).count());
        return "staff/dashboard";
    }

}
