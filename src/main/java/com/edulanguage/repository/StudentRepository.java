package com.edulanguage.repository;

import com.edulanguage.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /** Kiểm tra email đã tồn tại (dùng khi tạo mới). */
    boolean existsByEmail(String email);

    /** Kiểm tra phone đã tồn tại (dùng khi tạo mới). */
    boolean existsByPhone(String phone);

    /** Kiểm tra email đã tồn tại nhưng loại trừ chính record đang sửa. */
    boolean existsByEmailAndIdNot(String email, Long id);

    /** Kiểm tra phone đã tồn tại nhưng loại trừ chính record đang sửa. */
    boolean existsByPhoneAndIdNot(String phone, Long id);
}
