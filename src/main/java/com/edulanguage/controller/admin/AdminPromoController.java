package com.edulanguage.controller.admin;

import com.edulanguage.entity.PromoCode;
import com.edulanguage.service.PromoService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD Mã khuyến mại — Module Hệ thống (chỉ ADMIN).
 */
@Controller
@RequestMapping("/admin/promos")
public class AdminPromoController {

    private final PromoService promoService;

    public AdminPromoController(PromoService promoService) {
        this.promoService = promoService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("currentPage", "admin-promos");
        model.addAttribute("promos", promoService.findAll());
        return "admin/promos/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("currentPage", "admin-promos");
        PromoCode promo = new PromoCode();
        promo.setIsActive(true);
        promo.setUsageCount(0);
        model.addAttribute("promo", promo);
        return "admin/promos/form";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return promoService.findById(id)
                .map(promo -> {
                    model.addAttribute("currentPage", "admin-promos");
                    model.addAttribute("promo", promo);
                    return "admin/promos/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy mã khuyến mại.");
                    return "redirect:/admin/promos";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("promo") PromoCode promo,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("currentPage", "admin-promos");
            return "admin/promos/form";
        }

        try {
            // Giữ lại createdAt nếu đang sửa
            if (promo.getId() != null) {
                promoService.findById(promo.getId()).ifPresent(existing -> {
                    promo.setCreatedAt(existing.getCreatedAt());
                    // Giữ nguyên usageCount từ DB, không cho form ghi đè
                    promo.setUsageCount(existing.getUsageCount());
                });
            }

            promoService.save(promo);
            redirect.addFlashAttribute("message", "Đã lưu mã khuyến mại thành công.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Lưu thất bại: " + e.getMessage());
        }
        return "redirect:/admin/promos";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            promoService.deleteById(id);
            redirect.addFlashAttribute("message", "Đã xóa mã khuyến mại.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/promos";
    }
}
