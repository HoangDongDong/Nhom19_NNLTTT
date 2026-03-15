package com.edulanguage.service;

import com.edulanguage.entity.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentService {
    List<Enrollment> findAll();
    Optional<Enrollment> findById(Long id);
    List<Enrollment> findByStudentId(Long studentId);
    
    Enrollment enrollStudent(Long studentId, Long classId);
    void cancelEnrollment(Long enrollmentId);
    Enrollment save(Enrollment enrollment);
}
