package com.edulanguage.repository;

import com.edulanguage.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByClazzIdOrderByDateAscStartTimeAsc(Long clazzId);
}
