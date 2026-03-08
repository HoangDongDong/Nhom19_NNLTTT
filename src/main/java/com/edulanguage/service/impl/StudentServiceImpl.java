package com.edulanguage.service.impl;

import com.edulanguage.entity.Student;
import com.edulanguage.repository.StudentRepository;
import com.edulanguage.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: triển khai nghiệp vụ học viên.
 * Gọi Repository (Data Access), không gọi tầng Presentation.
 */
@Service
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findById(Long id) {
        return studentRepository.findById(id);
    }
}
