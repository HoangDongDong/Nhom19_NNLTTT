package com.edulanguage.service.impl;

import com.edulanguage.entity.Teacher;
import com.edulanguage.repository.TeacherRepository;
import com.edulanguage.service.TeacherService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherServiceImpl(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Teacher> findAll() {
        return teacherRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Teacher> findById(Long id) {
        return teacherRepository.findById(id);
    }
}
