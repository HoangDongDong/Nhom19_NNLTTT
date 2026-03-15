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
    private final com.edulanguage.service.ResultService resultService;

    public TeacherController(TeacherService teacherService,
                             UserAccountService userAccountService,
                             ClazzDao clazzDao,
                             com.edulanguage.repository.EnrollmentRepository enrollmentRepository,
                             com.edulanguage.service.AttendanceService attendanceService,
                             com.edulanguage.service.ResultService resultService) {
        this.teacherService = teacherService;
        this.userAccountService = userAccountService;
        this.clazzDao = clazzDao;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceService = attendanceService;
        this.resultService = resultService;
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

            // Load existing attendance for this class+date (for pre-fill form)
            List<com.edulanguage.entity.Attendance> existingAttendance = attendanceService.findByClazzIdAndDate(classId, selectedDate);
            model.addAttribute("existingAttendance", existingAttendance);
            java.util.Map<Long, com.edulanguage.entity.Attendance> existingMap = existingAttendance.stream()
                    .collect(java.util.stream.Collectors.toMap(a -> a.getStudent().getId(), a -> a, (a, b) -> a));

            // Load all attendance history for this class
            List<com.edulanguage.entity.Attendance> attendanceHistory = attendanceService.findByClazzIdOrderByDateDesc(classId);
            model.addAttribute("attendanceHistory", attendanceHistory);
            model.addAttribute("existingAttendanceMap", existingMap);
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
            java.util.Map<Long, String> notes = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, String> entry : requestParams.entrySet()) {
                if (entry.getKey().startsWith("status_")) {
                    Long studentId = Long.parseLong(entry.getKey().replace("status_", ""));
                    statuses.put(studentId, entry.getValue());
                } else if (entry.getKey().startsWith("note_")) {
                    Long studentId = Long.parseLong(entry.getKey().replace("note_", ""));
                    notes.put(studentId, entry.getValue() != null ? entry.getValue() : "");
                }
            }

            com.edulanguage.entity.Clazz clazz = new com.edulanguage.entity.Clazz();
            clazz.setId(classId);

            attendanceService.submitAttendance(clazz, date, statuses, notes);
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

            // Load existing results for this class (history + pre-fill form)
            List<com.edulanguage.entity.Result> existingResults = resultService.findByClazzId(classId);
            model.addAttribute("existingResults", existingResults);
            java.util.Map<Long, com.edulanguage.entity.Result> existingResultMap = existingResults.stream()
                    .collect(java.util.stream.Collectors.toMap(r -> r.getStudent().getId(), r -> r, (a, b) -> a));
            model.addAttribute("existingResultMap", existingResultMap);
        }

        return "teacher/results";
    }

    @org.springframework.web.bind.annotation.PostMapping("/results/submit")
    public String submitResults(@org.springframework.web.bind.annotation.RequestParam Long classId,
                                @org.springframework.web.bind.annotation.RequestParam java.util.Map<String, String> requestParams,
                                org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            java.util.Map<Long, com.edulanguage.service.ResultService.ResultRecord> studentData = new java.util.HashMap<>();
            for (java.util.Map.Entry<String, String> entry : requestParams.entrySet()) {
                if (entry.getKey().startsWith("score_")) {
                    Long studentId = Long.parseLong(entry.getKey().replace("score_", ""));
                    String scoreStr = entry.getValue();
                    String grade = requestParams.getOrDefault("grade_" + studentId, "");
                    String comment = requestParams.getOrDefault("comment_" + studentId, "");

                    if (scoreStr != null && !scoreStr.isBlank()) {
                        studentData.put(studentId, new com.edulanguage.service.ResultService.ResultRecord(
                                new java.math.BigDecimal(scoreStr),
                                grade != null && !grade.isBlank() ? grade : null,
                                comment != null && !comment.isBlank() ? comment : null
                        ));
                    }
                }
            }
            resultService.submitClassResults(classId, studentData);
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
