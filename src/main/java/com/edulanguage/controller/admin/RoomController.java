package com.edulanguage.controller.admin;

import com.edulanguage.entity.Room;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * CRUD Phòng học — Module Hệ thống (chỉ ADMIN).
 */
@Controller
@RequestMapping("/admin/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("rooms", roomService.findAll());
        return "admin/rooms/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("room", new Room());
        model.addAttribute("statusList", Status.values());
        return "admin/rooms/form";
    }

    @GetMapping("/edit/{id}")
    public String formEdit(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        return roomService.findById(id)
                .map(r -> {
                    model.addAttribute("room", r);
                    model.addAttribute("statusList", Status.values());
                    return "admin/rooms/form";
                })
                .orElseGet(() -> {
                    redirect.addFlashAttribute("error", "Không tìm thấy phòng học.");
                    return "redirect:/admin/rooms";
                });
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("room") Room room, BindingResult result, Model model, RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("statusList", Status.values());
            return "admin/rooms/form";
        }
        roomService.save(room);
        redirect.addFlashAttribute("message", "Đã lưu phòng học.");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        roomService.deleteById(id);
        redirect.addFlashAttribute("message", "Đã xóa phòng học.");
        return "redirect:/admin/rooms";
    }
}
