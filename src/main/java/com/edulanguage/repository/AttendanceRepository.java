package com.edulanguage.repository;

import com.edulanguage.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByClazzIdAndDate(Long clazzId, LocalDate date);

    List<Attendance> findByClazzIdOrderByDateDesc(Long clazzId);

    Optional<Attendance> findByStudentIdAndClazzIdAndDate(Long studentId, Long clazzId, LocalDate date);
}
