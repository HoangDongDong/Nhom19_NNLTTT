package com.edulanguage.controller;

import com.edulanguage.entity.*;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.repository.*;
import com.edulanguage.service.ScheduleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/staff/classes")
public class StaffClassController {

    private final ClazzRepository clazzRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final ScheduleService scheduleService;

    public StaffClassController(ClazzRepository clazzRepository, CourseRepository courseRepository,
                                TeacherRepository teacherRepository, RoomRepository roomRepository,
                                ScheduleService scheduleService) {
        this.clazzRepository = clazzRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public String listClasses(Model model) {
        model.addAttribute("currentPage", "staff-classes");
        model.addAttribute("classes", clazzRepository.findAll());
        return "staff/classes/list";
    }

    @GetMapping("/new")
    public String newClassForm(Model model) {
        model.addAttribute("currentPage", "staff-classes");
        model.addAttribute("clazz", new Clazz());
        model.addAttribute("courses", courseRepository.findAll());
        model.addAttribute("teachers", teacherRepository.findAll());
        model.addAttribute("rooms", roomRepository.findAll());
        return "staff/classes/form";
    }

    @PostMapping("/new")
    public String createClass(@ModelAttribute Clazz clazz,
                              @RequestParam("daysOfWeek") List<Integer> daysOfWeek,
                              @RequestParam("startTimeStr") String startTimeStr,
                              @RequestParam("endTimeStr") String endTimeStr,
                              RedirectAttributes redirectAttributes) {

        try {
            // Set default status to pending or active based on start date
            if (clazz.getStartDate().isAfter(LocalDate.now())) {
                clazz.setStatus(Status.PENDING);
            } else {
                clazz.setStatus(Status.ACTIVE);
            }

            // Save the class first to get an ID
            Clazz savedClass = clazzRepository.save(clazz);

            // Parse times
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            // Generate schedules
            scheduleService.autoGenerateSchedule(savedClass, savedClass.getRoom(), startTime, endTime, daysOfWeek);

            redirectAttributes.addFlashAttribute("successMsg", "Tạo lớp và sinh lịch học thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi tạo lớp: " + e.getMessage());
        }

        return "redirect:/staff/classes";
    }
    
    @GetMapping("/{id}")
    public String classDetail(@PathVariable Long id, Model model) {
        model.addAttribute("currentPage", "staff-classes");
        
        Clazz clazz = clazzRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid class ID"));
        List<Schedule> schedules = scheduleService.findByClazzId(id);
        
        model.addAttribute("clazz", clazz);
        model.addAttribute("schedules", schedules);
        model.addAttribute("rooms", roomRepository.findAll()); // for modal edit room
        
        return "staff/classes/detail";
    }

    // Schedule adjustments
    @PostMapping("/{classId}/schedule/{scheduleId}/edit")
    public String updateSchedule(@PathVariable Long classId, 
                                 @PathVariable Long scheduleId,
                                 @RequestParam("date") LocalDate date,
                                 @RequestParam("startTime") LocalTime startTime,
                                 @RequestParam("endTime") LocalTime endTime,
                                 @RequestParam("roomId") Long roomId,
                                 RedirectAttributes redirectAttributes) {
        // Implementation for updating single schedule (Manual Adjust)
        // ... Needs ScheduleRepository inside service ...
        redirectAttributes.addFlashAttribute("successMsg", "Cập nhật buổi học thành công!");
        return "redirect:/staff/classes/" + classId;
    }
}
