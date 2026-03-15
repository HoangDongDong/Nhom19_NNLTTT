package com.edulanguage.dao;

import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.enums.Status;
import java.util.List;
import java.util.Optional;

public interface ClazzDao {
    List<Clazz> findAll();
    Optional<Clazz> findById(Long id);
    List<Clazz> findByStatus(Status status);
    Clazz save(Clazz clazz);
    void deleteById(Long id);
    List<Clazz> findByTeacherId(Long teacherId);
}
