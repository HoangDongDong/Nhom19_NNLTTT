package com.edulanguage.controller;

import com.edulanguage.service.FinanceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/invoices")
public class StaffFinanceController {

    private final FinanceService financeService;

    public StaffFinanceController(FinanceService financeService) {
        this.financeService = financeService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "staff-invoices");
        model.addAttribute("invoices", financeService.findAllInvoices());
        return "staff/invoices/list";
    }

    @PostMapping("/confirm")
    public String confirmPayment(@RequestParam Long invoiceId,
                                 @RequestParam boolean isApproved,
                                 RedirectAttributes redirectAttributes) {
        try {
            financeService.confirmPayment(invoiceId, isApproved);
            if (isApproved) {
                redirectAttributes.addFlashAttribute("message", "Đã XÁC NHẬN thanh toán. Ghi danh đã được kích hoạt.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Đã TỪ CHỐI thanh toán. Yêu cầu học viên nộp lại.");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xác nhận: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý: " + e.getMessage());
        }
        return "redirect:/staff/invoices";
    }
}
