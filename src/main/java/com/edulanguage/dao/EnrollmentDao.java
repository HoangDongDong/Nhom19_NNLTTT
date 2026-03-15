package com.edulanguage.dao;

import com.edulanguage.entity.Enrollment;

import java.util.List;
import java.util.Optional;

public interface EnrollmentDao {
    List<Enrollment> findAll();
    Optional<Enrollment> findById(Long id);
    List<Enrollment> findByStudentId(Long studentId);
    int countByClazzId(Long clazzId);
    boolean existsByStudentIdAndClazzId(Long studentId, Long clazzId);
    Enrollment save(Enrollment enrollment);
    void deleteById(Long id);
}
