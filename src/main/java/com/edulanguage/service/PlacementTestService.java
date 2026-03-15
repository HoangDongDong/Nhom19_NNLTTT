package com.edulanguage.service;

import com.edulanguage.entity.Result;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Tầng nghiệp vụ: dịch vụ Test đầu vào (Placement Test).
 * Chấm điểm tự động và phân loại trình độ.
 */
public interface PlacementTestService {

    /**
     * Nộp kết quả test đầu vào.
     * Hệ thống tự động tính grade và lưu Result (class_id = NULL).
     */
    Result submitTest(Long studentId, BigDecimal score, String comment);

    /** Lấy kết quả placement test mới nhất của học viên. */
    Optional<Result> getLatestResult(Long studentId);

    /** Tính grade (A/B/C/D/F) từ điểm số. */
    String determineGrade(BigDecimal score);

    /** Xác định trình độ (Advanced/Intermediate/Beginner) từ grade. */
    String determineLevel(String grade);
}
