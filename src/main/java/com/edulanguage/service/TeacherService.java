package com.edulanguage.service;

import com.edulanguage.entity.Teacher;

import java.util.List;
import java.util.Optional;

public interface TeacherService {

    List<Teacher> findAll();
    Optional<Teacher> findById(Long id);
}
