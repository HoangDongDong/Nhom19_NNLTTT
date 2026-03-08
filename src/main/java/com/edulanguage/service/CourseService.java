package com.edulanguage.service;

import com.edulanguage.entity.Course;

import java.util.List;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: dịch vụ khóa học.
 */
public interface CourseService {

    List<Course> findAll();
    Optional<Course> findById(Long id);
}
