package com.edulanguage.controller;

import com.edulanguage.entity.UserAccount;
import com.edulanguage.entity.Clazz;
import com.edulanguage.service.TeacherService;
import com.edulanguage.service.UserAccountService;
import com.edulanguage.dao.ClazzDao;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

/**
 * Dashboard dành cho TEACHER (Giảng viên).
 */
@Controller
@RequestMapping("/teacher")
public class TeacherController {

    private final TeacherService teacherService;
    private final UserAccountService userAccountService;
    private final ClazzDao clazzDao;
    private final com.edulanguage.repository.EnrollmentRepository enrollmentRepository;
    private final com.edulanguage.service.AttendanceService attendanceService;
    private final com.edulanguage.repository.ResultRepository resultRepository;
    private final com.edulanguage.repository.AttendanceRepository attendanceRepository;

    public TeacherController(TeacherService teacherService,
                             UserAccountService userAccountService,
                             ClazzDao clazzDao,
                             com.edulanguage.repository.EnrollmentRepository enrollmentRepository,
                             com.edulanguage.service.AttendanceService attendanceService,
                             com.edulanguage.repository.ResultRepository resultRepository,
                             com.edulanguage.repository.AttendanceRepository attendanceRepository) {
        this.teacherService = teacherService;
        this.userAccountService = userAccountService;
        this.clazzDao = clazzDao;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceService = attendanceService;
        this.resultRepository = resultRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @GetMapping({"", "/", "/dashboard"})
    public String dashboard(Authentication auth, Model model) {
        model.addAttribute("currentPage", "teacher-dashboard");
        resolveTeacher(auth, model);
        return "teacher/dashboard";
    }

    @GetMapping("/schedule")
    public String schedule(Authentication auth, Model model) {
        model.addAttribute("currentPage", "teacher-schedule");
        resolveTeacher(auth, model);
        return "teacher/schedule";
    }

    @GetMapping("/classes")
    public String classes(Authentication auth, Model model) {
        model.addAttribute("currentPage", "teacher-classes");
        resolveTeacher(auth, model);
        return "teacher/classes";
    }

    @GetMapping("/attendance")
    public String attendance(Authentication auth, Model model,
                             @org.springframework.web.bind.annotation.RequestParam(required = false) Long classId,
                             @org.springframework.web.bind.annotation.RequestParam(required = false) java.time.LocalDate date) {
        model.addAttribute("currentPage", "teacher-attendance");
        resolveTeacher(auth, model);

        if (classId != null) {
            java.time.LocalDate selectedDate = date != null ? date : java.time.LocalDate.now();
            model.addAttribute("selectedClassId", classId);
            model.addAttribute("selectedDate", selectedDate);

            // Fetch students enrolled in this class
            List<com.edulanguage.entity.Enrollment> enrollments = enrollmentRepository.findByClazzId(classId);
            model.addAttribute("enrollments", enrollments);

            // Load existing attendance for this class+date (history)
            List<com.edulanguage.entity.Attendance> existingAttendance = attendanceRepository.findByClazzIdAndDate(classId, selectedDate);
            model.addAttribute("existingAttendance", existingAttendance);

            // Load all attendance history for this class
            List<com.edulanguage.entity.Attendance> attendanceHistory = attendanceRepository.findByClazzIdOrderByDateDesc(classId);
            model.addAttribute("attendanceHistory", attendanceHistory);
        }

        return "teacher/attendance";
    }

    @org.springframework.web.bind.annotation.PostMapping("/attendance/submit")
    public String submitAttendance(@org.springframework.web.bind.annotation.RequestParam Long classId,
                                   @org.springframework.web.bind.annotation.RequestParam("attendanceDate") java.time.LocalDate date,
                                   @org.springframework.web.bind.annotation.RequestParam java.util.Map<String, String> requestParams,
                                   org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        
        try {
            java.util.Map<Long, String> statuses = new java.util.HashMap<>();
            // Extract studentId and status from requestParams (format: status_1=PRESENT)
            for (java.util.Map.Entry<String, String> entry : requestParams.entrySet()) {
                if (entry.getKey().startsWith("status_")) {
                    Long studentId = Long.parseLong(entry.getKey().replace("status_", ""));
                    statuses.put(studentId, entry.getValue());
                }
            }

            com.edulanguage.entity.Clazz clazz = new com.edulanguage.entity.Clazz();
            clazz.setId(classId);
            
            attendanceService.submitAttendance(clazz, date, statuses);
            redirectAttributes.addFlashAttribute("successMsg", "Điểm danh thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi điểm danh: " + e.getMessage());
        }

        return "redirect:/teacher/attendance?classId=" + classId + "&date=" + date;
    }

    @GetMapping("/results")
    public String results(Authentication auth, Model model,
                          @org.springframework.web.bind.annotation.RequestParam(required = false) Long classId) {
        model.addAttribute("currentPage", "teacher-results");
        resolveTeacher(auth, model);

        if (classId != null) {
            model.addAttribute("selectedClassId", classId);
            List<com.edulanguage.entity.Enrollment> enrollments = enrollmentRepository.findByClazzId(classId);
            model.addAttribute("enrollments", enrollments);

            // Load existing results for this class (history)
            List<com.edulanguage.entity.Result> existingResults = resultRepository.findByClazzId(classId);
            model.addAttribute("existingResults", existingResults);
        }

        return "teacher/results";
    }

    @org.springframework.web.bind.annotation.PostMapping("/results/submit")
    public String submitResults(@org.springframework.web.bind.annotation.RequestParam Long classId,
                                @org.springframework.web.bind.annotation.RequestParam java.util.Map<String, String> requestParams,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            com.edulanguage.entity.Clazz clazz = new com.edulanguage.entity.Clazz();
            clazz.setId(classId);

            for (java.util.Map.Entry<String, String> entry : requestParams.entrySet()) {
                if (entry.getKey().startsWith("score_")) {
                    Long studentId = Long.parseLong(entry.getKey().replace("score_", ""));
                    String scoreStr = entry.getValue();
                    String grade = requestParams.getOrDefault("grade_" + studentId, "");
                    String comment = requestParams.getOrDefault("comment_" + studentId, "");

                    if (scoreStr != null && !scoreStr.isBlank()) {
                        com.edulanguage.entity.Student student = new com.edulanguage.entity.Student();
                        student.setId(studentId);

                        com.edulanguage.entity.Result result = new com.edulanguage.entity.Result();
                        result.setStudent(student);
                        result.setClazz(clazz);
                        result.setScore(new java.math.BigDecimal(scoreStr));
                        result.setGrade(grade);
                        result.setComment(comment);

                        resultRepository.save(result);
                    }
                }
            }
            redirectAttributes.addFlashAttribute("successMsg", "Nhập điểm thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi nhập điểm: " + e.getMessage());
        }
        return "redirect:/teacher/results?classId=" + classId;
    }

    /** Trích xuất Teacher profile từ Authentication, đưa vào Model. */
    private void resolveTeacher(Authentication auth, Model model) {
        if (auth == null) return;
        Optional<UserAccount> acct = userAccountService.findByUsername(auth.getName());
        if (acct.isPresent() && acct.get().getRelatedId() != null) {
            Long teacherId = acct.get().getRelatedId();
            teacherService.findById(teacherId).ifPresent(t -> {
                model.addAttribute("teacher", t);
                List<Clazz> myClasses = clazzDao.findByTeacherId(teacherId);
                model.addAttribute("myClasses", myClasses);
                model.addAttribute("classCount", myClasses.size());
            });
        }
    }
}
