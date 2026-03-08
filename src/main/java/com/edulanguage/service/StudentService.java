package com.edulanguage.service;

import com.edulanguage.entity.Student;

import java.util.List;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: dịch vụ học viên.
 */
public interface StudentService {

    List<Student> findAll();
    Optional<Student> findById(Long id);
}
