package com.edulanguage.service;

import com.edulanguage.entity.Invoice;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {

    List<Invoice> findAll();
    Optional<Invoice> findById(Long id);
}
