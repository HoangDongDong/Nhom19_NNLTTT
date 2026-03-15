package com.edulanguage.dao.impl;

import com.edulanguage.dao.EnrollmentDao;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.repository.EnrollmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EnrollmentDaoImpl implements EnrollmentDao {

    private final EnrollmentRepository enrollmentRepository;

    public EnrollmentDaoImpl(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public Optional<Enrollment> findById(Long id) {
        return enrollmentRepository.findById(id);
    }

    @Override
    public List<Enrollment> findByStudentId(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    @Override
    public int countByClazzId(Long clazzId) {
        return enrollmentRepository.countByClazzId(clazzId);
    }

    @Override
    public boolean existsByStudentIdAndClazzId(Long studentId, Long clazzId) {
        return enrollmentRepository.existsByStudentIdAndClazzId(studentId, clazzId);
    }

    @Override
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepository.save(enrollment);
    }

    @Override
    public void deleteById(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
