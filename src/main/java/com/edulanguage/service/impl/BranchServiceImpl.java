package com.edulanguage.service.impl;

import com.edulanguage.entity.Branch;
import com.edulanguage.repository.BranchRepository;
import com.edulanguage.service.BranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    public BranchServiceImpl(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> findAll() {
        return branchRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Branch> findById(Long id) {
        return branchRepository.findById(id);
    }

    @Override
    @Transactional
    public Branch save(Branch branch) {
        return branchRepository.save(branch);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        branchRepository.deleteById(id);
    }
}
