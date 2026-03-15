package com.edulanguage.service.impl;

import com.edulanguage.dao.EnrollmentDao;
import com.edulanguage.dao.InvoiceDao;
import com.edulanguage.dao.PaymentDao;
import com.edulanguage.entity.Enrollment;
import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.Payment;
import com.edulanguage.entity.enums.PaymentMethod;
import com.edulanguage.service.FinanceService;
import com.edulanguage.service.PromoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FinanceServiceImpl implements FinanceService {

    private static final Logger log = LoggerFactory.getLogger(FinanceServiceImpl.class);

    private final InvoiceDao invoiceDao;
    private final PaymentDao paymentDao;
    private final EnrollmentDao enrollmentDao;
    private final PromoService promoService;

    public FinanceServiceImpl(InvoiceDao invoiceDao, PaymentDao paymentDao, EnrollmentDao enrollmentDao, PromoService promoService) {
        this.invoiceDao = invoiceDao;
        this.paymentDao = paymentDao;
        this.enrollmentDao = enrollmentDao;
        this.promoService = promoService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findAllInvoices() {
        return invoiceDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findInvoiceById(Long id) {
        return invoiceDao.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> findInvoiceByIdForPrint(Long id) {
        return invoiceDao.findById(id).map(inv -> {
            if (inv.getStudent() != null) inv.getStudent().getFullName();
            if (inv.getEnrollment() != null) {
                if (inv.getEnrollment().getClazz() != null) {
                    inv.getEnrollment().getClazz().getClassName();
                    if (inv.getEnrollment().getClazz().getCourse() != null) {
                        inv.getEnrollment().getClazz().getCourse().getCourseName();
                        inv.getEnrollment().getClazz().getCourse().getFee();
                    }
                }
            }
            return inv;
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findUnpaidInvoices() {
        return invoiceDao.findByStatus("UNPAID");
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> findInvoicesByStudentId(Long studentId) {
        return invoiceDao.findByStudentId(studentId);
    }

    @Override
    public BigDecimal calculateDiscountAmount(BigDecimal originalFee, String discountCode) {
        if (discountCode == null || discountCode.isBlank()) return BigDecimal.ZERO;
        try {
            return promoService.calculateDiscount(discountCode, originalFee);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Override
    @Transactional
    public void processPayment(Long invoiceId, BigDecimal amount, PaymentMethod paymentMethod, String discountCode) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền thanh toán phải lớn hơn 0");
        }

        Invoice invoice = invoiceDao.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hóa đơn!"));

        if (invoice.getEnrollment() == null) {
            throw new IllegalArgumentException("Hóa đơn chưa liên kết với ghi danh. Vui lòng kiểm tra dữ liệu.");
        }

        if ("PAID".equals(invoice.getStatus())) {
            throw new IllegalArgumentException("Hóa đơn đã được thanh toán đầy đủ!");
        }

        BigDecimal discountAmount = BigDecimal.ZERO;
        if (discountCode != null && !discountCode.isBlank()) {
            discountAmount = promoService.calculateDiscount(discountCode, invoice.getTotalAmount());
            invoice.setAppliedPromoCode(discountCode.trim().toUpperCase());
            invoice.setPromoDiscountAmount(discountAmount);
        }
        BigDecimal amountToPay = invoice.getTotalAmount().subtract(discountAmount);

        List<Payment> pastPayments = paymentDao.findByEnrollmentId(invoice.getEnrollment().getId());
        BigDecimal totalPaid = pastPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotalPaid = totalPaid.add(amount);

        Payment payment = new Payment();
        payment.setStudent(invoice.getStudent());
        payment.setEnrollment(invoice.getEnrollment());
        payment.setAmount(amount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("COMPLETED");
        Payment saved = paymentDao.save(payment);
        log.info("Payment saved: id={}, enrollmentId={}, amount={}", saved.getId(), invoice.getEnrollment().getId(), amount);

        if (newTotalPaid.compareTo(amountToPay) >= 0) {
            invoice.setStatus("PAID");
            Enrollment enrollment = invoice.getEnrollment();
            enrollment.setStatus("ACTIVE");
            enrollmentDao.save(enrollment);
        } else {
            invoice.setStatus("PARTIAL");
        }

        if (discountCode != null && !discountCode.isBlank()) {
            promoService.incrementUsage(discountCode);
        }
        invoiceDao.save(invoice);
    }

    @Override
    public List<Invoice> findPendingConfirmInvoices() {
        return invoiceDao.findByStatus("PENDING_CONFIRM");
    }

    @Override
    @Transactional
    public void submitPaymentRequest(Long invoiceId, BigDecimal amount, PaymentMethod paymentMethod, String discountCode) {
        Invoice invoice = invoiceDao.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hóa đơn!"));

        if (!"UNPAID".equals(invoice.getStatus()) && !"REJECTED".equals(invoice.getStatus())) {
            throw new IllegalArgumentException("Hóa đơn này không ở trạng thái chờ thanh toán!");
        }

        BigDecimal originalAmount = invoice.getTotalAmount();
        BigDecimal discountAmount = BigDecimal.ZERO;

        // Áp dụng mã khuyến mại nếu có
        if (discountCode != null && !discountCode.isBlank()) {
            discountAmount = promoService.calculateDiscount(discountCode, originalAmount);
            invoice.setAppliedPromoCode(discountCode.trim().toUpperCase());
            invoice.setPromoDiscountAmount(discountAmount);
        }

        BigDecimal finalAmount = originalAmount.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // Tạo Payment với trạng thái PENDING
        Payment payment = new Payment();
        payment.setStudent(invoice.getStudent());
        payment.setEnrollment(invoice.getEnrollment());
        payment.setAmount(finalAmount);
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod(paymentMethod);
        payment.setStatus("PENDING");
        
        paymentDao.save(payment);

        // Cập nhật Invoice thành PENDING_CONFIRM
        invoice.setStatus("PENDING_CONFIRM");
        invoiceDao.save(invoice);

        // Tăng số lượt dùng của mã khuyến mại
        if (discountCode != null && !discountCode.isBlank()) {
            promoService.incrementUsage(discountCode);
        }
    }

    @Override
    @Transactional
    public void confirmPayment(Long invoiceId, boolean isApproved) {
        Invoice invoice = invoiceDao.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Hóa đơn!"));

        if (!"PENDING_CONFIRM".equals(invoice.getStatus())) {
            throw new IllegalArgumentException("Hóa đơn không ở trạng thái chờ xác nhận!");
        }

        // Tìm payment PENDING gần nhất của invoice này
        List<Payment> payments = paymentDao.findByEnrollmentId(invoice.getEnrollment().getId());
        Payment pendingPayment = payments.stream()
                .filter(p -> "PENDING".equals(p.getStatus()))
                .max((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt()))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao dịch chờ duyệt!"));

        if (isApproved) {
            pendingPayment.setStatus("COMPLETED");
            paymentDao.save(pendingPayment);

            invoice.setStatus("PAID");
            invoiceDao.save(invoice);

            Enrollment enrollment = invoice.getEnrollment();
            enrollment.setStatus("ACTIVE");
            enrollmentDao.save(enrollment);
        } else {
            pendingPayment.setStatus("REJECTED");
            paymentDao.save(pendingPayment);

            invoice.setStatus("UNPAID");
            invoiceDao.save(invoice);
        }
    }
}
