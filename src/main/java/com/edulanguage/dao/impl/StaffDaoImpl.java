package com.edulanguage.dao.impl;

import com.edulanguage.dao.StaffDao;
import com.edulanguage.entity.Staff;
import com.edulanguage.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class StaffDaoImpl implements StaffDao {

    private final StaffRepository staffRepository;

    public StaffDaoImpl(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public List<Staff> findAll() {
        return staffRepository.findAll();
    }

    @Override
    public Optional<Staff> findById(Long id) {
        return staffRepository.findById(id);
    }

    @Override
    public Staff save(Staff staff) {
        return staffRepository.save(staff);
    }

    @Override
    public void deleteById(Long id) {
        staffRepository.deleteById(id);
    }
}

