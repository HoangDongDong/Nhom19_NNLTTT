package com.edulanguage.controller.admin;

import com.edulanguage.entity.Staff;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.service.StaffService;
import com.edulanguage.service.UserAccountService;
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
    private final UserAccountService userAccountService;

    public AdminStaffController(StaffService staffService, UserAccountService userAccountService) {
        this.staffService = staffService;
        this.userAccountService = userAccountService;
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
                    
                    userAccountService.findByRoleAndRelatedId(s.getRole(), s.getId())
                            .ifPresent(account -> model.addAttribute("currentUsername", account.getUsername()));
                            
                    return "admin/staffs/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy nhân viên.");
                    return "redirect:/admin/staffs";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("staff") Staff staff, 
                       @RequestParam(value = "username", required = false) String username,
                       @RequestParam(value = "password", required = false) String password,
                       BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("roleList", Role.values());
            return "admin/staffs/form";
        }
        
        // Cần truyền ID vào để mapping UserAccount, nên save staff trước
        Staff savedStaff = staffService.save(staff);
        
        try {
            userAccountService.createOrUpdateAccount(savedStaff.getRole(), savedStaff.getId(), username, password);
            redirect.addFlashAttribute("message", "Đã lưu nhân viên và tài khoản cấu hình thành công.");
        } catch (IllegalArgumentException e) {
            redirect.addFlashAttribute("error", "Đã lưu nhân viên nhưng cấu hình tài khoản thất bại: " + e.getMessage());
        }
        
        return "redirect:/admin/staffs";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            // Tìm role thực tế của staff (STAFF hoặc ADMIN) trước khi xóa
            staffService.findById(id).ifPresent(staff -> 
                userAccountService.deleteByRoleAndRelatedId(staff.getRole(), id)
            );
            staffService.deleteById(id);
            redirect.addFlashAttribute("message", "Đã xóa nhân viên và tài khoản liên quan.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/admin/staffs";
    }
}
