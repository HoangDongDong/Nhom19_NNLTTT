package com.edulanguage.controller;

import com.edulanguage.entity.Staff;
import com.edulanguage.entity.Student;
import com.edulanguage.entity.Teacher;
import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.enums.Role;
import com.edulanguage.repository.StaffRepository;
import com.edulanguage.repository.StudentRepository;
import com.edulanguage.repository.TeacherRepository;
import com.edulanguage.service.UserAccountService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserAccountService userAccountService;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final StaffRepository staffRepository;

    public ProfileController(UserAccountService userAccountService,
                             StudentRepository studentRepository,
                             TeacherRepository teacherRepository,
                             StaffRepository staffRepository) {
        this.userAccountService = userAccountService;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.staffRepository = staffRepository;
    }

    @GetMapping
    public String viewProfile(Authentication auth, Model model) {
        model.addAttribute("currentPage", "profile");
        if (auth == null) return "redirect:/login";

        Optional<UserAccount> acctOpt = userAccountService.findByUsername(auth.getName());
        if (acctOpt.isEmpty()) return "redirect:/login";

        UserAccount account = acctOpt.get();
        model.addAttribute("account", account);

        if (account.getRelatedId() != null) {
            if (account.getRole() == Role.STUDENT) {
                studentRepository.findById(account.getRelatedId()).ifPresent(student -> {
                    model.addAttribute("userFullName", student.getFullName());
                    model.addAttribute("userEmail", student.getEmail());
                    model.addAttribute("userPhone", student.getPhone());
                });
            } else if (account.getRole() == Role.TEACHER) {
                teacherRepository.findById(account.getRelatedId()).ifPresent(teacher -> {
                    model.addAttribute("userFullName", teacher.getFullName());
                    model.addAttribute("userEmail", teacher.getEmail());
                    model.addAttribute("userPhone", teacher.getPhone());
                    model.addAttribute("userSpecialty", teacher.getSpecialty());
                });
            } else if (account.getRole() == Role.STAFF || account.getRole() == Role.ADMIN) {
                staffRepository.findById(account.getRelatedId()).ifPresent(staff -> {
                    model.addAttribute("userFullName", staff.getFullName());
                    model.addAttribute("userEmail", staff.getEmail());
                    model.addAttribute("userPhone", staff.getPhone());
                });
            }
        }

        return "profile";
    }

    @PostMapping("/update")
    public String updateProfile(Authentication auth,
                                @RequestParam String email,
                                @RequestParam String phone,
                                RedirectAttributes redirectAttributes) {
        if (auth == null) return "redirect:/login";

        Optional<UserAccount> acctOpt = userAccountService.findByUsername(auth.getName());
        if (acctOpt.isEmpty()) return "redirect:/login";

        UserAccount account = acctOpt.get();
        if (account.getRelatedId() != null) {
            try {
                if (account.getRole() == Role.STUDENT) {
                    studentRepository.findById(account.getRelatedId()).ifPresent(student -> {
                        student.setEmail(email);
                        student.setPhone(phone);
                        studentRepository.save(student);
                    });
                } else if (account.getRole() == Role.TEACHER) {
                    teacherRepository.findById(account.getRelatedId()).ifPresent(teacher -> {
                        teacher.setEmail(email);
                        teacher.setPhone(phone);
                        teacherRepository.save(teacher);
                    });
                } else if (account.getRole() == Role.STAFF || account.getRole() == Role.ADMIN) {
                    staffRepository.findById(account.getRelatedId()).ifPresent(staff -> {
                        staff.setEmail(email);
                        staff.setPhone(phone);
                        staffRepository.save(staff);
                    });
                }
                redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thông tin liên hệ thành công.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMsg", "Lỗi lưu dữ liệu: " + e.getMessage());
            }
        }

        return "redirect:/profile";
    }

    @PostMapping("/change-password")
    public String changePassword(Authentication auth,
                                 @RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (auth == null) return "redirect:/login";

        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMsg", "Mật khẩu xác nhận không khớp.");
            return "redirect:/profile";
        }

        try {
            userAccountService.changePassword(auth.getName(), oldPassword, newPassword);
            redirectAttributes.addFlashAttribute("successMsg", "Đổi mật khẩu thành công. Vui lòng sử dụng mật khẩu mới cho lần đăng nhập sau.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi đổi mật khẩu: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}
