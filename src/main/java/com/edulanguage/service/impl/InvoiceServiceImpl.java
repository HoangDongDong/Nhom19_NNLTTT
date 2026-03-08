package com.edulanguage.service.impl;

import com.edulanguage.entity.Invoice;
import com.edulanguage.repository.InvoiceRepository;
import com.edulanguage.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }
}
