package com.edulanguage.service;

import com.edulanguage.entity.Attendance;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Student;
import com.edulanguage.entity.enums.AttendanceStatus;
import com.edulanguage.repository.AttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }

    /**
     * Bulk insert or update attendance records for a specific class schedule.
     */
    @Transactional
    public void submitAttendance(Clazz clazz, LocalDate date, Map<Long, String> studentStatuses) {
        for (Map.Entry<Long, String> entry : studentStatuses.entrySet()) {
            Long studentId = entry.getKey();
            String statusStr = entry.getValue();

            Student student = new Student();
            student.setId(studentId);

            Attendance attendance = new Attendance();
            attendance.setClazz(clazz);
            attendance.setStudent(student);
            attendance.setDate(date);
            attendance.setStatus(AttendanceStatus.valueOf(statusStr));

            attendanceRepository.save(attendance);
        }
    }
}
