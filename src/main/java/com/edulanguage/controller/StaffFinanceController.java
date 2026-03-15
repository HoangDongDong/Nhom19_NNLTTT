package com.edulanguage.controller;

import com.edulanguage.entity.enums.PaymentMethod;
import com.edulanguage.service.FinanceService;
import com.edulanguage.service.PromoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/invoices")
public class StaffFinanceController {

    private final FinanceService financeService;
    private final PromoService promoService;

    public StaffFinanceController(FinanceService financeService, PromoService promoService) {
        this.financeService = financeService;
        this.promoService = promoService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "staff-invoices");
        model.addAttribute("invoices", financeService.findAllInvoices());
        return "staff/invoices/list";
    }

    @GetMapping("/payment/{id}")
    public String paymentForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return financeService.findInvoiceById(id)
                .map(invoice -> {
                    if ("PAID".equals(invoice.getStatus())) {
                        redirectAttributes.addFlashAttribute("message", "Hóa đơn này đã được thanh toán.");
                        return "redirect:/staff/invoices";
                    }
                    model.addAttribute("currentPage", "staff-invoices");
                    model.addAttribute("invoice", invoice);
                    model.addAttribute("paymentMethods", PaymentMethod.values());
                    model.addAttribute("promoCodes", promoService.findActivePromos());
                    return "staff/invoices/payment";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy hóa đơn.");
                    return "redirect:/staff/invoices";
                });
    }

    @PostMapping("/process")
    public String processPayment(@RequestParam Long invoiceId,
                                @RequestParam(name = "amount", required = false) String amountStr,
                                @RequestParam(required = false) PaymentMethod paymentMethod,
                                @RequestParam(required = false) String discountCode,
                                RedirectAttributes redirectAttributes) {
        try {
            if (amountStr == null || amountStr.isBlank()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập số tiền thanh toán.");
                return "redirect:/staff/invoices/payment/" + invoiceId;
            }
            // Cho phép: 3500000, 3500000.5, 3,500,000 (dấu phẩy nghìn)
            String clean = amountStr.replace(",", "").replace(" ", "").trim();
            if (clean.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng nhập số tiền thanh toán.");
                return "redirect:/staff/invoices/payment/" + invoiceId;
            }
            java.math.BigDecimal amount = new java.math.BigDecimal(clean);
            if (paymentMethod == null) {
                redirectAttributes.addFlashAttribute("error", "Vui lòng chọn phương thức thanh toán.");
                return "redirect:/staff/invoices/payment/" + invoiceId;
            }
            financeService.processPayment(invoiceId, amount, paymentMethod, discountCode != null ? discountCode.trim() : "");
            redirectAttributes.addFlashAttribute("message", "Thanh toán thành công!");
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "Số tiền không hợp lệ. Vui lòng nhập số (VD: 3500000).");
            return "redirect:/staff/invoices/payment/" + invoiceId;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
            return "redirect:/staff/invoices/payment/" + invoiceId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi xử lý: " + e.getMessage());
            return "redirect:/staff/invoices/payment/" + invoiceId;
        }
        return "redirect:/staff/invoices";
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
