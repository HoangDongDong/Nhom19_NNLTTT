package com.edulanguage.dao;

import com.edulanguage.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceDao {
    List<Invoice> findAll();
    Optional<Invoice> findById(Long id);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByStudentId(Long studentId);
    Invoice save(Invoice invoice);
    void deleteById(Long id);
}
