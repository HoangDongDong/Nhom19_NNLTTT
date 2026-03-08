package com.edulanguage.controller.admin;

import com.edulanguage.entity.Branch;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.BranchService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD Chi nhánh — Module Hệ thống (chỉ ADMIN).
 */
@Controller
@RequestMapping("/admin/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("branches", branchService.findAll());
        return "admin/branches/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("branch", new Branch());
        model.addAttribute("statusList", Status.values());
        return "admin/branches/form";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return branchService.findById(id)
                .map(b -> {
                    model.addAttribute("branch", b);
                    model.addAttribute("statusList", Status.values());
                    return "admin/branches/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy chi nhánh.");
                    return "redirect:/admin/branches";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("branch") Branch branch, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("statusList", Status.values());
            return "admin/branches/form";
        }
        branchService.save(branch);
        redirect.addFlashAttribute("message", "Đã lưu chi nhánh.");
        return "redirect:/admin/branches";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        branchService.deleteById(id);
        redirect.addFlashAttribute("message", "Đã xóa chi nhánh.");
        return "redirect:/admin/branches";
    }
}
