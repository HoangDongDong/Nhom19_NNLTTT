package com.edulanguage.service;

import com.edulanguage.entity.Clazz;

import java.util.List;
import java.util.Optional;

public interface ClazzService {

    List<Clazz> findAll();
    Optional<Clazz> findById(Long id);
}
