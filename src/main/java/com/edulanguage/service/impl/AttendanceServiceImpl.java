package com.edulanguage.service.impl;

import com.edulanguage.entity.Attendance;
import com.edulanguage.repository.AttendanceRepository;
import com.edulanguage.service.AttendanceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;

    public AttendanceServiceImpl(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> findAll() {
        return attendanceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Attendance> findById(Long id) {
        return attendanceRepository.findById(id);
    }
}
