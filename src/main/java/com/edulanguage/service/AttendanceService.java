package com.edulanguage.service;

import com.edulanguage.entity.Attendance;

import java.util.List;
import java.util.Optional;

public interface AttendanceService {

    List<Attendance> findAll();
    Optional<Attendance> findById(Long id);
}
