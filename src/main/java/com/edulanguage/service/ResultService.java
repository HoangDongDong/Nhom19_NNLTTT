package com.edulanguage.service;

import com.edulanguage.entity.Result;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service quản lý kết quả học tập (Result).
 * Tuân thủ SOLID: Single Responsibility - chỉ xử lý nghiệp vụ nhập điểm.
 * Phân biệt: Placement Test (class_id = null) do PlacementTestService xử lý.
 */
public interface ResultService {

    /**
     * Lấy tất cả kết quả của một lớp học.
     */
    List<Result> findByClazzId(Long clazzId);

    /**
     * Lấy kết quả của một học viên trong một lớp cụ thể.
     */
    Optional<Result> findByStudentIdAndClazzId(Long studentId, Long clazzId);

    /**
     * Gửi/nhập điểm cho lớp học.
     * Cập nhật nếu đã tồn tại (cùng student + class), ngược lại tạo mới.
     * Chỉ chấp nhận học viên đã ghi danh vào lớp.
     *
     * @param clazzId     ID lớp học
     * @param studentData Map: studentId -> {score, grade, comment}
     */
    void submitClassResults(Long clazzId, Map<Long, ResultRecord> studentData);

    /**
     * DTO cho dữ liệu nhập điểm một học viên.
     */
    record ResultRecord(BigDecimal score, String grade, String comment) {}
}
