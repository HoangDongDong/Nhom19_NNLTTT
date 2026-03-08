package com.edulanguage.service.impl;

import com.edulanguage.entity.Staff;
import com.edulanguage.repository.StaffRepository;
import com.edulanguage.service.StaffService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StaffServiceImpl implements StaffService {

    private final StaffRepository staffRepository;

    public StaffServiceImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Staff> findById(Long id) {
        return staffRepository.findById(id);
    }

    @Override
    @Transactional
    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }
}
