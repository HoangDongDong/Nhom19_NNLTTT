package com.edulanguage.repository;

import com.edulanguage.entity.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    /** Lấy tất cả kết quả của 1 học viên, sắp xếp mới nhất trước. */
    List<Result> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    /** Lấy kết quả placement test (class_id IS NULL) của học viên, mới nhất trước. */
    List<Result> findByStudentIdAndClazzIsNullOrderByCreatedAtDesc(Long studentId);

    /** Lấy tất cả kết quả của 1 lớp học. */
    List<Result> findByClazzId(Long clazzId);

    /** Lấy kết quả của 1 học viên trong 1 lớp học cụ thể. */
    java.util.Optional<Result> findByStudentIdAndClazzId(Long studentId, Long clazzId);
}
