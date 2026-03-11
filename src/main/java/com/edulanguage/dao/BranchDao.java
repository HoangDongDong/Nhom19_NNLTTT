package com.edulanguage.dao;

import com.edulanguage.entity.Branch;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) cho Chi nhánh.
 * Service gọi DAO; DAO gọi Repository.
 */
public interface BranchDao {

    List<Branch> findAll();
    Optional<Branch> findById(Long id);
    Branch save(Branch branch);
    void deleteById(Long id);
}

