package com.edulanguage.service.impl;

import com.edulanguage.entity.Enrollment;
import com.edulanguage.repository.EnrollmentRepository;
import com.edulanguage.service.EnrollmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }
}
