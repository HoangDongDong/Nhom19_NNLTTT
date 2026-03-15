package com.edulanguage.dao;

import com.edulanguage.entity.Result;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) cho Kết quả (Result / Placement Test).
 */
public interface ResultDao {

    Result save(Result result);
    List<Result> findByStudentId(Long studentId);

    /** Lấy kết quả placement test (class_id IS NULL) mới nhất của học viên. */
    Optional<Result> findLatestPlacementResult(Long studentId);
}
