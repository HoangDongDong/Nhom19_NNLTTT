package com.edulanguage.repository;

import com.edulanguage.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    
    List<Invoice> findByStatus(String status);
    
    List<Invoice> findByStudentId(Long studentId);
}
