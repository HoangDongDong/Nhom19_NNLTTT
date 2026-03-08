package com.edulanguage.service.impl;

import com.edulanguage.entity.Clazz;
import com.edulanguage.repository.ClazzRepository;
import com.edulanguage.service.ClazzService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClazzServiceImpl implements ClazzService {

    private final ClazzRepository clazzRepository;

    public ClazzServiceImpl(ClazzRepository clazzRepository) {
        this.clazzRepository = clazzRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Clazz> findAll() {
        return clazzRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Clazz> findById(Long id) {
        return clazzRepository.findById(id);
    }
}
