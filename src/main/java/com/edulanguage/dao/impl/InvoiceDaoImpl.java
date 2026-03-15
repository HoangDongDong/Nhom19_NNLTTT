package com.edulanguage.dao.impl;

import com.edulanguage.dao.InvoiceDao;
import com.edulanguage.entity.Invoice;
import com.edulanguage.repository.InvoiceRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InvoiceDaoImpl implements InvoiceDao {

    private final InvoiceRepository invoiceRepository;

    public InvoiceDaoImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public List<Invoice> findByStatus(String status) {
        return invoiceRepository.findByStatus(status);
    }

    @Override
    public List<Invoice> findByStudentId(Long studentId) {
        return invoiceRepository.findByStudentId(studentId);
    }

    @Override
    public Invoice save(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public void deleteById(Long id) {
        invoiceRepository.deleteById(id);
    }
}
