package com.edulanguage.service;

import com.edulanguage.entity.Attendance;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Student;
import com.edulanguage.entity.enums.AttendanceStatus;
import com.edulanguage.repository.AttendanceRepository;
import com.edulanguage.repository.EnrollmentRepository;
import com.edulanguage.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service quản lý điểm danh (Attendance).
 * Tuân thủ SOLID: Single Responsibility - chỉ xử lý nghiệp vụ điểm danh.
 */
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    public List<Attendance> findByClazzIdAndDate(Long clazzId, LocalDate date) {
        return attendanceRepository.findByClazzIdAndDate(clazzId, date);
    }

    public List<Attendance> findByClazzIdOrderByDateDesc(Long clazzId) {
        return attendanceRepository.findByClazzIdOrderByDateDesc(clazzId);
    }

    /**
     * Gửi điểm danh cho lớp học vào ngày cụ thể.
     * Cập nhật nếu đã tồn tại (cùng student + class + date), ngược lại tạo mới.
     * Chỉ chấp nhận học viên đã ghi danh vào lớp.
     */
    @Transactional
    public void submitAttendance(Clazz clazz, LocalDate date,
                                 Map<Long, String> studentStatuses,
                                 Map<Long, String> studentNotes) {
        Long clazzId = clazz.getId();
        for (Map.Entry<Long, String> entry : studentStatuses.entrySet()) {
            Long studentId = entry.getKey();
            String statusStr = entry.getValue();
            String note = studentNotes != null ? studentNotes.getOrDefault(studentId, "") : "";

            if (!enrollmentRepository.existsByStudentIdAndClazzId(studentId, clazzId)) {
                continue;
            }

            Student student = studentRepository.findById(studentId).orElse(null);
            if (student == null) {
                continue;
            }

            Optional<Attendance> existingOpt = attendanceRepository
                    .findByStudentIdAndClazzIdAndDate(studentId, clazzId, date);

            Attendance attendance;
            if (existingOpt.isPresent()) {
                attendance = existingOpt.get();
                attendance.setStatus(AttendanceStatus.valueOf(statusStr));
                attendance.setNote(note != null && !note.isBlank() ? note : null);
            } else {
                attendance = Attendance.builder()
                        .student(student)
                        .clazz(clazz)
                        .date(date)
                        .status(AttendanceStatus.valueOf(statusStr))
                        .note(note != null && !note.isBlank() ? note : null)
                        .build();
            }
            attendanceRepository.save(attendance);
        }
    }
}
