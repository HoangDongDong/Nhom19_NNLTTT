package com.edulanguage.dao.impl;

import com.edulanguage.dao.PaymentDao;
import com.edulanguage.entity.Payment;
import com.edulanguage.repository.PaymentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentDaoImpl implements PaymentDao {

    private final PaymentRepository paymentRepository;

    public PaymentDaoImpl(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public List<Payment> findByEnrollmentId(Long enrollmentId) {
        return paymentRepository.findByEnrollmentId(enrollmentId);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }
}
