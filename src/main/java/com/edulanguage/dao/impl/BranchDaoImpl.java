package com.edulanguage.dao.impl;

import com.edulanguage.dao.BranchDao;
import com.edulanguage.entity.Branch;
import com.edulanguage.repository.BranchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BranchDaoImpl implements BranchDao {

    private final BranchRepository branchRepository;

    public BranchDaoImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    @Override
    public Optional<Branch> findById(Long id) {
        return branchRepository.findById(id);
    }

    @Override
    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    public void deleteById(Long id) {
        branchRepository.deleteById(id);
    }
}

