package com.edulanguage.service.impl;

import com.edulanguage.entity.Clazz;
import com.edulanguage.entity.Result;
import com.edulanguage.entity.Student;
import com.edulanguage.repository.EnrollmentRepository;
import com.edulanguage.repository.ResultRepository;
import com.edulanguage.repository.StudentRepository;
import com.edulanguage.service.ResultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation của ResultService.
 * Tuân thủ SOLID: Dependency Inversion - phụ thuộc vào interface Repository.
 */
@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public ResultServiceImpl(ResultRepository resultRepository,
                              StudentRepository studentRepository,
                              EnrollmentRepository enrollmentRepository) {
        this.resultRepository = resultRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    @Override
    public List<Result> findByClazzId(Long clazzId) {
        return resultRepository.findByClazzId(clazzId);
    }

    @Override
    public Optional<Result> findByStudentIdAndClazzId(Long studentId, Long clazzId) {
        return resultRepository.findByStudentIdAndClazzId(studentId, clazzId);
    }

    @Override
    @Transactional
    public void submitClassResults(Long clazzId, Map<Long, ResultService.ResultRecord> studentData) {
        Clazz clazz = new Clazz();
        clazz.setId(clazzId);

        for (Map.Entry<Long, ResultService.ResultRecord> entry : studentData.entrySet()) {
            Long studentId = entry.getKey();
            ResultService.ResultRecord record = entry.getValue();

            if (record == null || record.score() == null) {
                continue;
            }
            if (!enrollmentRepository.existsByStudentIdAndClazzId(studentId, clazzId)) {
                continue;
            }

            Optional<Student> studentOpt = studentRepository.findById(studentId);
            if (studentOpt.isEmpty()) {
                continue;
            }

            Optional<Result> existingOpt = resultRepository.findByStudentIdAndClazzId(studentId, clazzId);
            Result result;
            if (existingOpt.isPresent()) {
                result = existingOpt.get();
                result.setScore(record.score());
                result.setGrade(record.grade() != null && !record.grade().isBlank() ? record.grade() : null);
                result.setComment(record.comment() != null && !record.comment().isBlank() ? record.comment() : null);
            } else {
                result = Result.builder()
                        .student(studentOpt.get())
                        .clazz(clazz)
                        .score(record.score())
                        .grade(record.grade() != null && !record.grade().isBlank() ? record.grade() : null)
                        .comment(record.comment() != null && !record.comment().isBlank() ? record.comment() : null)
                        .build();
            }
            resultRepository.save(result);
        }
    }
}
