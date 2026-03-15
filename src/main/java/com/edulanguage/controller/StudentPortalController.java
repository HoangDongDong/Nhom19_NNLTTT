package com.edulanguage.controller;

import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.Result;
import com.edulanguage.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.enums.PaymentMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Portal cá nhân dành cho Học viên (role = STUDENT).
 */
@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private final UserAccountService userAccountService;
    private final StudentService studentService;
    private final PlacementTestService placementTestService;
    private final EnrollmentService enrollmentService;
    private final FinanceService financeService;
    private final PromoService promoService;

    public StudentPortalController(UserAccountService userAccountService,
                                   StudentService studentService,
                                   PlacementTestService placementTestService,
                                   EnrollmentService enrollmentService,
                                   FinanceService financeService,
                                   PromoService promoService) {
        this.userAccountService = userAccountService;
        this.studentService = studentService;
        this.placementTestService = placementTestService;
        this.enrollmentService = enrollmentService;
        this.financeService = financeService;
        this.promoService = promoService;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("currentPage", "student-dashboard");
        resolveStudent(auth, model);
        return "student/dashboard";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model) {
        model.addAttribute("currentPage", "student-schedule");
        resolveStudent(auth, model);
        return "student/schedule";
    }

    @GetMapping("/results")
    public String results(Authentication auth, Model model) {
        model.addAttribute("currentPage", "student-results");
        resolveStudent(auth, model);
        return "student/results";
    }

    @GetMapping("/invoices")
    public String invoices(Authentication auth, Model model) {
        model.addAttribute("currentPage", "student-invoices");
        resolveStudent(auth, model);
        return "student/invoices";
    }

    @GetMapping("/invoices/payment/{id}")
    public String paymentForm(@PathVariable Long id, Authentication auth, Model model, RedirectAttributes redirectAttributes) {
        Optional<UserAccount> acctOpt = userAccountService.findByUsername(auth.getName());
        if (acctOpt.isEmpty() || acctOpt.get().getRelatedId() == null) {
            return "redirect:/student/invoices";
        }
        Long studentId = acctOpt.get().getRelatedId();

        Optional<Invoice> optInvoice = financeService.findInvoiceById(id);
        if (optInvoice.isEmpty() || !optInvoice.get().getStudent().getId().equals(studentId)) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn của bạn.");
            return "redirect:/student/invoices";
        }

        Invoice invoice = optInvoice.get();
        if ("PAID".equals(invoice.getStatus())) {
            redirectAttributes.addFlashAttribute("message", "Hóa đơn này đã được thanh toán.");
            return "redirect:/student/invoices";
        }
        if ("PENDING_CONFIRM".equals(invoice.getStatus())) {
            redirectAttributes.addFlashAttribute("message", "Hóa đơn đang chờ trung tâm xác nhận.");
            return "redirect:/student/invoices";
        }

        model.addAttribute("currentPage", "student-invoices");
        model.addAttribute("invoice", invoice);
        model.addAttribute("paymentMethods", PaymentMethod.values());
        model.addAttribute("promoCodes", promoService.findActivePromos());
        resolveStudent(auth, model);
        return "student/payment";
    }

    @PostMapping("/invoices/payment/process")
    public String processPayment(@RequestParam Long invoiceId,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam String paymentMethod,
                                 @RequestParam(required = false) String discountCode,
                                 RedirectAttributes redirectAttributes) {
        try {
            PaymentMethod method = PaymentMethod.valueOf(paymentMethod);
            financeService.submitPaymentRequest(invoiceId, amount, method, discountCode);
            redirectAttributes.addFlashAttribute("message", "Yêu cầu thanh toán đã được gửi đi. Vui lòng chờ trung tâm xác nhận.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/student/invoices/payment/" + invoiceId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "redirect:/student/invoices/payment/" + invoiceId;
        }
        return "redirect:/student/invoices";
    }

    /** Trích xuất Student profile từ Authentication, đưa vào Model. */
    private void resolveStudent(Authentication auth, Model model) {
        if (auth == null) return;
        Optional<UserAccount> acctOpt = userAccountService.findByUsername(auth.getName());
        if (acctOpt.isEmpty() || acctOpt.get().getRelatedId() == null) {
            model.addAttribute("errorMsg", "Tài khoản chưa được liên kết với hồ sơ học viên.");
            return;
        }
        Long studentId = acctOpt.get().getRelatedId();
        studentService.findById(studentId).ifPresent(student -> {
            model.addAttribute("student", student);
            model.addAttribute("hasStudentProfile", true);

            // Kết quả placement test mới nhất
            Optional<Result> latestResult = placementTestService.getLatestResult(studentId);
            if (latestResult.isPresent()) {
                Result r = latestResult.get();
                model.addAttribute("lastResult", r);
                model.addAttribute("lastLevel", placementTestService.determineLevel(r.getGrade()));
            }

            // Ghi danh & Hóa đơn
            model.addAttribute("enrollments", enrollmentService.findByStudentId(studentId));
            model.addAttribute("invoices", financeService.findInvoicesByStudentId(studentId));
        });
    }
}
