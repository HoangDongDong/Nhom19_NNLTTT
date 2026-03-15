package com.edulanguage.dao.impl;

import com.edulanguage.dao.ClazzDao;
import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.enums.Status;
import com.edulanguage.repository.ClazzRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ClazzDaoImpl implements ClazzDao {

    private final ClazzRepository clazzRepository;

    public ClazzDaoImpl(ClazzRepository clazzRepository) {
        this.clazzRepository = clazzRepository;
    }

    @Override
    public List<Clazz> findAll() {
        return clazzRepository.findAll();
    }

    @Override
    public Optional<Clazz> findById(Long id) {
        return clazzRepository.findById(id);
    }

    @Override
    public List<Clazz> findByStatus(Status status) {
        return clazzRepository.findByStatus(status);
    }

    @Override
    public Clazz save(Clazz clazz) {
        return clazzRepository.save(clazz);
    }

    @Override
    public void deleteById(Long id) {
        clazzRepository.deleteById(id);
    }

    @Override
    public List<Clazz> findByTeacherId(Long teacherId) {
        return clazzRepository.findByTeacherId(teacherId);
    }
}
