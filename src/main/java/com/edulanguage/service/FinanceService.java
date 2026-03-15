package com.edulanguage.service;

import com.edulanguage.entity.Invoice;
import com.edulanguage.entity.enums.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FinanceService {

    List<Invoice> findAllInvoices();
    Optional<Invoice> findInvoiceById(Long id);
    List<Invoice> findUnpaidInvoices();
    List<Invoice> findInvoicesByStudentId(Long studentId);
    
    /**
     * Tính toán số tiền được giảm tương ứng với mã giảm giá (áp dụng trên số tiền gốc)
     */
    BigDecimal calculateDiscountAmount(BigDecimal originalFee, String discountCode);

    /**
     * Nộp tiền trực tiếp (dành cho Desktop App)
     */
    void processPayment(Long invoiceId, BigDecimal amount, PaymentMethod paymentMethod, String discountCode);

    /**
     * Lấy danh sách các hóa đơn đang chờ xác nhận thanh toán (PENDING_CONFIRM)
     */
    List<Invoice> findPendingConfirmInvoices();

    /**
     * Học viên gửi yêu cầu thanh toán (tạo Payment PENDING, cập nhật Invoice thành PENDING_CONFIRM)
     */
    void submitPaymentRequest(Long invoiceId, BigDecimal amount, PaymentMethod paymentMethod, String discountCode);

    /**
     * Nhân viên xác nhận yêu cầu thanh toán (Duyệt/Từ chối)
     */
    void confirmPayment(Long invoiceId, boolean isApproved);
}
