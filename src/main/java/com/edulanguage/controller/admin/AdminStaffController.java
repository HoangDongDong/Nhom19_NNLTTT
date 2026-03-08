package com.edulanguage.controller.admin;

import com.edulanguage.entity.Staff;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD Nhân viên — Module Hệ thống (chỉ ADMIN).
 */
@Controller
@RequestMapping("/admin/staffs")
public class AdminStaffController {

    private final StaffService staffService;

    public AdminStaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("staffs", staffService.findAll());
        return "admin/staffs/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("staff", new Staff());
        model.addAttribute("roleList", Role.values());
        return "admin/staffs/form";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return staffService.findById(id)
                .map(s -> {
                    model.addAttribute("staff", s);
                    model.addAttribute("roleList", Role.values());
                    return "admin/staffs/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy nhân viên.");
                    return "redirect:/admin/staffs";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("staff") Staff staff, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("roleList", Role.values());
            return "admin/staffs/form";
        }
        staffService.save(staff);
        redirect.addFlashAttribute("message", "Đã lưu nhân viên.");
        return "redirect:/admin/staffs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        staffService.deleteById(id);
        redirect.addFlashAttribute("message", "Đã xóa nhân viên.");
        return "redirect:/admin/staffs";
    }
}
