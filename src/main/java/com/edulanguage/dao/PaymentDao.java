package com.edulanguage.dao;

import com.edulanguage.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentDao {
    List<Payment> findAll();
    Optional<Payment> findById(Long id);
    List<Payment> findByEnrollmentId(Long enrollmentId);
    Payment save(Payment payment);
    void deleteById(Long id);
}
