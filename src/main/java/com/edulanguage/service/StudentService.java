package com.edulanguage.service;

import com.edulanguage.entity.Student;

import java.util.List;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: dịch vụ học viên.
 */
public interface StudentService {

    List<Student> findAll();
    Optional<Student> findById(Long id);
    Student save(Student student);
    void deleteById(Long id);

    /** Kiểm tra email đã tồn tại (tạo mới). */
    boolean existsByEmail(String email);

    /** Kiểm tra phone đã tồn tại (tạo mới). */
    boolean existsByPhone(String phone);

    /** Kiểm tra email đã tồn tại, loại trừ record đang sửa. */
    boolean existsByEmailExcluding(String email, Long id);

    /** Kiểm tra phone đã tồn tại, loại trừ record đang sửa. */
    boolean existsByPhoneExcluding(String phone, Long id);
}
