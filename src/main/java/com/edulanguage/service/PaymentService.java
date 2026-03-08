package com.edulanguage.service;

import com.edulanguage.entity.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {

    List<Payment> findAll();
    Optional<Payment> findById(Long id);
}
