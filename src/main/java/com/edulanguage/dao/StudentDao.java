package com.edulanguage.dao;

import com.edulanguage.entity.Student;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) cho Học viên.
 * Service gọi DAO; DAO gọi Repository.
 */
public interface StudentDao {

    List<Student> findAll();
    Optional<Student> findById(Long id);
    Student save(Student student);
    void deleteById(Long id);

    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);
}
