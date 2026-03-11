package com.edulanguage.service.impl;

import com.edulanguage.dao.StaffDao;
import com.edulanguage.entity.Staff;
import com.edulanguage.service.StaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffDao staffDao;

    public StaffServiceImpl(StaffDao staffDao) {
        this.staffDao = staffDao;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> findAll() {
        return staffDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> findById(Long id) {
        return staffDao.findById(id);
    }

    @Override
    @Transactional
    public Staff save(Staff staff) {
        return staffDao.save(staff);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        staffDao.deleteById(id);
    }
}
