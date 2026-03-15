package com.edulanguage.repository;

import com.edulanguage.entity.Clazz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.edulanguage.entity.enums.Status;

import java.util.List;

@Repository
public interface ClazzRepository extends JpaRepository<Clazz, Long> {
    List<Clazz> findByStatus(Status status);
    List<Clazz> findByTeacherId(Long teacherId);
}
