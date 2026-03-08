package com.edulanguage.service;

import com.edulanguage.entity.Staff;

import java.util.List;
import java.util.Optional;

public interface StaffService {

    List<Staff> findAll();
    Optional<Staff> findById(Long id);
    Staff save(Staff staff);
    void deleteById(Long id);
}
