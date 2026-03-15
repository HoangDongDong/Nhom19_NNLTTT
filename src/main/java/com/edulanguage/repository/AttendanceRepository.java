package com.edulanguage.repository;

import com.edulanguage.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByClazzIdAndDate(Long clazzId, java.time.LocalDate date);

    List<Attendance> findByClazzIdOrderByDateDesc(Long clazzId);
}
