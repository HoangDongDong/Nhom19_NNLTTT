package com.edulanguage.service;

import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Room;
import com.edulanguage.entity.Schedule;
import com.edulanguage.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<Schedule> findByClazzId(Long clazzId) {
        return scheduleRepository.findByClazzIdOrderByDateAscStartTimeAsc(clazzId);
    }

    @Transactional
    public void autoGenerateSchedule(Clazz clazz, Room room, LocalTime startTime, LocalTime endTime, List<Integer> daysOfWeek) {
        LocalDate startDate = clazz.getStartDate();
        LocalDate endDate = clazz.getEndDate();

        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return; // Invalid dates
        }

        List<Schedule> newSchedules = new ArrayList<>();
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            // DayOfWeek enum: Monday = 1, Sunday = 7
            // the UI will pass JS compatible days (e.g. 1=Mon, 2=Tue, etc.)
            int currentDayVal = currentDate.getDayOfWeek().getValue(); 
            if (daysOfWeek.contains(currentDayVal)) {
                Schedule schedule = new Schedule();
                schedule.setClazz(clazz);
                schedule.setRoom(room);
                schedule.setDate(currentDate);
                schedule.setStartTime(startTime);
                schedule.setEndTime(endTime);
                newSchedules.add(schedule);
            }
            currentDate = currentDate.plusDays(1);
        }

        scheduleRepository.saveAll(newSchedules);
    }
}
