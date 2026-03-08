package com.edulanguage.service;

import com.edulanguage.entity.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {

    List<Enrollment> findAll();
    Optional<Enrollment> findById(Long id);
}
