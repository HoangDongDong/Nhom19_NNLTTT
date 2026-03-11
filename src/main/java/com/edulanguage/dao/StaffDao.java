package com.edulanguage.dao;

import com.edulanguage.entity.Staff;

import java.util.List;
import java.util.Optional;

/**
 * DAO (Data Access Object) cho Nhân viên.
 */
public interface StaffDao {

    List<Staff> findAll();
    Optional<Staff> findById(Long id);
    Staff save(Staff staff);
    void deleteById(Long id);
}

