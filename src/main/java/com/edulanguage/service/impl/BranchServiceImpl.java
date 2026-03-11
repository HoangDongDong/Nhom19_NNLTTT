package com.edulanguage.service.impl;

import com.edulanguage.dao.BranchDao;
import com.edulanguage.entity.Branch;
import com.edulanguage.service.BranchService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchDao branchDao;

    public BranchServiceImpl(BranchDao branchDao) {
        this.branchDao = branchDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Branch> findAll() {
        return branchDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Branch> findById(Long id) {
        return branchDao.findById(id);
    }

    @Override
    @Transactional
    public Branch save(Branch branch) {
        return branchDao.save(branch);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        branchDao.deleteById(id);
    }
}
