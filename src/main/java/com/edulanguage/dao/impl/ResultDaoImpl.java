package com.edulanguage.dao.impl;

import com.edulanguage.dao.ResultDao;
import com.edulanguage.entity.Result;
import com.edulanguage.repository.ResultRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ResultDaoImpl implements ResultDao {

    private final ResultRepository resultRepository;

    public ResultDaoImpl(ResultRepository resultRepository) {
        this.resultRepository = resultRepository;
    }

    @Override
    public Result save(Result result) {
        return resultRepository.save(result);
    }

    @Override
    public List<Result> findByStudentId(Long studentId) {
        return resultRepository.findByStudentIdOrderByCreatedAtDesc(studentId);
    }

    @Override
    public Optional<Result> findLatestPlacementResult(Long studentId) {
        List<Result> results = resultRepository
                .findByStudentIdAndClazzIsNullOrderByCreatedAtDesc(studentId);
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}
