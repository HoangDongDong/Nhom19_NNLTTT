package com.edulanguage.service.impl;

import com.edulanguage.dao.ResultDao;
import com.edulanguage.entity.Result;
import com.edulanguage.entity.Student;
import com.edulanguage.service.PlacementTestService;
import com.edulanguage.service.StudentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Triển khai nghiệp vụ Test đầu vào (Placement Test).
 * <ul>
 *   <li>Nhận điểm (0–100) → tự động tính grade (A/B/C/D/F)</li>
 *   <li>Từ grade → xác định level (Advanced/Intermediate/Beginner)</li>
 *   <li>Lưu Result với class_id = NULL (không gắn lớp)</li>
 * </ul>
 */
@Service
public class PlacementTestServiceImpl implements PlacementTestService {

    private final ResultDao resultDao;
    private final StudentService studentService;

    public PlacementTestServiceImpl(ResultDao resultDao, StudentService studentService) {
        this.resultDao = resultDao;
        this.studentService = studentService;
    }

    @Override
    @Transactional
    public Result submitTest(Long studentId, BigDecimal score, String comment) {
        Student student = studentService.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy học viên ID: " + studentId));

        String grade = determineGrade(score);

        Result result = new Result();
        result.setStudent(student);
        result.setClazz(null);          // Placement test — không gắn lớp
        result.setScore(score);
        result.setGrade(grade);
        result.setComment(comment);

        return resultDao.save(result);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Result> getLatestResult(Long studentId) {
        return resultDao.findLatestPlacementResult(studentId);
    }

    @Override
    public String determineGrade(BigDecimal score) {
        if (score == null) return "F";
        if (score.compareTo(new BigDecimal("90")) >= 0) return "A";
        if (score.compareTo(new BigDecimal("75")) >= 0) return "B";
        if (score.compareTo(new BigDecimal("60")) >= 0) return "C";
        if (score.compareTo(new BigDecimal("40")) >= 0) return "D";
        return "F";
    }

    @Override
    public String determineLevel(String grade) {
        if (grade == null) return "Beginner";
        return switch (grade) {
            case "A", "B" -> "Advanced";
            case "C"      -> "Intermediate";
            default        -> "Beginner";   // D, F
        };
    }
}
